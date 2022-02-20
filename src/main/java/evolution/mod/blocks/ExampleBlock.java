package evolution.mod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;


import java.util.Random;

public class ExampleBlock extends Block {
    public ExampleBlock(Settings settings) {
        //The actual parameters given to super() are used to initialize the inherited instance variables
        super(settings);
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random){

        //get position of current block
        int block_x = pos.getX();
        int block_y = pos.getY();
        int block_z = pos.getZ();

        //randomize offsets in nearby area
        //no y-coordinate just yet
        Random r = new Random();
        int x_offset = r.nextInt(30) - 15;
        int z_offset = r.nextInt(30) - 15;
        //place a random block somewhere nearby
        //spread custom block
        BlockState new_block = this.getDefaultState();
        BlockPos new_block_location = new BlockPos(block_x + x_offset, block_y,block_z + z_offset);
        world.setBlockState(new_block_location, new_block);

    }



}

