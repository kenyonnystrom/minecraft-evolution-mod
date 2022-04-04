package evo.mod.helpers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TreeGrower {
    public static void grow_Trunk(World world, BlockPos pos, int age){
        BlockState wood_block = Blocks.OAK_LOG.getDefaultState();
        BlockPos wood_pos = pos.add(0, age, 0);
        world.setBlockState(wood_pos, wood_block);
    }

    private static void grow_Small_Leaves(World world, BlockPos pos, int age, BlockState leaves_block){
        BlockPos top_of_tree = pos.add(0, age + 1, 0);
        for(int i = -1; i < 2; i+=2){
            BlockPos old_loc_1 = pos.add(0, age-1, i);
            BlockPos old_loc_2 = pos.add(i, age-1, 0);
            BlockPos loc_1 = pos.add(0, age, i);
            BlockPos loc_2 = pos.add(i, age, 0);
            //setting them
            world.setBlockState(loc_1, leaves_block);
            world.setBlockState(loc_2, leaves_block);
            world.setBlockState(old_loc_1, Blocks.AIR.getDefaultState());
            world.setBlockState(old_loc_2,  Blocks.AIR.getDefaultState());
        }
        world.setBlockState(top_of_tree, leaves_block);

    }

    private static void grow_Large_Leaves(World world, BlockPos pos, int age, BlockState leaves_block){
        if (age%2==0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i != 0 || j != 0) {
                        world.setBlockState(pos.add(i, age, j), leaves_block);
                    }
                }
            }
        }
        else{
            for(int i = -1; i < 2; i+=2){
                world.setBlockState(pos.add(0, age, i), leaves_block);
                world.setBlockState(pos.add(i, age, 0), leaves_block);
            }
        }
        world.setBlockState(pos.add(0, age+1, 0), leaves_block);
    }

    public static void grow_Leaves(World world, BlockPos pos, int age) {
        BlockState leaves_block = Blocks.OAK_LEAVES.getDefaultState();
        if (age < 4) {
            grow_Small_Leaves(world, pos, age, leaves_block);
        } else{
            grow_Large_Leaves(world, pos, age, leaves_block);
        }
    }

    public static void kill_Tree(World world, BlockPos pos) {
        BlockState air = Blocks.AIR.getDefaultState();
        for (int k=1; k<10;k++) {
            for (int j = -1; j < 2; j++) {
                for (int i = -1; i < 2; i++) {
                    world.setBlockState(pos.add(i,k,j),air);
                }
            }
        }
    }
}
