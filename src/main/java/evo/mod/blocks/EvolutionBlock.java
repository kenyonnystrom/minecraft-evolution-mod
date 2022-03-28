package evo.mod.blocks;

import evo.mod.blockentity.EvolutionBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            //testing
            String s =String.valueOf(blockEntity.getGene2());
            player.sendMessage(new LiteralText(s), false);
            blockEntity.updateGene2(0.1f);
        }

        return ActionResult.SUCCESS;
    }
    //the code of this method will run everytime the block receives a random tick
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random){
        //cloneTree(world, pos, random,6);
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);
        blockEntity.die(world);
        //BlockPos new_block_location = clone(world, pos);
        //EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(new_block_location);
        //blockEntity.updateGene2(1.5f);
    }

    //ensure we are only cloning to valid locations
    // can amend to return block position of cloned block
    public void cloneTree(ServerWorld world, BlockPos pos, Random random,int height){
        int x_offset = (int) Math.round(random.nextGaussian()*height);
        int z_offset = (int) Math.round(random.nextGaussian()*height);
        BlockPos checkPos = new BlockPos(pos.getX() + x_offset, pos.getY() +height,pos.getZ() + z_offset);
        int status = 0;
        while (status < 2){
            BlockState blockState = world.getBlockState(checkPos);
            if (blockState.isAir()){
                status = 1;
                checkPos = checkPos.down();
            }
            else if ((status == 1) && (blockState.isOf(Blocks.FARMLAND) || blockState.isOf(Blocks.DIRT) || blockState.isOf(Blocks.COARSE_DIRT) || blockState.isOf(Blocks.PODZOL) || blockState.isOf(Blocks.GRASS_BLOCK))){
                status = 2;
            }
            else{
                status = 3;
            }
        }
        if (status == 2){
            BlockPos newTreePos = checkPos.up();
            BlockState newTree = this.getDefaultState();
            world.setBlockState(newTreePos, newTree);
            EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(newTreePos);
            blockEntity.updateGene2(1.5f);
        }
    }
    //link block entity and block
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EvolutionBlockEntity(pos, state);
    }



}

