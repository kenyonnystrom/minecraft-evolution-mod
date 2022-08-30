//
// This extension cleverly captures local variables in EntityModels to add extra key/model pairings
// to the immutable map that is used in the model-rendering hierarchy.
//

package evo.mod.rendering.mixins;

import com.google.common.collect.ImmutableMap;
import evo.mod.rendering.EntityModelLayersExt;
import evo.mod.rendering.SheepThickWoolEntityModel;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.Map;

@Mixin(EntityModels.class)
public abstract class EntityModelsExt {
    @Inject(remap = false, method = "getModels", at = @At(value = "INVOKE", target = "net/minecraft/client/render/entity/model/BipedEntityModel.getModelData (Lnet/minecraft/client/model/Dilation;F)Lnet/minecraft/client/model/ModelData;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void addToMap(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder) {
        builder.put(EntityModelLayersExt.SHEEP_THICK, SheepThickWoolEntityModel.getTexturedModelData());
    }
}
