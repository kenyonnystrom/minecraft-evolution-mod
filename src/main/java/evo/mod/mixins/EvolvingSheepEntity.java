package evo.mod.mixins;

import evo.mod.DamageSourceExt;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;

import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.BlockPos;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(SheepEntity.class)
public abstract class EvolvingSheepEntity
extends AnimalEntity {
    private int realTick = 100;
    private int lifeTicks = 0;

    //We can decide on range in testing
    //5 being avg, 0 being virtually no wool, 10 being super chunky
    //start with 2: 0 = no wool, 1 = normal wool, 2 = heavy wool
    private int woolDensity = 1;
    //will need to decide how parents pass density on and how kid recieves

    public EvolvingSheepEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // Get temp of current location
    private float getTemp() {
        BlockPos pos = super.getBlockPos();
        Biome biome = world.getBiome(pos);
        return biome.getTemperature();
    }

    // React to temperature.
    private void feelTemperature() {
        float currTemp = this.getTemp();
        if (currTemp < 0F && woolDensity != 2) {
            this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
        } else if (currTemp < 0.5F && woolDensity < 1) {
            this.damage(DamageSourceExt.HYPOTHERMIA, 1.5F);
        } else if (currTemp < 1F && woolDensity > 1) {
            this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
        } else {
            this.damage(DamageSourceExt.HEATSTROKE, 1.5F);
        }
    }

    // Have a kid. Currently asexual
    private void giveBirth() {
        // Asexually reproduce. Make this more randomized at some point
        PassiveEntity kid = this.createChild((ServerWorld) world, this);
        if (kid != null) {
            kid.setBaby(true);
            kid.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            ((ServerWorld) world).spawnEntityAndPassengers(kid);
            world.sendEntityStatus(this, (byte) 18);
            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
            }
        }
    }

    // Called with tick; ages sheep and determines when they reproduce/die of old age
    private void increaseAge() {
        lifeTicks--;
        System.out.println(lifeTicks);
        // With re-framed ticks, an old-age val of 50 seems to work well. Remove this comment after testing is done
        if (lifeTicks > 10) {
            // Die of old age
            this.onDeath(DamageSourceExt.OUT_OF_WORLD);
            this.kill();
        } else if (lifeTicks == 6 || lifeTicks == 4) {
            // 50-50 chance of reproducing
            if (Math.random() > 0.5) {
                this.giveBirth();
            }
        }
    }

    // Extension of mobTick in superclass, ticks every 0.2 seconds unadjusted
    public void mobTick() {
        // Reduce tick frequency by a factor specified in construction to reduce unnecessary load
        realTick--;
        if (realTick == 0) {
            realTick = 100;

            this.feelTemperature();
            this.increaseAge();
            super.mobTick();
        }
    }
}
