package evo.mod;

import evo.mod.blockentity.EvolutionBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import evo.mod.blocks.EvolutionBlock;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class evo implements ModInitializer {
    //mod id
    public static final String MOD_ID = "evo";

    //blocks
    public static final EvolutionBlock EVOLUTION_BLOCK = new EvolutionBlock(FabricBlockSettings.of(Material.PLANT).hardness(2.0f).sounds(BlockSoundGroup.WOOD).ticksRandomly().nonOpaque());

    //block entity declartion
    public static BlockEntityType<EvolutionBlockEntity> EVOLUTION_ENTITY;

    /*
    feature registration
    add these in TreeManager mixin file if you would like the features to generate in the game (upon world creation)
    */
    //evolution blocks as saplings in a patch like melons or pumpkins - not great
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> EVOLUTION_BLOCK_CONFIG = ConfiguredFeatures.register("patch_pumpkin2", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(EVOLUTION_BLOCK))), List.of(Blocks.GRASS_BLOCK),50)));
    public static final PlacedFeature PATCH_EVO_BLOCK_FEATURE = PlacedFeatures.register("patch_pumpkin2", EVOLUTION_BLOCK_CONFIG.withPlacement(new PlacementModifier[]{RarityFilterPlacementModifier.of(1), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()}));

    //evolution blocks as saplings scattered intermittently better
    public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> SINGLE_EVO_BLOCK_FEATURE_CONFIG = ConfiguredFeatures.register("single_evo_block_feature_config", Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(evo.EVOLUTION_BLOCK.getDefaultState()))));
    public static final PlacedFeature SINGLE_EVO_BLOCK_FEATURE = PlacedFeatures.register("single_evo_block_feature", SINGLE_EVO_BLOCK_FEATURE_CONFIG.withPlacement(new PlacementModifier[]{RarityFilterPlacementModifier.of(1), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()}));

    //initialize
    @Override
    public void onInitialize() {
        //evolution block
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"evolution_block"), EVOLUTION_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evolution_block"), new BlockItem(EVOLUTION_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

        //block entity
        EVOLUTION_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "evolution_block_entity"), FabricBlockEntityTypeBuilder.create(EvolutionBlockEntity::new, EVOLUTION_BLOCK).build(null));

        //render
        BlockRenderLayerMap.INSTANCE.putBlock(evo.EVOLUTION_BLOCK, RenderLayer.getCutout());

        System.out.println("Mod is initialized!");

    }
}
