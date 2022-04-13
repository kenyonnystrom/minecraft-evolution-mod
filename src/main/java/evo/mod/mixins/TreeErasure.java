package evo.mod.mixins;


import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(DefaultBiomeFeatures.class)
public class TreeErasure {
    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome
     */
    @Overwrite()
    public static void addSavannaTrees(GenerationSettings.Builder builder) {
        return;
    }
    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addBirchTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addTaigaTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addGroveTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addWaterBiomeOakTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addForestTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite()
    public static void addTallBirchTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addExtraSavannaTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addWindsweptHillsTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addWindsweptForestTrees(GenerationSettings.Builder builder) {

    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addJungleTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addSparseJungleTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addBadlandsPlateauTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addSnowySpruceTrees(GenerationSettings.Builder builder) {
        return;
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addPlainsFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_PLAIN);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
    }

    /**
     * @author = Ben Santos
     */
    @Overwrite
    public static void addMeadowFlowers(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_MEADOW);
    }

}
