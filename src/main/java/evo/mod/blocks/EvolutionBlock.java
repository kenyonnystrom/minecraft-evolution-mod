package evo.mod.blocks;

import evo.mod.blockentity.EvolutionBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


import java.util.Random;

public class EvolutionBlock extends Block implements BlockEntityProvider {

    public EvolutionBlock(Settings settings) {
        //The actual parameters given to super() are used to initialize the inherited instance variables
        super(settings);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);
            String s =String.valueOf(blockEntity.gene2);
            player.sendMessage(new LiteralText(s), false);
            blockEntity.gene2 = blockEntity.gene2 + 10;
        }

        return ActionResult.SUCCESS;
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
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(new_block_location);
        blockEntity.gene2 = blockEntity.gene2 + 10;
    }

    //link block entity and block
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EvolutionBlockEntity(pos, state);
    }



}

