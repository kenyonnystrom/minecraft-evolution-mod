package eliarbogast.evolution.mod.mixins;

import eliarbogast.evolution.mod.SheepEntityExt;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

import static eliarbogast.evolution.mod.utils.DyeUtils.colorRingOfDyeColors;
import static eliarbogast.evolution.mod.utils.DyeUtils.colorRingOfMaterials;
@Mixin(SheepEntity.class)
public abstract class SheepLifeCycleMixin extends AnimalEntity implements SheepEntityExt,SheepEntityInvoker{
    public SheepLifeCycleMixin(EntityType<? extends SheepEntity> entityType, World world) {
        super(entityType, world);
    }

    //@Inject(method="onEatingGrass", at = @At("HEAD"))
    public void onEatingGrass(CallbackInfo info) {
        System.out.println("onEatingGrass");
        ((SheepEntityExt)this).killSheepBySurroundingColor();
    }

    public MaterialColor getSurroundingColor() {
        BlockPos blockPos = this.getBlockPos();
        List<MaterialColor> colorList = new ArrayList<>();
        //search radius 5*5
        for(int i = -2; i < 3; i++){
            for(int j = -2; j < 3; j++){
                Material material = this.world.getBlockState(blockPos.add(i,j,0)).getMaterial();
                if(material.isSolid()) {
                    colorList.add(material.getColor());
                }
            }
        }
        //get the most frequent color.
        if(colorList.size()!=0){
            Map<MaterialColor,Long> map = colorList.stream().collect(Collectors.groupingBy(materialColor -> materialColor, Collectors.counting()));
            MaterialColor key = null;
            int count = 0;
            for(Map.Entry<MaterialColor,Long> item:map.entrySet()){
                if(item.getValue().intValue() > count){
                    key = item.getKey();
                }
            }
            //System.out.println(key.id);
            return key;
        }
        return null;
    }

    public void killSheepBySurroundingColor(){
        if(Random(getDifference())){
            System.out.println("dead shee color: " + ((SheepEntityInvoker)this).getSheepColor().name());
            this.onDeath(DamageSource.OUT_OF_WORLD);
        }
    }

    private Boolean Random(double d){
        double rand = Math.random();
        return rand <= d;
    }

    public double getDifference(){
        //System.out.println("on kill Sheep by surrounding color");
        int colorOfSurroundings = colorRingOfMaterials.get(((SheepEntityExt)this).getSurroundingColor().id);//((SheepEntityExt)this).getSurroundingColor().color;
        System.out.println("((SheepEntityExt)this).getSurroundingColor().color: "+((SheepEntityExt)this).getSurroundingColor().id);
        //System.out.println("on color of surroundings");
        int sheepColor = colorRingOfDyeColors.get(((SheepEntityInvoker)this).getSheepColor().name().toLowerCase());//((SheepEntityInvoker)this).getSheepColor().name()
        System.out.println("((SheepEntityInvoker)this).getSheepColor().name(): " + ((SheepEntityInvoker)this).getSheepColor().name());
        double difference = 0;
        /* compare the difference by the contrast of the sheep, max = 8 */
        if(colorOfSurroundings > sheepColor){
            difference = Math.min(colorOfSurroundings - sheepColor, sheepColor + 15 - colorOfSurroundings);
        }else{
            difference = Math.min(sheepColor - colorOfSurroundings, colorOfSurroundings + 15 - sheepColor);
        }//mutation amount
        //4% to create child after eating grass.
        //max contrast is 8
        difference = isBaby() ? difference / 2: difference;
        difference = difference < 2? (difference / 25):(difference / 12);
        System.out.println("difference: " + difference);//adding comment here and readME.
        return difference;
    }
}
