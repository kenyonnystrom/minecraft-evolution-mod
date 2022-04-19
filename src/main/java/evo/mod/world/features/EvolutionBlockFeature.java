package evo.mod.world.features;

import com.mojang.serialization.Codec;
import evo.mod.evo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EvolutionBlockFeature extends Feature<DefaultFeatureConfig> {
    public EvolutionBlockFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        context.getWorld().setBlockState(blockPos, evo.EVOLUTION_BLOCK.getDefaultState(), 3);
        return true;
    }
}
