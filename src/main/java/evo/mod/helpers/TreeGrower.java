package evo.mod.helpers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class TreeGrower {
    public static void grow_Trunk(World world, BlockPos pos, int height, BlockState wood_block){
        //can add max height here if needed
        BlockPos wood_pos = pos.add(0, height, 0);
        world.setBlockState(wood_pos, wood_block);
    }

    public static void generate_Trunk(StructureWorldAccess world, BlockPos pos, int height, BlockState wood_block){
        //can add max height here if needed
//        for(int i=0; i <= final_height; i++){
//            BlockPos wood_pos = pos.add(0, i, 0);
//            world.setBlockState(wood_pos, wood_block,2);
//        }
        //can add max height here if needed

        BlockPos wood_pos = pos.add(0, height, 0);
        world.setBlockState(wood_pos, wood_block,2);

    }

    private static void grow_Small_Leaves(World world, BlockPos pos, int height, BlockState leaves_block){
        BlockPos top_of_tree = pos.add(0, height + 1, 0);
        for(int i = -1; i < 2; i+=2){
            BlockPos old_loc_1 = pos.add(0, height-1, i);
            BlockPos old_loc_2 = pos.add(i, height-1, 0);
            BlockPos loc_1 = pos.add(0, height, i);
            BlockPos loc_2 = pos.add(i, height, 0);
            //setting them
            world.setBlockState(loc_1, leaves_block);
            world.setBlockState(loc_2, leaves_block);
            world.setBlockState(old_loc_1, Blocks.AIR.getDefaultState());
            world.setBlockState(old_loc_2,  Blocks.AIR.getDefaultState());
        }
        world.setBlockState(top_of_tree, leaves_block);

    }

    private static void grow_Large_Leaves(World world, BlockPos pos, int height, BlockState leaves_block){
        if (height%2==0) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i != 0 || j != 0) {
                        world.setBlockState(pos.add(i, height, j), leaves_block);
                    }
                }
            }
        }
        else{
            for(int i = -1; i < 2; i+=2){
                world.setBlockState(pos.add(0, height, i), leaves_block);
                world.setBlockState(pos.add(i, height, 0), leaves_block);
            }
        }
        world.setBlockState(pos.add(0, height+1, 0), leaves_block);
    }

    public static void generate_Small_Leaves(StructureWorldAccess world, BlockPos pos, int height, BlockState leaves_block){
        BlockPos top_of_tree = pos.add(0, height + 1, 0);
        for(int i = -1; i < 2; i+=2){
            BlockPos old_loc_1 = pos.add(0, height-1, i);
            BlockPos old_loc_2 = pos.add(i, height-1, 0);
            BlockPos loc_1 = pos.add(0, height, i);
            BlockPos loc_2 = pos.add(i, height, 0);
            //setting them
            world.setBlockState(loc_1, leaves_block,2);
            world.setBlockState(loc_2, leaves_block,2);
            world.setBlockState(old_loc_1, Blocks.AIR.getDefaultState(),2);
            world.setBlockState(old_loc_2,  Blocks.AIR.getDefaultState(),2);
        }
        world.setBlockState(top_of_tree, leaves_block,2);

    }

    public static void grow_Leaves(World world, BlockPos pos, int height, BlockState leaves_block) {
        //below line is deprecated, was used before we made leaf color change by idealTemp
        //BlockState leaves_block = Blocks.OAK_LEAVES.getDefaultState().with(Properties.PERSISTENT, true);
        if (height < 4) {
            grow_Small_Leaves(world, pos, height, leaves_block);
        } else{ // can add max height here if needed
            grow_Large_Leaves(world, pos, height, leaves_block);
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
