package evo.mod.wolf.mixins;

import evo.mod.features.DamageSourceExt;
import evo.mod.wolf.WolfAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
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
public abstract class CheckSheepThresholdMixin extends AnimalEntity implements WolfAccess {
    private static final TrackedData<Boolean> DESPAWNBOOL;

    // Constructor
    public CheckSheepThresholdMixin(EntityType<? extends WolfEntity> entityType, World world) {
        super(entityType, world);
    }

    // Register data tracking
    static {
        DESPAWNBOOL = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    // Adding to wolfEntity data tracker initialization, begin tracking Despawn boolean
    @Inject(method="initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(DESPAWNBOOL, (boolean)false);
    }

    // Sets despawn boolean of a wolf and adds it to tracker
    public void setDespawnBool(boolean bool) {
        this.dataTracker.set(DESPAWNBOOL, bool);
    }

    // Gets despawn boolean of a wolf from data tracker
    public boolean getDespawnBool() {
        return this.dataTracker.get(DESPAWNBOOL);
    }

    // Save despawn boolean to nbt so that it is saved when exiting Minecraft world
    @Inject(method="writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("DespawnBool", this.getDespawnBool());
    }

    // Reinstate saved data upon reloading Minecraft world
    @Inject(method="readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setDespawnBool(nbt.getBoolean("DespawnBool"));
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
        if (numSheep <= lowerThreshold && this.getDespawnBool()) {
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
