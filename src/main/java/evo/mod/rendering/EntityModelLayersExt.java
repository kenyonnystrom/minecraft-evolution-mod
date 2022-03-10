//
// Adds more layers that can be rendered onto entities. These layers must be associated
// with a model file, which can be done in EntityModelsExt. They also must be accessed
// by way of the getLayer method using the ModelLayerAccess interface.
//

package evo.mod.rendering;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import static evo.mod.rendering.mixins.ModelLayerAccess.register;

public abstract class EntityModelLayersExt{
    // Register new layers here
    public static final EntityModelLayer SHEEP_THICK = register("sheep", "thick");
}