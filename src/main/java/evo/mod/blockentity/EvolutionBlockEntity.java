package evo.mod.blockentity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import evo.mod.evo;

public class EvolutionBlockEntity extends BlockEntity {

    //data to store
    public float gene1 = 1;
    public float gene2 = 200;
    //constructor
    public EvolutionBlockEntity(BlockPos pos, BlockState state) {
        super(evo.EVOLUTION_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putFloat("gene1", gene1);
        tag.putFloat("gene2", gene1);
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        gene1 = tag.getFloat("gene1");
        gene2 = tag.getFloat("gene2");
    }
}
