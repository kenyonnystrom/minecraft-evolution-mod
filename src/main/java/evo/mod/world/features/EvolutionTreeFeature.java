package evo.mod.world.features;

import com.mojang.serialization.Codec;
import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.evo;
import evo.mod.helpers.TreeGrower;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import static evo.mod.blocks.EvolutionBlock.STAGE;
import static net.minecraft.block.LeavesBlock.PERSISTENT;

public class EvolutionTreeFeature extends Feature<DefaultFeatureConfig> {
    private static final BlockStatePredicate CAN_GENERATE_GRASS;
    private static final BlockStatePredicate CAN_GENERATE_PODZOL;
    private static final BlockStatePredicate CAN_GENERATE_SAND;

    public EvolutionTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {

        //Block position of feature
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        //Can we place a tree here?
        if(!CAN_GENERATE_GRASS.test(world.getBlockState(blockPos.down())) && !CAN_GENERATE_SAND.test(world.getBlockState(blockPos.down())) && !CAN_GENERATE_PODZOL.test(world.getBlockState(blockPos.down()))){
            return false;
        }
        //Base of tree
        world.setBlockState(blockPos, evo.EVOLUTION_BLOCK.getDefaultState(),2);//.with(STAGE, 2);
        //Fetch associated Block Entity
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(blockPos);
        world.setBlockState(blockPos, evo.EVOLUTION_BLOCK.getDefaultState().with(STAGE,blockEntity.get_STAGE()),2);

        BlockState wood_block = blockEntity.get_Wood_Block();
        BlockState leaves_block = blockEntity.get_Leaf_Block();

        //Grow the tree
        for(int i = 1; i <= 3; ++i) {
            TreeGrower.generate_Trunk(world,blockPos,i, wood_block);
            TreeGrower.generate_Small_Leaves(world, blockPos,i, leaves_block.with(PERSISTENT, true));

            blockEntity.increment_Age();
            blockEntity.increment_Height();
        }
        return true;
    }

    static {
        CAN_GENERATE_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS_BLOCK);
        CAN_GENERATE_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
        CAN_GENERATE_PODZOL = BlockStatePredicate.forBlock(Blocks.PODZOL);//forBlock(Blocks.COARSE_DIRT).forBlock(Blocks.DIRT).forBlock(Blocks.SNOW_BLOCK);
    }
}
