package evo.mod.tree.mixins;


import evo.mod.evo;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DefaultBiomeFeatures.class)

public class TreeManager {


    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addSavannaTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }
    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addBirchTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addTaigaTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addGroveTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addWaterBiomeOakTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addForestTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite()
    public static void addTallBirchTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addExtraSavannaTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addWindsweptHillsTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addWindsweptForestTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addJungleTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addSparseJungleTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addBadlandsPlateauTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addSnowySpruceTrees(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        return;
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addPlainsFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_PLAIN);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);

    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addMeadowFlowers(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_MEADOW);
    }

    /**
     * @author = Ben Santos
     * @reason = removing trees from this biome, adding our custom trees
     */
    @Overwrite
    public static void addSwampFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, evo.EVOLUTION_TREE_PLACED);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_SWAMP);
    }

}
