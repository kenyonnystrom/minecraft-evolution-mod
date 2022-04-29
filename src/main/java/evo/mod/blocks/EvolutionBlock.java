package evo.mod.blocks;

import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.evo;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import evo.mod.helpers.TreeGrower;
import org.jetbrains.annotations.Nullable;

import java.util.Random;


public class EvolutionBlock extends BlockWithEntity implements BlockEntityProvider {
    //public static final IntProperty AGE = IntProperty.of("age",0, 2);
    //public static final BooleanProperty GROWN = BooleanProperty.of("grown");

    //set up integer property that can have 12 different values 0-11, each of these is essentially a different blockstate
    //these are primarily for models/textures but are also used in places to keep track of the stage
    //for example if STAGE = 11, tree is already dead and will despawn in the next few random ticks
    /*
        "stage=0": oak_sapling
        "stage=1": acacia_log
        "stage=2": oak_log
        "stage=3": jungle_log
        "stage=4": dark_oak_log
        "stage=5": spruce_log
        "stage=6": stripped_acacia_log
        "stage=7": stripped_oak_log
        "stage=8": stripped_jungle_log
        "stage=9": stripped_dark_oak_log
        "stage=10": stripped_spruce_log
        "stage=11": dead_bush
    */
    public static final IntProperty STAGE = IntProperty.of("stage",0,11);

    public EvolutionBlock(Settings settings) {
        //The actual parameters given to super() are used to initialize the inherited instance variables
        super(settings);
        //EvolutionBlock is a sapling by default
        //setDefaultState(getStateManager().getDefaultState().with(GROWN, false));
        setDefaultState(getStateManager().getDefaultState().with(STAGE, 0));
    }

    /*
    Setting up GROWN property
    We need a BlockState to give our block a different appearance once the tree begins growing. This can be down with a property
    We register this property of the EvolutionBlock by overriding appendProperties, and then add the GROWN property
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        //builder.add(AGE);
        //builder.add(GROWN);
        builder.add(STAGE);
    }

    /*
    The code of this method will run everytime the EvolutionBlock is right-clicked
    Great for testing new features, debugging
    */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            //grow_Tree(world,pos);
            int stage = state.get(STAGE);
            if (stage >= 1 && stage <=5){
                world.setBlockState(pos, state.with(STAGE, stage + 5));
            }
            //world.setBlockState(pos, state.with(STAGE, state.get(STAGE)+1));
            //System.out.println(world.getBlockState(pos));
            //world.setBlockState(pos, state.with(AGE, world.getBlockState(pos).get(AGE)+1));
            EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);
            String s =String.valueOf(blockEntity.get_Age());
            player.sendMessage(new LiteralText(s), false);

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

        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);

        //if already dead, delete from world with probability 0.3
        if (state.get(STAGE)==11){
            if (random.nextFloat()<0.3F){
                //delete block entity
                blockEntity.die();
                //delete block
                world.removeBlock(pos, false);
            }
        }
        //normal case if tree still alive
        else {
            //increment age, age = number of random ticks this tree has received
            blockEntity.increment_Age();

            //get health based on environment and genetics
            float health = blockEntity.get_Health();

            //every random tick there is a chance that the tree dies, greater chance the lower the health
            //old verion
            //if (health < random.nextFloat()){
            //new version intended to add more evolutionary pressure
            if (health < random.nextGaussian()+1){
                //tree dies so remove blocks, delete blockEntity, put dead bush blockstate
                for (int i=1; i<blockEntity.get_height(); i+=1){
                    dropStack(world, pos.add(0,i,0), new ItemStack(Items.OAK_LOG, 1));
                    dropStack(world, pos.add(0,i,0), new ItemStack(Items.STICK, 1));
                }
                //remove all other blocks of tree
                TreeGrower.kill_Tree(world, pos);
                //set blockstate to 11, or dead, will be dead sapling and then disappear
                world.setBlockState(pos, state.with(STAGE, 11));
            }
            //if tree survives
            else {
                //grow a number of blocks determined by health and age
                for(int i = 0; i < blockEntity.get_grow_amt(health,random); i++){
                    //if tree is stripped
                    if (world.getBlockState(pos).get(STAGE) > 5){
                        //subtract 5 to return tree to appropriate un-stripped state
                        //Note: this takes one grow stage so tree cannot grow until it grows back
                        world.setBlockState(pos, state.with(STAGE, world.getBlockState(pos).get(STAGE)-5));
                    }
                    //if tree is not stripped
                    else{
                        //if tree is in sapling stage
                        if (state.get(STAGE) == 0){
                            //change it to be in the appropriate stage (1-5) depending on idealMoisture, this will affect its wood type
                            world.setBlockState(pos, state.with(STAGE, blockEntity.get_STAGE()));
                        }
                        grow_Tree(world,pos);
                    }
                }
                //attempt to produce offspring a number of times determined by health and age
                for(int i = 0; i < blockEntity.get_num_seeds(health, random);i++){
                    cloneTree(world, pos, random, blockEntity.get_height(), blockEntity);
                }
            }
        }
    }

    /*
    Clones EvolutionBlock at provided position to a nearby location
    Copies parent's genome - modifies it by specific updateGeneName BlockEntity methods
    */
    public void cloneTree(ServerWorld world, BlockPos pos, Random random,int height,EvolutionBlockEntity parent){
        int x_offset = (int) Math.round(random.nextGaussian()*height*4);
        int z_offset = (int) Math.round(random.nextGaussian()*height*4);
        BlockPos checkPos = new BlockPos(pos.getX() + x_offset, pos.getY() +height,pos.getZ() + z_offset);
        int status = 0;
        while (status < 2){
            BlockState blockState = world.getBlockState(checkPos);
            if (blockState.isAir()){
                status = 1;
                checkPos = checkPos.down();
            }
            else if ((status == 1) && (blockState.isOf(Blocks.FARMLAND) || blockState.isOf(Blocks.DIRT) || blockState.isOf(Blocks.COARSE_DIRT) || blockState.isOf(Blocks.PODZOL) || blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.SAND) || blockState.isOf(Blocks.SNOW_BLOCK))){
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
            blockEntity.copyValues(parent);
            blockEntity.mutate();
            //blockEntity.update_IdealTemp(0.8F + (random.nextFloat() * 0.4F));
        }
    }

    /*
    Grows the tree one block higher
    Needs
    */
    public void grow_Tree(World world, BlockPos pos){
        //Get associated BlockEntity
        EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(pos);
        //Increase its height by 1
        blockEntity.increment_Height();
        //Add height to position of block to get top of trunk
        //curr_height = height of tree trunk
        int curr_height = blockEntity.get_height();
        //System.out.println(curr_height);

        TreeGrower.grow_Trunk(world, pos, curr_height, blockEntity.get_Wood_Block());
        TreeGrower.grow_Leaves(world, pos, curr_height, blockEntity.get_Leaf_Block());
    }

    /*
    Links EvolutionBlock to EvolutionBlockEntity
    */
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EvolutionBlockEntity(pos, state);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, evo.EVOLUTION_ENTITY, (world1, pos, state1, be) -> EvolutionBlockEntity.tick(world1, pos, state1, be));
    }

    //make BlockVisible
    //necessary if you allow it to tick
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }
}

