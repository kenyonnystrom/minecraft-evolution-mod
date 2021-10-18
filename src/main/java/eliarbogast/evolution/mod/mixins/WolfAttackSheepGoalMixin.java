package eliarbogast.evolution.mod.mixins;

import eliarbogast.evolution.mod.SheepEntityExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public class WolfAttackSheepGoalMixin extends WolfEntity {

    public WolfAttackSheepGoalMixin(EntityType<? extends WolfEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="initGoals", at = @At("HEAD"))
    public void initGoals(CallbackInfo info) {
        System.out.println("initGoals");
        this.goalSelector.add(4, new AttackSheepGoal(this, 1.0D, true));
    }
}
