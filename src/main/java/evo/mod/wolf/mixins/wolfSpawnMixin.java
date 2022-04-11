package evo.mod.wolf.mixins;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ServerWorld.class)
public abstract class wolfSpawnMixin {
    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk(WorldChunk chunk, int randomTickSpeed){
        //get list of all entities of a certain type
        //get length of list
        //check against threshold
        //spawn wolves if past
        //despawn wolves if lower
    }

}
