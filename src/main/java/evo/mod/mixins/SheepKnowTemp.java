package evo.mod.mixins;

import evo.mod.KnowTemp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepEntity.class)
public abstract class SheepKnowTemp
extends AnimalEntity
implements KnowTemp {

    public SheepKnowTemp(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public BiomeAccess bAccess;

    public void getTemp() {
        BlockPos pos = super.getBlockPos();
        Biome b = bAccess.getBiome(pos);
        float temp = b.getTemperature();
    }

}
