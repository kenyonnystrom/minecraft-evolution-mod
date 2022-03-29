package evo.mod.blockentity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import evo.mod.evo;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class EvolutionBlockEntity extends BlockEntity {

    //data to store
    Random r = new Random();
    private float gene1 = 100;
    private float gene2 = 200;
    private float currentTemp;
    private double idealTemp = -0.7 + (r.nextFloat() * 2.7);; //health gets worse the farther actual temp it is from this value
    private float idealMoisture; // same as above but for water (we will have to generate value for water)
    private float growthPercent; //at times when tree is doing both growing and reproducing, what percent of health is spent on growth
    private float health; //health, calculated at the start of each random tick, based on location and genetics and randomness
    private int age = 1; //number of random ticks the tree has received
    private int ageProduceSeeds; //the tree will only attempt to produce new trees when age >= this value, otherwise, all resources will be spent on growth
    private int ageStopGrowing; //the tree will only attempt to grow when age >= this value, otherwise, all resources will be spent on growth
    private int height;
    //constructor
    public EvolutionBlockEntity(BlockPos pos, BlockState state) {
        super(evo.EVOLUTION_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putFloat("gene1", gene1);
        tag.putFloat("gene2", gene2);
        tag.putDouble("idealTemp",idealTemp);
        tag.putFloat("idealMoisture",idealMoisture);
        tag.putFloat("growthPercent",growthPercent);
        tag.putFloat("health",health);
        tag.putFloat("currentTemp",currentTemp);
        tag.putInt("age",age);
        tag.putInt("aageProduceSeeds",ageProduceSeeds);
        tag.putInt("ageStopGrowing",ageStopGrowing);
        tag.putInt("height",height);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        gene1 = tag.getFloat("gene1");
        gene2 = tag.getFloat("gene2");
        idealTemp = tag.getFloat("idealTemp");
        idealMoisture = tag.getFloat("idealMoisture");
        growthPercent = tag.getFloat("growthPercent");
        health = tag.getFloat("health");
        age = tag.getInt("age");
        ageProduceSeeds = tag.getInt("ageProduceSeeds");
        ageStopGrowing = tag.getInt("ageStopGrowing");
        height = tag.getInt("height");
    }

    public float getGene2(){
        return gene2;
    }

    public double get_idealTemp() {return idealTemp;}
    public float get_idealMoisture() {return idealMoisture;}
    public float get_growthPercent() {return growthPercent;}
    public float get_health() {return health;}
    public int get_Age() {return age;}
    public int get_ageProduceSeeds() {return ageProduceSeeds;}
    public int get_ageStopGrowing() {return ageStopGrowing;}
    public int get_height() {return height;}

    public void increment_age(){
        age = age + 1;
        markDirty();
    }
    public void reset_age(){
        age = 0;
        markDirty();
    }
    public void set_health(float newHealth){
        health = newHealth;
        markDirty();
    }

    public void updateGene2(float multiplier){
        gene2 = gene2 * multiplier;
        markDirty();
    }
    public void update_IdealTemp(float multiplier){
        idealTemp = idealTemp * multiplier;
        markDirty();
    }

    public float get_Health(ServerWorld world, BlockPos pos){
        float light = (float) world.getLightLevel(pos.up(height));
        this.get_Temp();
        float temp_dist = get_TempDist();
        float newHealth = Math.max(0.0F, light - age - temp_dist);
        this.set_health(newHealth);
        return newHealth;
    }

    public float get_TempDist(){
        float temp_dist = (float) Math.pow(((idealTemp - currentTemp)*5), 2.0F);
        return temp_dist;
    }

    public float get_Temp() {
        Biome biome = world.getBiome(pos);
        currentTemp = biome.getTemperature();
        markDirty();
        return currentTemp;
    }

    //relies on block recieivng a random tick
    public void die(ServerWorld world){
            BlockState deadBush = Blocks.DEAD_BUSH.getDefaultState();
            world.setBlockState(pos,deadBush);
            markRemoved();
    }

}
