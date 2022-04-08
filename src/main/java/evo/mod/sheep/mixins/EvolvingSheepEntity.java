//
// This mixin edits SheepEntity to allow it to get and feel temperature, as well as
// track its own wool type, determining its appearance and sensitivity to temperature.
//

package evo.mod.sheep.mixins;

import evo.mod.features.ChatExt;
import evo.mod.features.DamageSourceExt;

import evo.mod.sheep.EvolvingSheepAccess;
import evo.mod.features.WoolType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
extends AnimalEntity
implements EvolvingSheepAccess {
    private int realTick = 100;
    private int lifeTicks = 0;
    private int grassCount;
    private static final TrackedData<Byte> WOOL;

    // Constructor
    public EvolvingSheepEntity(EntityType<? extends AnimalEntity> entityType, World world) { super(entityType, world); }

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
        this.dataTracker.set(WOOL, (byte)(b & 240 | wool.getId() & 15));
    }

    // Overwrite Entity class method so that the default name can be determined in the lang file
    protected Text getDefaultName() {
        return new TranslatableText("entity.minecraft.sheep." + this.getWool().getName());
    }

    // Gets wool type of this sheep from data tracker, returning WoolType
    public WoolType getWool() {
        return WoolType.byId(this.dataTracker.get(WOOL) & 15);
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

    // Don't allow no_wool sheep to be sheared
    @Inject(method = "isShearable()Z", at = @At("RETURN"), cancellable = true)
    private void isShearable(CallbackInfoReturnable<Boolean> cir) {
        if (this.getWool() == WoolType.NO_WOOL)
            cir.setReturnValue(false);
    }

    // Wool size increases wool output (still randomized)
    @ModifyVariable(method = "sheared", at = @At("STORE"), ordinal = 0)
    public int sheared(int i) {
        return i * this.getWool().getId();
    }

    //endregion
    //region TEMPERATURE-RELATED METHODS

    // Get temp of this sheep's current location
    private float getTemp() {
        BlockPos pos = super.getBlockPos();
        Biome biome = world.getBiome(pos);
        double temp = biome.getTemperature();
        double light = world.getLightLevel(pos);
        double altitude = pos.getY();
        // (Not 100% final) Full temperature calculation
        return (float) (40 * (temp + 0.5) + light - (altitude * 0.1));
    }

    // Get temperature, and take damage based on this sheep's wool type
    private void feelTemperature() {
        float currTemp = this.getTemp();
        System.out.printf("Temp: %f%n", currTemp);
        switch(this.getWool()) {
            case NO_WOOL:
                if (currTemp < 65F) {
                    // Death in five hits
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                }
                break;
            case THIN_WOOL:
                if (currTemp < 45F) {
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                } else if (currTemp > 100F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
            case STD_WOOL:
                if (currTemp < 10F) {
                    this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
                } else if (currTemp > 75F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
            case THICK_WOOL:
                if (currTemp > 55F) {
                    this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
                }
                break;
        }
    }
    //endregion
    //region INHERITANCE-RELATED METHODS

    // Injecting cannot accomplish the desired result (passing "genes" between EvolvingSheepEntities) without completely
    // overriding the breeding function in AnimalEntity, so this method does just that. The only differences in this
    // version of the method are an updated call to createChild in SheepEntity and a redirect to passGenes below.
    @Override
    public void breed(ServerWorld world, AnimalEntity other) {
        PassiveEntity passiveEntity = this.createChild(world, this);
        if (passiveEntity != null) {
            // Newly added section
            EvolvingSheepEntity kid = this.passGenes(passiveEntity, other);

            // Previously established section
            ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
            if (serverPlayerEntity == null && other.getLovingPlayer() != null) {
                serverPlayerEntity = other.getLovingPlayer();
            }
            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, other, kid);
            }
            this.setBreedingAge(6000);
            other.setBreedingAge(6000);
            this.resetLoveTicks();
            other.resetLoveTicks();
            kid.setBaby(true);
            kid.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            world.spawnEntityAndPassengers(kid);
            world.sendEntityStatus(this, (byte)18);
            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }
        }
    }

    // Called on the creation of a child through sexual reproduction; passes genes from parents with pseudo-simulated
    // evolutionary components such as recombination and mutation.
    private EvolvingSheepEntity passGenes(PassiveEntity newKid, PassiveEntity other) {
        EvolvingSheepEntity parentB = (EvolvingSheepEntity) other;
        EvolvingSheepEntity kid = (EvolvingSheepEntity) newKid;
        if (kid != null) {
            // Pass WoolType:
            // 20% chance of getting the exact same wool as one parent
            // 50% chance of getting something between them
            // 10% chance of getting something outside this range
            int pAW = this.getWool().getId();
            int pBW = parentB.getWool().getId();
            int diff = pAW - pBW;
            int kW;
            int i = random.nextInt(10);
            System.out.printf(" PARENT A: %d%n PARENT B: %d%n I: %d%n", pAW, pBW, i);
            if (i > 0){
                if (diff == 0) {
                    // If parents same, inherit same
                    kW = pAW;
                } else if (Math.abs(diff) == 1 || i < 5){
                    // Inherit same as one parent
                    if (random.nextInt(2) == 1) {
                        kW = pAW;
                    } else {
                        kW = pBW;
                    }
                } else if (Math.abs(diff) == 2){
                    // Inherit middle between two parents
                    kW = pAW - diff/2;
                } else {
                    // Inherit something in the middle (update if more wool states!)
                    kW = pAW - (diff/3 * (random.nextInt(2) + 1));
                }
            }
            else {
                if (Math.abs(diff) == 3) {
                    // Can't be outside range so just pick one (update if more wool states!)
                    kW = random.nextInt(4);
                }
                else {
                    // Inherit outside range of values between parents
                    int j = random.nextInt(3 - Math.abs(diff));
                    if (diff >= 0) {
                        if (j < pBW) {
                            kW = j;
                        } else {
                            kW = j + diff + 1;
                        }
                    } else {
                        if (j < pAW) {
                            kW = j;
                        } else {
                            kW = j - diff + 1;
                        }
                    }
                }
            }
            kid.setWool(WoolType.byId(kW));
        }
        return kid;
    }

    //endregion
    //region LIFE-RELATED METHODS

    @Inject(method="onEatingGrass", at = @At("HEAD"))
    public void onEatingGrass(CallbackInfo info) {
        this.grassCount++;
        if (grassCount >= 2) {
            if (!this.isBaby()) {
                if (!this.world.isClient) {
                    setLoveTicks(60000);
                }
            }
        }
    }

    public void onDeath(DamageSource source) {
        super.onDeath(source);
        try {
            DamageSourceExt s = (DamageSourceExt) source;
            // Comment out the following line if you are getting tired of sheep death messages
            ChatExt.sendText(s.getDeathMessage(this));
        } catch (Exception e){
            // Don't print anything
        }
    }

    // Called with tick; ages sheep and determines when they reproduce/die of old age
    private void increaseAge() {
        if (lifeTicks < 10) {
            lifeTicks ++;
            this.growUp(200);
        } else {
            lifeTicks += random.nextInt(2);
            if (lifeTicks > 15) {
                this.damage(DamageSourceExt.OLD_AGE, 20F);
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
