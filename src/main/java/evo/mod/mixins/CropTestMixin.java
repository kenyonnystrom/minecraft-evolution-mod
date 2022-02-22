package evo.mod.mixins;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(CropBlock.class)
public class CropTestMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci){

        //get position of current block
        int block_x = pos.getX();
        int block_y = pos.getY();
        int block_z = pos.getZ();
        //place a random block somewhere nearby
        Random r = new Random();
        //set up sapling block
        BlockState new_block = Blocks.OAK_SAPLING.getDefaultState();
        BlockPos new_block_location = new BlockPos(block_x + r.nextInt(20), block_y,block_z + r.nextInt(20));
        world.setBlockState(new_block_location, new_block);
        System.out.println(block_x + " " + block_y + " " + block_z);
        System.out.println("TICK!");

    }
}
