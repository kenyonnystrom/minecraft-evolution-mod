package evo.mod.blockentity;
import evo.mod.helpers.TreeGrower;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import evo.mod.evo;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import static evo.mod.blocks.EvolutionBlock.STAGE;
import static net.minecraft.block.Block.dropStack;
import static net.minecraft.block.LeavesBlock.PERSISTENT;
import java.util.Random;

public class EvolutionBlockEntity extends BlockEntity {

    //data to store
    static Random r = new Random();
    public int tickCounter = r.nextInt(1600);
    private float idealTemp = -0.7F + (r.nextFloat() * 2.7F); //health gets worse the farther actual temp it is from this value
    private float idealMoisture = r.nextFloat(); // same as above but for water (we will have to generate value for water)
    private float growthPercent = r.nextFloat(); //at times when tree is doing both growing and reproducing, what percent of health is spent on growth
    private int age = 0; //number of random ticks the tree has received
    private int ageProduceSeeds =(int) (4 * r.nextFloat()); //the tree will only attempt to produce new trees when age >= this value, otherwise, all resources will be spent on growth
    private int ageStopGrowing = 4 + (int) (4 * r.nextFloat()); //the tree will only attempt to grow when age >= this value, otherwise, all resources will be spent on growth
    private int height = 0;
    public static int ticksToEvolve = 2400;


