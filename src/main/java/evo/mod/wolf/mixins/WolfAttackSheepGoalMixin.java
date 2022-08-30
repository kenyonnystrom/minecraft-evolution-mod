package evo.mod.wolf.mixins;

import evo.mod.wolf.AttackSheepGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
Based on code from Silas Zhao
Sets the new AttackSheepGoal as a higher priority
than normal melee attack.
*/

@Mixin(WolfEntity.class)
public abstract class WolfAttackSheepGoalMixin extends AnimalEntity {

    protected WolfAttackSheepGoalMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="initGoals", at = @At("HEAD"))
    //InitGoal is a void function, so use CallbackInfo. More details in ReadMe Remark session.
    public void initGoals(CallbackInfo info) {
        //System.out.println("initGoals");
        //lower number have more priority. I used 4 because I want to use this goal to replace MeleeAttackGoal when meeting a sheep.
        this.goalSelector.add(4, new AttackSheepGoal(this, 1.0D, true));
        //System.out.println("successful goal init");
    }
}