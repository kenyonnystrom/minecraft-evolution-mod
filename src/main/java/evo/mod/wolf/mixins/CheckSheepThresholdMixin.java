package evo.mod.wolf.mixins;

import evo.mod.features.DamageSourceExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WolfEntity.class)
public abstract class CheckSheepThresholdMixin extends AnimalEntity {
    // Constructor
    public CheckSheepThresholdMixin(EntityType<? extends WolfEntity> entityType, World world) {
        super(entityType, world);
    }

    //This checks if there are less than 4 sheep in the area, if so it will kill the wolves to make sure some sheep survive
    public void CheckSheepThreshold(ServerWorld world) {
        //System.out.println("in sheep check (wolf)");
        // create box around sheep to check
        BlockPos pos = super.getBlockPos();
        int wolfX = pos.getX();
        int wolfY = pos.getY();
        int wolfZ = pos.getZ();
        Box areaAroundWolf = new Box((wolfX - 20), (wolfY - 20), (wolfZ - 20), (wolfX + 20), (wolfY + 20), (wolfZ + 20));

        //get list of all entities of a certain type
        List<SheepEntity> listOfEnts = world.getEntitiesByType(EntityType.SHEEP, areaAroundWolf, EntityPredicates.VALID_ENTITY);

        //get length of list
        int numSheep = listOfEnts.size();
        //System.out.printf("number of sheep: %d", numSheep);

        //check against threshold
        int lowerThreshold = 4;
        if (numSheep <= lowerThreshold) {
            //despawn wolf
            this.damage(DamageSourceExt.STARVATION, 10F);
        }
    }

    //This is used to avoid casting issues from ClientWorld (normal tick) and ServerWorld (what the checkSheepThreshold function needs)
    protected void mobTick() {
        super.mobTick();
        CheckSheepThreshold((ServerWorld) world);
    }

}
