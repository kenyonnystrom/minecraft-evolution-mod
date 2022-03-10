//
// Almost entirely overwriting SheepWoolFeatureRenderer, this render file handles
// wool rendering for all possible sheep wool types. The correct model and texture
// are rendered based on the wool type or sheared state of the sheep.
//

package evo.mod.rendering.mixins;

import evo.mod.*;

import evo.mod.rendering.EntityModelLayersExt;
import evo.mod.rendering.SheepThickWoolEntityModel;
import evo.mod.sheep.EvolvingSheepAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SheepWoolRenderer extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
    private static final Identifier SHAVED = new Identifier(Evo.MOD_ID, "textures/sheep_no_wool.png");
    private static final Identifier THIN = new Identifier(Evo.MOD_ID, "textures/sheep_thin_wool.png");
    private static final Identifier STANDARD = new Identifier("textures/entity/sheep/sheep_fur.png");
    private static final Identifier THICK = new Identifier(Evo.MOD_ID, "textures/thick_wool.png");
    private static final Identifier SHEARED = new Identifier(Evo.MOD_ID, "textures/sheep_sheared.png");
    private SheepEntityModel<SheepEntity> baseModel;
    private SheepWoolEntityModel<SheepEntity> woolModel;
    private SheepThickWoolEntityModel<SheepEntity> thickModel;

    public SheepWoolRenderer(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context) {
        super(context);
    }

    // Edits SheepWoolFeatureRenderer constructor, allowing it to set these variables upon instantiation
    @Inject(method="<init>*", at = @At("RETURN"))
    public void onConstructed(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context, EntityModelLoader loader, CallbackInfo ci) {
        this.baseModel = new SheepEntityModel<>(loader.getModelPart(EntityModelLayers.SHEEP));
        this.woolModel = new SheepWoolEntityModel<>(loader.getModelPart(EntityModelLayers.SHEEP_FUR));
        this.thickModel = new SheepThickWoolEntityModel<>(loader.getModelPart(EntityModelLayersExt.SHEEP_THICK));
    }

    // Overwrites render function from SheepWoolFeatureRenderer
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l) {
        if (sheepEntity.isInvisible()) {
            // Invisible section is not affected by wool size because it is pretty irrelevant,
            // so just about everything in this if statement is straight from the source code
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            boolean bl = minecraftClient.hasOutline(sheepEntity);
            if (bl) {
                this.getContextModel().copyStateTo(this.woolModel);
                this.woolModel.animateModel(sheepEntity, f, g, h);
                this.woolModel.setAngles(sheepEntity, f, g, j, k, l);
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(STANDARD));
                this.woolModel.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(sheepEntity, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
            }
        } else {
            // Rainbow color Easter egg
            float s;
            float t;
            float u;
            if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().asString())) {
                int n = sheepEntity.age / 25 + sheepEntity.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float) (sheepEntity.age % 25) + h) / 25.0F;
                float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
                float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
                s = fs[0] * (1.0F - r) + gs[0] * r;
                t = fs[1] * (1.0F - r) + gs[1] * r;
                u = fs[2] * (1.0F - r) + gs[2] * r;
            } else {
                // Gets standard color
                float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
                s = hs[0];
                t = hs[1];
                u = hs[2];
            }
            if (sheepEntity.isSheared()) {
                render(this.getContextModel(), this.baseModel, SHEARED, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);
            } else {
                // Render different skin and model based on wool size
                // Access evolving sheep method using interface
                switch (((EvolvingSheepAccess)sheepEntity).getWool()) {
                    case NO_WOOL:
                        // Ignore color vars
                        render(this.getContextModel(), this.baseModel, SHAVED, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, 0.9019608F, 0.9019608F, 0.9019608F);
                        break;
                    case THIN_WOOL:
                        render(this.getContextModel(), this.baseModel, THIN, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);
                        break;
                    case THICK_WOOL:
                        render(this.getContextModel(), this.thickModel, THICK, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);
                        break;
                    default:
                        // STD_WOOL comes here
                        render(this.getContextModel(), this.woolModel, STANDARD, matrixStack, vertexConsumerProvider, i, sheepEntity, f, g, j, k, l, h, s, t, u);
                        break;
                }
            }
        }
    }
}
