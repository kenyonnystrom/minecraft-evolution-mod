//
// This mixin edits SheepEntity to allow it to get and feel temperature, as well as
// track its own wool type, determining its appearance and sensitivity to temperature.
//

package evo.mod.sheep.mixins;

import evo.mod.blocks.EvolutionBlock;
import evo.mod.features.ChatExt;
import evo.mod.features.DamageSourceExt;
import evo.mod.wolf.WolfAccess;

import evo.mod.sheep.EatTreeGoal;
import evo.mod.sheep.EvolvingSheepAccess;
import evo.mod.features.WoolType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.List;
import java.util.Random;

@Mixin(SheepEntity.class)
public abstract class EvolvingSheepEntity
extends AnimalEntity
implements EvolvingSheepAccess {
    private static final TrackedData<Byte> WOOL;
    private int realTick = 100;
    private int lifeTicks = 0;
    private final int breedThreshold = 2;
    private int foodCount;
    private int timeSinceEating = 5;
    private BlockPos treeTarget;
    public boolean hasTree;


    // Constructor
    public EvolvingSheepEntity(EntityType<? extends AnimalEntity> entityType, World world) { super(entityType, world); }

    //region WOOL-RELATED METHODS

    // Register data tracking
    static {
        WOOL = DataTracker.registerData(EvolvingSheepEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    // Insert Tree-eating goal in priority list
    @Inject(method="initGoals", at = @At("TAIL"))
    protected void initGoals(CallbackInfo ci) {
        this.goalSelector.add(3, new EatTreeGoal(this, 1.0));
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
    //region TREE-RELATED METHODS

    // When sheep eats anything, check food count for breeding threshold or reset
    private void onEating() {
        this.foodCount++;
        // If sheep has eaten enough food, go into love mode
        if (this.foodCount == this.breedThreshold) {
            if (!this.world.isClient) {
                setLoveTicks(60000);
            }
        // If sheep has taken enough time after breeding, start eating trees again
        } else if (this.foodCount > 3) {
            this.foodCount = 0;
        }
        this.timeSinceEating = 0;
    }

    // When sheep finishes tree goal, check to make sure it actually ate then divert to eating method
    public void onEatingTree(boolean success) {
        this.hasTree = false;
        if (success) {
            this.onEating();
        }
    }

    // When sheep eat grass, divert to eating method before standard grass procedure
    @Inject(method="onEatingGrass", at = @At("HEAD"))
    public void onEatingGrass(CallbackInfo info) {
        if (!this.isBaby()) {
            this.onEating();
        }
    }

    // Called in mobTick; searches for an EvolvingTree in a given search vicinity, sets global and returns true if found
    private boolean findEvolvingTree(double searchDistance) {
        BlockPos blockPos = super.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; (double) i <= searchDistance; i = i > 0 ? -i : 1 - i) {
            for (int j = 0; (double) j < searchDistance; ++j) {
                for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        mutable.set(blockPos, k, i - 1, l);
                        // Find position within distance that has an uneaten evolving tree, and prevent recurring pathfinding failures
                        if (blockPos.isWithinDistance(mutable, searchDistance) && (this.world.getBlockState(mutable).getBlock() instanceof EvolutionBlock) &&
                                (this.world.getBlockState(mutable).get(EvolutionBlock.STAGE) < 6) && mutable != this.treeTarget) {
                            this.treeTarget = mutable.toImmutable();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Accessor method that allows goals to get updated status of tree target
    public boolean updateTreeTarget() {
        // Make sure tree is still there
        if (this.hasTree && this.world.getBlockState(this.treeTarget).getBlock() instanceof EvolutionBlock && this.world.getBlockState(this.treeTarget).get(EvolutionBlock.STAGE) < 6) {
            return true;
        } else {
            this.hasTree = false;
            return false;
        }
    }

    // Accessor method that allows goals to get tree target
    public BlockPos getTreeTarget() { return this.treeTarget; }

    //endregion
    //region LIFE-RELATED METHODS

    // Called on death, currently relatively pointless but could be used for death messages if desired
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        try {
            DamageSourceExt s = (DamageSourceExt) source;
            // Uncomment the following line if you want sheep death messages
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
            if (lifeTicks > 40 && !this.hasCustomName()) {
                this.damage(DamageSourceExt.OLD_AGE, 20F);
            }
        }
    }

    // Called when sheep age (on tick), determines if wolves should spawn or not
    // Could be changed to work off of chunk tick instead
    private void checkWolfThreshold(ServerWorld world) {
        // create box around sheep to check
        BlockPos pos = super.getBlockPos();
        int sheepX = pos.getX();
        int sheepY = pos.getY();
        int sheepZ = pos.getZ();
        Box areaAroundSheep = new Box((sheepX-20), (sheepY-20), (sheepZ-20), (sheepX + 20), (sheepY + 20), (sheepZ + 20));

        //get list of all entities of a certain type
        List<SheepEntity> listOfEnts = world.getEntitiesByType(EntityType.SHEEP, areaAroundSheep, EntityPredicates.VALID_ENTITY);

        //get length of list
        int numSheep = listOfEnts.size();

        //check against threshold
        int upperThreshold = 15;
        if (numSheep >= upperThreshold){
            //spawn a wolf
            WolfEntity wolfEntity = (WolfEntity)EntityType.WOLF.create(world);
            assert wolfEntity != null;
            wolfEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            world.spawnEntityAndPassengers(wolfEntity);
            world.sendEntityStatus(this, (byte)18);
            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }
            ((WolfAccess)wolfEntity).setDespawnBool(true);
        }
    }

    // Extension of mobTick in superclass, ticks every 0.2 seconds unadjusted
    @Inject(method="mobTick", at = @At("HEAD"))
    public void mobTick(CallbackInfo ci) {
        // Reduce tick frequency by a factor specified in construction to reduce unnecessary load
        this.realTick--;
        if (this.realTick == 0) {
            this.realTick = 100;

            this.feelTemperature();
            this.increaseAge();
            this.checkWolfThreshold((ServerWorld) world);
            // If sheep is not currently pathfinding and needs to eat more to breed, search for viable tree
            if (!this.hasTree && this.foodCount < this.breedThreshold) {
                if (!this.findEvolvingTree(timeSinceEating)) {
                    timeSinceEating++;
                } else {
                    this.hasTree = true;
                }
            }
        }
    }
    //endregion
}
