package evo.mod;

import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.world.features.EvolutionTreeFeature;
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
    public static final EvolutionBlock EVOLUTION_BLOCK = new EvolutionBlock(FabricBlockSettings.of(Material.PLANT).hardness(2.0f).sounds(BlockSoundGroup.WOOD).nonOpaque());

    //block entity declaration
    public static BlockEntityType<EvolutionBlockEntity> EVOLUTION_ENTITY;

    /*
    feature registration
    add these in TreeManager mixin file if you would like the features to generate in the game (upon world creation)
    */

    //evolution block with tree above
    public static final Feature<DefaultFeatureConfig> EVOLUTION_TREE_FEATURE = Registry.register(Registry.FEATURE,"evolution_tree", new EvolutionTreeFeature(DefaultFeatureConfig.CODEC));
    public static final ConfiguredFeature<DefaultFeatureConfig, ?> EVOLUTION_TREE_FEATURE_CONFIGURED = ConfiguredFeatures.register("evolution_tree_config", EVOLUTION_TREE_FEATURE.configure(FeatureConfig.DEFAULT));
    public static final PlacedFeature EVOLUTION_TREE_PLACED = PlacedFeatures.register("evolution_tree_placed", EVOLUTION_TREE_FEATURE_CONFIGURED.withPlacement(new PlacementModifier[]{RarityFilterPlacementModifier.of(1), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()}));

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
