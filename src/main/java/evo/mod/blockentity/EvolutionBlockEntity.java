package evo.mod.blockentity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import evo.mod.evo;

public class EvolutionBlockEntity extends BlockEntity {

    //data to store
    private float gene1 = 100;
    private float gene2 = 200;
    private float idealTemp; //health gets worse the farther actual temp it is from this value
    private float idealMoisture; // same as above but for water (we will have to generate value for water)
    private float growthPercent; //at times when tree is doing both growing and reproducing, what percent of health is spent on growth
    private float health; //health, calculated at the start of each random tick, based on location and genetics and randomness
    private int age; //number of random ticks the tree has received
    private int ageProduceSeeds; //the tree will only attempt to produce new trees when age >= this value, otherwise, all resources will be spent on growth
    private int ageStopGrowing; //the tree will only attempt to grow when age >= this value, otherwise, all resources will be spent on growth

    //constructor
    public EvolutionBlockEntity(BlockPos pos, BlockState state) {
        super(evo.EVOLUTION_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putFloat("gene1", gene1);
        tag.putFloat("gene2", gene2);
        tag.putFloat("idealTemp",idealTemp);
        tag.putFloat("idealMoisture",idealMoisture);
        tag.putFloat("growthPercent",growthPercent);
        tag.putFloat("health",health);
        tag.putInt("age",age);
        tag.putInt("aageProduceSeeds",ageProduceSeeds);
        tag.putInt("ageStopGrowing",ageStopGrowing);
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
    }

    public float getGene2(){
        return gene2;
    }

<<<<<<< Updated upstream
=======
    public float get_idealTemp() {return idealTemp;}
    public float get_idealMoisture() {return idealMoisture;}
    public float get_growthPercent() {return growthPercent;}
    public float get_health() {return health;}
    public int get_age() {return age;}
    public int get_ageProduceSeeds() {return ageProduceSeeds;}
    public int get_ageStopGrowing() {return ageStopGrowing;}

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
>>>>>>> Stashed changes
    public void updateGene2(float multiplier){
        gene2 = gene2 * multiplier;
        markDirty();
    }

}