    //constructor
    public EvolutionBlockEntity(BlockPos pos, BlockState state) {
        super(evo.EVOLUTION_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putFloat("idealTemp",idealTemp);
        tag.putFloat("idealMoisture",idealMoisture);
        tag.putFloat("growthPercent",growthPercent);
        tag.putInt("age",age);
        tag.putInt("ageProduceSeeds",ageProduceSeeds);
        tag.putInt("ageStopGrowing",ageStopGrowing);
        tag.putInt("height",height);
        tag.putInt("tickCounter", tickCounter);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        idealTemp = tag.getFloat("idealTemp");
        idealMoisture = tag.getFloat("idealMoisture");
        growthPercent = tag.getFloat("growthPercent");
        age = tag.getInt("age");
        ageProduceSeeds = tag.getInt("ageProduceSeeds");
        ageStopGrowing = tag.getInt("ageStopGrowing");
        height = tag.getInt("height");
        tickCounter = tag.getInt("tickCounter");
    }

    public float get_idealTemp() {return idealTemp;}

    public float get_idealMoisture() {return idealMoisture;}

    public float get_growthPercent() {return growthPercent;}

    public int get_ageProduceSeeds() {return ageProduceSeeds;}

    public int get_ageStopGrowing() {return ageStopGrowing;}

    public int get_height() {return height;}

    public void increment_Height(){
        height = height + 1;
        markDirty();
    }

    public void increment_Age(){
        age = age + 1;
        markDirty();
    }

    //looks up default temp value for biome, which ranges -0.7 to 2, could be more sophisticated to take in light, altitude
    public float get_Temp() {
        Biome biome = world.getBiome(pos);
        return biome.getTemperature();
    }

    //just looks up default downfall value for biome, could be more sophisticated by taking in temp, altitude, proximity to water for example
    public float get_Moisture(){
        Biome biome = world.getBiome(pos);
        return biome.getDownfall();
    }

    // Health has max of 3, decreases when tree is older, and temp and moisture levels are more different from ideal, when light is low, and when bark is stripped
    //magic number city
    public float get_Health(){
        float temp_dist = (float) Math.abs(idealTemp - get_Temp()) * 12F;
        float moisture_dist = (float) Math.abs(idealMoisture - get_Moisture())*22F;
        float light = (float) world.getLightLevel(pos.up(height +1));
        float health = (20 + light - 3*age - temp_dist - moisture_dist)/10;

        return health;
    }

    //given health and genetics, returns how many offspring should be produced this lifecycle (could be 0)
    public int get_num_seeds(float total_health, Random random){
        float unrounded;
        // if too young to produce seeds, return zero so no clones will be made
        if (age < ageProduceSeeds){
            return 0;
        }
        else {
            if (age >= ageStopGrowing){
                //tree is no longer growing so all resources are dedicated to seed production
                unrounded = total_health;
            }
            else {
                //tree is growing and producing seeds, resources split according to growthPercent
                unrounded = total_health * (1F - growthPercent);
            }
        }
        // attempting to control growth simply by cutting num of seeds in half
        //magic number
        unrounded = unrounded/2;
        //floor of unrounded value
        int rounded = (int) unrounded;
        //we are rounding probabilistically, if unrounded = 1.4 than 40% of the time rounded = 2, 6-% of the time rounded = 1
        if (unrounded % 1 > random.nextFloat()){
            rounded ++;
        }
        return rounded;
    }

    //given health and genetics, returns how blocks tree should grow this lifecycle (could be 0)
    public int get_grow_amt(float total_health, Random random){
        float unrounded;
        // if too old to grow, return zero so no growth occurs
        if (age >= ageStopGrowing){
            return 0;
        }
        else {
            if (age < ageProduceSeeds){
                //tree is not producing seeds yet so all resources are dedicated to growing
                unrounded = total_health;
            }
            else {
                //tree is growing and producing seeds, resources split according to growthPercent
                unrounded = total_health * (1F - growthPercent);
            }
        }
        //floor of unrounded value (CASTING TO INT TAKES FLOOR)
        int rounded = (int) unrounded;
        //we are rounding probabilistically, if unrounded = 1.4 than 40% of the time rounded = 2, 60% of the time rounded = 1
        if (unrounded % 1 > random.nextFloat()){
            rounded ++;
        }
        return rounded;
    }

    public String getStatus(){
        return String.format("Health: %f\nAge: %d\nHeight: %d\nIdeal Temperature: %f\nActual Temperature: %f\nIdeal Moisture: %f\nActual Moisture: %f\n\n\n", get_Health(), age, height, idealTemp, get_Temp(), idealMoisture, get_Moisture());
    }


    //based on idealTemp, return appropriate leaf block for TreeGrower to use, darker leaves = more adapted to cold
    public BlockState get_Leaf_Block(){
        if (idealTemp < 0.0F){
            return Blocks.SPRUCE_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else if (idealTemp < 0.5F){
            return Blocks.BIRCH_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else if (idealTemp < 0.75F){
            return Blocks.ACACIA_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else if (idealTemp < 0.1F){
            return Blocks.DARK_OAK_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else if (idealTemp < 1.3F){
            return Blocks.OAK_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else if (idealTemp < 1.6F){
            return Blocks.JUNGLE_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
        else{
            return Blocks.AZALEA_LEAVES.getDefaultState().with(PERSISTENT, true);
        }
    }

    //based on idealMoisture, return appropriate wood block for TreeGrower to use, darker bark = more adapted to wet
    public BlockState get_Wood_Block(){
        if (idealMoisture < 0.1F){
            return Blocks.ACACIA_LOG.getDefaultState();
        }
        else if (idealMoisture < 0.3F){
            return Blocks.OAK_LOG.getDefaultState();
        }
        else if (idealMoisture < 0.5F){
            return Blocks.JUNGLE_LOG.getDefaultState();
        }
        else if (idealMoisture < 0.75F){
            return Blocks.DARK_OAK_LOG.getDefaultState();
        }
        else {
            return Blocks.SPRUCE_LOG.getDefaultState();
        }
    }

    //same as above but for changing blockstate of base block rather than b locks of trunk
    //unlike get_Leaf_Block() and get_Wood_Block(), should only be called once when tree first grows from sapling
    public int get_STAGE(){
        if (idealMoisture < 0.1F){
            return 1;
        }
        else if (idealMoisture < 0.3F){
            return 2;
        }
        else if (idealMoisture < 0.5F){
            return 3;
        }
        else if (idealMoisture < 0.75F){
            return 4;
        }
        else {
            return 5;
        }
    }

    //used to copy genetic info from parent to new offspring, to then be mutated
    public void copyValues(EvolutionBlockEntity parent){
        idealTemp = parent.get_idealTemp();
        idealMoisture = parent.get_idealMoisture();
        growthPercent = parent.get_growthPercent();
        ageProduceSeeds = parent.get_ageProduceSeeds();
        ageStopGrowing = parent.get_ageStopGrowing();
    }

    //takes offspring that is identical copy of parent, resets age and height, and randomly mutates genetics
    public void mutate(){
        age = 0;
        height = 0;
        //magic numbers
        //By adding a normally distributed value with sd = 1/3 there is roughly 86% chance it stays the same, 7% chance it increases, 7% chance it decreases
        ageStopGrowing = (int) Math.round(ageStopGrowing + r.nextGaussian()/3);
        //"clamping" value to be within allowed range 4-8
        ageStopGrowing = Math.max(4, Math.min(8,ageStopGrowing));
        ageProduceSeeds = (int) Math.round(ageProduceSeeds + r.nextGaussian()/3);
        //"clamping" value to be within allowed range 1-4
        ageProduceSeeds = Math.max(1, Math.min(4,ageProduceSeeds));

        //changing float values with random distribution according to mutation rate
        // magic numbers
        float idealTempMutationRate = 0.05F;
        float idealMoistureMutationRate = 0.03F;
        float growthPrecentMutationRate = 0.03F;
        idealTemp = idealTemp + idealTempMutationRate * (float) r.nextGaussian();
        idealMoisture = idealMoisture + idealMoistureMutationRate * (float) r.nextGaussian();
        growthPercent = growthPercent + growthPrecentMutationRate * (float) r.nextGaussian();
        //clamping growthpercent between 0-1 so it makes sense as a percent
        // idealTemp and idealMoisture are unclamped since theoretically they should be clamped by evolutionary pressure
        growthPercent = Math.max(0.0F, Math.min(1.0F,growthPercent));
        markDirty();
    }

    //called when dead bush decays, removes block entity
    //possibly we could get rid of it earlier and save a bit of memory, but I was paranoid about having a block without an associated entity
    public void die(){
            markRemoved();
            markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, EvolutionBlockEntity be) {
        //increment tickCounter
        be.tickCounter = be.tickCounter + 1;

        //number of ticks needed for evolution to occur = could be a minimum amount or a multiple of a number
        if (be.tickCounter >= ticksToEvolve) {
            if (!world.isClient) {
                //if tree is already dead, delete from world with probability 0.3
                if (state.get(STAGE) == 11) {
                    if (r.nextFloat()<0.3F) {
                        //delete block entity
                        be.die();
                        //delete block
                        world.removeBlock(pos, false);
                    }
                }

                //tree is still alive
                else {
                    //increment age, age = number of random ticks this tree has received
                    be.increment_Age();

                    //get health based on environment and genetics
                    float health = be.get_Health();

                    //there is a chance that the tree dies, greater chance the lower the health
                    //new version intended to add more evolutionary pressure
                    if (health < r.nextGaussian() + 1) {
                        //drop items
                        for (int i = 1; i < be.get_height(); i += 1) {
                            dropStack(world, pos.add(0, i, 0), new ItemStack(Items.OAK_LOG, 1));
                            dropStack(world, pos.add(0, i, 0), new ItemStack(Items.STICK, 1));
                        }
                        //remove all other blocks of tree
                        TreeGrower.kill_Tree(world, pos);
                        //set blockstate to 11, or dead, will be dead sapling and then disappear
                        world.setBlockState(pos, state.with(STAGE, 11));

                    }
                    else {
                        //grow a number of blocks determined by health and age
                        for(int i = 0; i < be.get_grow_amt(health,r); i++){
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
                                    world.setBlockState(pos, state.with(STAGE, be.get_STAGE()));
                                }
                                grow_Tree(world,pos, be);
                            }
                        }
                        //attempt to produce offspring a number of times determined by health and age
                        for(int i = 0; i < be.get_num_seeds(health, r);i++){
                            cloneTree(world, pos, r, be.get_height(), be);
                        }
                    }
                }
            }
            be.tickCounter = 0;
        }
    }

    private static void cloneTree(World world, BlockPos pos, Random r, int height, EvolutionBlockEntity be) {
        int x_offset = (int) Math.round(r.nextGaussian()*height*4);
        int z_offset = (int) Math.round(r.nextGaussian()*height*4);
        BlockPos checkPos = new BlockPos(pos.getX() + x_offset, pos.getY() +height,pos.getZ() + z_offset);
        int status = 0;
        while (status < 2){
            BlockState blockState = world.getBlockState(checkPos);
            if ((blockState.isAir()) || (blockState.isOf(Blocks.SNOW))) {
                status = 1;
                checkPos = checkPos.down();
            }
            else if ((status == 1) && (blockState.isOf(Blocks.FARMLAND) || blockState.isOf(Blocks.DIRT) || blockState.isOf(Blocks.COARSE_DIRT) || blockState.isOf(Blocks.PODZOL) || blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.SAND) || blockState.isOf(Blocks.SNOW_BLOCK))){
                status = 2;
                for (int i = -2; i < 3; i ++){
                    for (int j = -2; j < 3; j ++){
                        BlockPos checkTree = checkPos.add(i,1,j);
                        BlockState checkTreeState = world.getBlockState(checkTree);
                        if (checkTreeState.isIn(BlockTags.LOGS) || checkTreeState.isOf(evo.EVOLUTION_BLOCK)){
                            status = 4;
                        }
                    }
                }
            }
            else{
                status = 3;
            }
        }
        if (status == 2){
            BlockPos newTreePos = checkPos.up();
            BlockState newTree = evo.EVOLUTION_BLOCK.getDefaultState();
            world.setBlockState(newTreePos, newTree);
            EvolutionBlockEntity blockEntity = (EvolutionBlockEntity) world.getBlockEntity(newTreePos);
            blockEntity.copyValues(be);
            blockEntity.mutate();
        }
        /*
        for debugging/demonstration, place red concrete block when tree fails to place
        if (status > 2){
            BlockPos newTreePos = checkPos.up();
            world.setBlockState(newTreePos, Blocks.RED_CONCRETE.getDefaultState());
        }

         */
    }

    public static void grow_Tree(World world, BlockPos pos, EvolutionBlockEntity be) {
        //Increase BlockEntity height by 1
        be.increment_Height();
        //Add height to position of block to get top of trunk
        //curr_height = height of tree trunk
        int curr_height = be.get_height();
        TreeGrower.grow_Trunk(world, pos, curr_height, be.get_Wood_Block());
        TreeGrower.grow_Leaves(world, pos, curr_height, be.get_Leaf_Block());
    }
}
