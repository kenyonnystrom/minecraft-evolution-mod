package evo.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import evo.mod.blocks.EvolutionBlock;


public class Evo implements ModInitializer {

    public static final String MOD_ID = "evo";
    public static final EvolutionBlock EVOLUTION_BLOCK = new EvolutionBlock(FabricBlockSettings.of(Material.STONE).hardness(4.0f).ticksRandomly());

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"evolution_sapling_block"), EVOLUTION_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evolution_sapling_block"), new BlockItem(EVOLUTION_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
        System.out.println("Mod is initialized!");
    }
}
