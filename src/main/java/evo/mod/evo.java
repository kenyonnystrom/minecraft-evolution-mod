package evo.mod;

import evo.mod.blockentity.EvolutionBlockEntity;
import evo.mod.world.features.StoneSpiralFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
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
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class evo implements ModInitializer {
    //mod id
    public static final String MOD_ID = "evo";

    //blocks
    public static final EvolutionBlock EVOLUTION_BLOCK = new EvolutionBlock(FabricBlockSettings.of(Material.PLANT).hardness(2.0f).sounds(BlockSoundGroup.WOOD).ticksRandomly().nonOpaque());

    //block entities
    public static BlockEntityType<EvolutionBlockEntity> EVOLUTION_ENTITY;

    //features
    private static final Feature<DefaultFeatureConfig> SPIRAL = new StoneSpiralFeature(DefaultFeatureConfig.CODEC);


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

        //features
        Registry.register(Registry.FEATURE, new Identifier("tutorial", "spiral"), SPIRAL);

        System.out.println("Mod is initialized!");
    }
}
