package eliarbogast.evolution.mod.mixins;

import eliarbogast.evolution.mod.AttackSheepGoal;
import eliarbogast.evolution.mod.SheepEntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class AttackSheepAndBreedMixin extends AnimalEntity {
    int killSheepCount = 0;
    protected AttackSheepAndBreedMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tryAttack", at = @At("TAIL"))
    public void tryAttack(Entity target, CallbackInfo info) {
        if(target instanceof SheepEntity){
            SheepEntity sheep = (SheepEntity) target;
            if(sheep.getHealth() < 1){
                killSheepCount++;
            }
        }
        if(killSheepCount > 2){
            this.setLoveTicks(50);
            this.canBreedWith(this);
        }
    }
}
