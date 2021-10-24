
package eliarbogast.evolution.mod.mixins;


        import eliarbogast.evolution.mod.EscapeFromWolfGoal;
        import eliarbogast.evolution.mod.SheepEntityExt;
        import net.minecraft.entity.EntityType;
        import net.minecraft.entity.passive.AnimalEntity;
        import net.minecraft.entity.passive.SheepEntity;
        import net.minecraft.world.World;
        import org.spongepowered.asm.mixin.Mixin;
        import org.spongepowered.asm.mixin.injection.At;
        import org.spongepowered.asm.mixin.injection.Inject;
        import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class EscapeFromWolfMixin extends AnimalEntity {

    protected EscapeFromWolfMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="initGoals", at = @At("HEAD"))
    public void initGoals(CallbackInfo info) {
        System.out.println("init sheep Goals");
        this.goalSelector.add(0, new EscapeFromWolfGoal(this, 1.7D));

    }
}
