package evo.mod;

import evo.mod.blockentity.EvolutionBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import evo.mod.blocks.EvolutionBlock;

public class evo implements ModInitializer {
    //mod id
    public static final String MOD_ID = "evo";

    //blocks
    public static final EvolutionBlock EVOLUTION_BLOCK = new EvolutionBlock(FabricBlockSettings.of(Material.STONE).hardness(4.0f).ticksRandomly());


    //block entities
    public static BlockEntityType<EvolutionBlockEntity> EVOLUTION_ENTITY;

    //initialize
    @Override
    public void onInitialize() {
        //evolution block
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"evolution_sapling_block"), EVOLUTION_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evolution_sapling_block"), new BlockItem(EVOLUTION_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

        //block entity
        EVOLUTION_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "evolution_block_entity"), FabricBlockEntityTypeBuilder.create(EvolutionBlockEntity::new, EVOLUTION_BLOCK).build(null));

        System.out.println("Mod is initialized!");
    }
}
