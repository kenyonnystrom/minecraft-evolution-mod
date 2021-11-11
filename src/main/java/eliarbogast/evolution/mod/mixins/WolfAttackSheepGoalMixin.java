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
/**
 * @author
 * Silas Zhao
 */
@Mixin(WolfEntity.class)
public abstract class WolfAttackSheepGoalMixin extends AnimalEntity {

    protected WolfAttackSheepGoalMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="initGoals", at = @At("HEAD"))
    //InitGoal is a void function, so use CallbackInfo. More details in ReadMe Remark session.
    public void initGoals(CallbackInfo info) {
        System.out.println("initGoals");
        //lower number have more priority. I used 4 because I want to use this goal to replace MeleeAttackGoal when meeting a sheep.
        this.goalSelector.add(4, new AttackSheepGoal(this, 1.0D, true));
    }
}