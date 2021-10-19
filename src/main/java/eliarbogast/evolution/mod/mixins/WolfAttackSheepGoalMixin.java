package eliarbogast.evolution.mod.mixins;

import eliarbogast.evolution.mod.SheepEntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    @Inject(method="tryAttack", at = @At("RETURN"), cancellable = true)
    public void tryAttack(Entity target, CallbackInfo info, CallbackInfoReturnable<Boolean> cir){
        boolean bl = cir.getReturnValue();
        System.out.println("in tryAttack");
        if(bl){
            if(target instanceof SheepEntity){
                SheepEntityExt sheep = (SheepEntityExt)target;
                double difference = sheep.getDifference();
                if(Math.random() < difference){
                    cir.setReturnValue(true);
                }else{
                    System.out.println("didn't attack" + ((SheepEntity)sheep).getColor().getName() + " sheep.");
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
