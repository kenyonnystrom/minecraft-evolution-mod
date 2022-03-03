package evo.mod.mixins;

import evo.mod.DamageSourceExt;

import evo.mod.WoolType;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.BlockPos;

import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.Random;

@Mixin(SheepEntity.class)
public abstract class EvolvingSheepEntity
extends AnimalEntity {
    private int realTick = 100;
    private int lifeTicks = 0;
    private static final TrackedData<Byte> WOOL;

    // Constructor
    public EvolvingSheepEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    //region WOOL-RELATED METHODS

    // Register data tracking
    static {
        WOOL = DataTracker.registerData(EvolvingSheepEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    // Adding to sheepEntity data tracker initialization (which only tracks color), begin tracking Wool
    @Inject(method="initDataTracker", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(WOOL, (byte)0);
    }

    // Sets wool type for this sheep (more info in WoolType.java) and adds it to tracker
    public void setWool(WoolType wool) {
        byte b = this.dataTracker.get(WOOL);
        this.dataTracker.set(WOOL, (byte)(b & 240 | wool.getId() & 3));
    }

    // Gets wool type of this sheep from data tracker, returning WoolType
    public WoolType getWool() {
        return WoolType.byId(this.dataTracker.get(WOOL) & 3);
    }

    // If sheep is spawned in rather than birthed, randomly chooses its starting wool state
    public WoolType generateDefaultWool(Random random) {
        int i = random.nextInt(4);
        return WoolType.byId(i);
    }

    // Save wool and age data to nbt so that it is saved when exiting Minecraft world
    @Inject(method="writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putByte("Wool", (byte)this.getWool().getId());
        nbt.putInt("Age", lifeTicks);
    }

    // Reinstate saved data upon reloading Minecraft world
    @Inject(method="readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setWool(WoolType.byId(nbt.getByte("Wool")));
        this.lifeTicks = nbt.getInt("Age");
    }

    // Add random generation of wool size to the spawning process
    @Inject(method="initialize", at = @At("HEAD"))
    public void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        this.setWool(generateDefaultWool(world.getRandom()));
    }
    //endregion
    //region TEMPERATURE-RELATED METHODS

    // Get temp of this sheep's current location
    private float getTemp() {
        BlockPos pos = super.getBlockPos();
        Biome biome = world.getBiome(pos);
        return biome.getTemperature();
    }

    // Get temperature, and take damage based on this sheep's wool type
    private void feelTemperature() {
        float currTemp = this.getTemp();
        System.out.println(currTemp);
        switch(this.getWool()) {
            case NO_WOOL:
                if (currTemp < 0.5F) {
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                }
                break;
            case THIN_WOOL:
                if (currTemp < 0.3F) {
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                } else if (currTemp > 1.5F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
            case STD_WOOL:
                if (currTemp < 0F) {
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                } else if (currTemp > 0.8F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
            case THICK_WOOL:
                if (currTemp > 0.5F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
        }
    }
    //endregion
    //region LIFE-RELATED METHODS

    // Have a kid. Currently asexual
    private void giveBirth() {
        // Asexually reproduce
        PassiveEntity kid = this.createChild((ServerWorld) world, this);
        if (kid != null) {
            kid.setBaby(true);
            // Boring Minecraft stuff to spawn in kid
            kid.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            ((ServerWorld) world).spawnEntityAndPassengers(kid);
            world.sendEntityStatus(this, (byte) 18);
            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }

            // Inherit parent wool
            EvolvingSheepEntity sheepKid = (EvolvingSheepEntity) kid;
            //80% chance of getting parent's wool
            //20% chance to get one step above or below
            //within that 50/50 for above or below
            int i = random.nextInt(10);
            if (i == 0 && this.getWool() != WoolType.NO_WOOL){
                //one below
                sheepKid.setWool(WoolType.byId(this.getWool().getId() - 1));
            }
            else if (i == 1 && this.getWool() != WoolType.THICK_WOOL){
                //one above
                sheepKid.setWool(WoolType.byId(this.getWool().getId() + 1));
            }
            else {
                sheepKid.setWool(this.getWool());
            }
        }
    }

    // Called with tick; ages sheep and determines when they reproduce/die of old age
    private void increaseAge() {
        lifeTicks++;
        System.out.println(lifeTicks);
        int p = random.nextInt(100);
        if (p <= 20) {
            this.damage(DamageSourceExt.BITE, 2.0F);
        }
        if (!this.isBaby()){
            int i = random.nextInt(100);
            if (i <= 15) {
                this.giveBirth();
            }
        }
    }

    // Extension of mobTick in superclass, ticks every 0.2 seconds unadjusted
    @Inject(method="mobTick", at = @At("HEAD"))
    public void mobTick(CallbackInfo ci) {
        // Reduce tick frequency by a factor specified in construction to reduce unnecessary load
        realTick--;
        if (realTick == 0) {
            realTick = 100;

            this.feelTemperature();
            this.increaseAge();
            System.out.println(this.getWool().getName());
        }
    }
    //endregion
}
