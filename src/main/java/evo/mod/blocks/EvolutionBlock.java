package evo.mod.blocks;

import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.evo;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
    /*
    The code of this method will run everytime the EvolutionBlock is right-clicked
    Great for testing new features, debugging
    */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);
            //testing
            blockEntity.get_Temp();
            String s =String.valueOf(blockEntity.getGene2());
            player.sendMessage(new LiteralText(s), false);
            blockEntity.updateGene2(0.1f);
        }

        return ActionResult.SUCCESS;
    }

    /*
    The code of this method will run everytime the block receives a random tick
    Acts as a "driver" of Evolution, or use EntityTicking
    Mainly calls other methods - such as cloning the EvolutionBlock
    */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random){
        //cloneTree(world, pos, random,6);
        int action;
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);

        //should the tree die - not well adapted or too old
        if (blockEntity.get_TempDist() > 6 || blockEntity.get_Age() > 50){
            blockEntity.die(world);
        }
        else {
            blockEntity.increment_age();
            Random r = new Random();
            cloneTree(world, pos, r, 6);
        }
    }

    /*
    Clones EvolutionBlock at provided position to a nearby location
    Copies parent's genome - modifies it by specific updateGeneName BlockEntity methods
    */
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

            blockEntity.update_IdealTemp(0.8F + (random.nextFloat() * 0.4F));
        }
    }

    /*
    Links block to BlockEntity
    */
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EvolutionBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, evo.EVOLUTION_ENTITY, (world1, pos, state1, be) -> EvolutionBlockEntity.tick(world1, pos, state1, be));
    }



}

