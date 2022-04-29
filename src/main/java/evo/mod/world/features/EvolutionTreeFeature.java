package evo.mod.world.features;

import com.mojang.serialization.Codec;
import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.evo;
import evo.mod.helpers.TreeGrower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Iterator;

import static evo.mod.blocks.EvolutionBlock.STAGE;
import static net.minecraft.block.LeavesBlock.PERSISTENT;

public class EvolutionTreeFeature extends Feature<DefaultFeatureConfig> {
    private static final BlockStatePredicate CAN_GENERATE;

    public EvolutionTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        world.setBlockState(blockPos, evo.EVOLUTION_BLOCK.getDefaultState(),2);//.with(STAGE, 2);
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(blockPos);

        BlockState wood_block;
        BlockState leaves_block;

        //grow tree
        for(int i = 1; i <= 3; ++i) {
            TreeGrower.generate_Trunk(world,blockPos,i, Blocks.OAK_LOG.getDefaultState());
            TreeGrower.generate_Small_Leaves(world, blockPos,i, Blocks.OAK_LEAVES.getDefaultState().with(PERSISTENT, true));

            blockEntity.increment_Age();
            blockEntity.increment_Height();

        }
//        }

        return true;
    }

    static {
        CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.GRASS);
    }
}