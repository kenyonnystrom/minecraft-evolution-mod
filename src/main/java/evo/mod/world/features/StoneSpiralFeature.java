package evo.mod.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class StoneSpiralFeature extends Feature<DefaultFeatureConfig> {
    public StoneSpiralFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos topPos = context.getWorld().getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, context.getOrigin());
        Direction offset = Direction.NORTH;

        for (int y = 0; y < 15; y++) {
            offset = offset.rotateYClockwise();
            context.getWorld().setBlockState(topPos.up(y).offset(offset), Blocks.STONE.getDefaultState(), 3);
        }

        return true;
    }



}
