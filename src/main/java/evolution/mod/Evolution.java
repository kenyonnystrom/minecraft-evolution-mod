package evolution.mod;

import evolution.mod.blocks.ExampleBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Evolution implements ModInitializer {
    public static final String MOD_ID = "evolution";
    public static final ExampleBlock EXAMPLE_BLOCK = new ExampleBlock(FabricBlockSettings.of(Material.STONE).hardness(4.0f).ticksRandomly());

    //public static final Block EVOLUTION_SAPLING_BLOCK = new Block(FabricBlockSettings.of(Material.PLANT).strength(0F, 0f).sounds(BlockSoundGroup.GRASS).breakByTool(FabricToolTags.HOES).ticksRandomly());
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"evolution_sapling_block"), EXAMPLE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evolution_sapling_block"), new BlockItem(EXAMPLE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
        System.out.println("Mod is initialized!");
    }
}
