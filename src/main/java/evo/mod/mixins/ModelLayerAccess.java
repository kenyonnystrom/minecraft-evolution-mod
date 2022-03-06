/*
Use to access new layers added in EntityModelLayersExt.
 */
package evo.mod.mixins;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityModelLayers.class)
public interface ModelLayerAccess {
    @Invoker("register")
    public static EntityModelLayer register(String id, String layer) {
        throw new AssertionError();
    }
}
