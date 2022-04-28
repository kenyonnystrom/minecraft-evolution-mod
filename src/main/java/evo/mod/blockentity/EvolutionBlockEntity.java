package evo.mod.blockentity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import evo.mod.evo;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class EvolutionBlockEntity extends BlockEntity {

    //data to store
    public int tickCounter = 0;
    Random r = new Random();
    private float idealTemp = -0.7F + (r.nextFloat() * 2.7F); //health gets worse the farther actual temp it is from this value
    private float idealMoisture = r.nextFloat(); // same as above but for water (we will have to generate value for water)
    private float growthPercent = r.nextFloat(); //at times when tree is doing both growing and reproducing, what percent of health is spent on growth
    private int age = 0; //number of random ticks the tree has received
    private int ageProduceSeeds =(int) (4 * r.nextFloat()); //the tree will only attempt to produce new trees when age >= this value, otherwise, all resources will be spent on growth
    private int ageStopGrowing = 4 + (int) (4 * r.nextFloat()); //the tree will only attempt to grow when age >= this value, otherwise, all resources will be spent on growth
    private int height = 0;
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
    }

    public float get_idealTemp() {return idealTemp;}

    public float get_idealMoisture() {return idealMoisture;}

    public float get_growthPercent() {return growthPercent;}

    public int get_Age() {return age;}

    public int get_ageProduceSeeds() {return ageProduceSeeds;}

    public int get_ageStopGrowing() {return ageStopGrowing;}

    public int get_height() {return height;}

    public int get_tickCounter() {return tickCounter;}

    public void increment_Height(){
        height = height + 1;
        markDirty();
    }

    public void increment_Age(){
        age = age + 1;
        markDirty();
    }

    public void increment_tickCounter(){
        tickCounter = tickCounter + 1;
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

        float temp_dist = (float) Math.pow(((idealTemp - get_Temp())*10), 2.0F);
        float moisture_dist = (float) Math.pow(((idealMoisture - get_Moisture())*10), 2.0F);
        float health = (30 - 3*age - temp_dist - moisture_dist)/10;
        float light = (float) world.getLightLevel(pos.up(height +1));

        System.out.printf("health:%f idealTemp:%f Temp:%f temp_dist:%f idealMoisture:%f Moisture:%f moist_dif:%f age:%d light:%f\n", health, idealTemp, get_Temp(), temp_dist, idealMoisture, get_Moisture(),moisture_dist, age, light);
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
        //we are rounding probabilistically, if unrounded = 1.4 than 40% of the time rounded = 2, 6-% of the time rounded = 1
        if (unrounded % 1 > random.nextFloat()){
            rounded ++;
        }
        return rounded;
    }

    //based on idealTemp, return appropriate leaf block for TreeGrower to use, darker leaves = more adapted to cold
    public BlockState get_Leaf_Block(){
        if (idealTemp < 0.0F){
            return Blocks.SPRUCE_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else if (idealTemp < 0.5F){
            return Blocks.BIRCH_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else if (idealTemp < 0.75F){
            return Blocks.ACACIA_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else if (idealTemp < 0.1F){
            return Blocks.DARK_OAK_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else if (idealTemp < 1.3F){
            return Blocks.OAK_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else if (idealTemp < 1.6F){
            return Blocks.JUNGLE_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        }
        else{
            return Blocks.AZALEA_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
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
    public Integer get_STAGE(){
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
    }

    // remnant of ben experimenting with the block entity getting game ticks so lifecycle would be on more consistent schedule
    public static void tick(World world, BlockPos pos, BlockState state, EvolutionBlockEntity be) {
        be.tickCounter = be.tickCounter + 1;
        if (be.tickCounter % 100 == 0){
            System.out.println(be.tickCounter);
        }
    }

}
