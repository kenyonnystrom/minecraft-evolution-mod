package evolution.mod;

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
    public static final String MOD_ID = "evolution";//block set up
    public static final Block EVOLUTION_SAPLING_BLOCK = new Block(FabricBlockSettings.of(Material.PLANT).strength(0F, 0f).sounds(BlockSoundGroup.GRASS).breakByTool(FabricToolTags.HOES));
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID,"evolution_sapling_block"), EVOLUTION_SAPLING_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "evolution_sapling_block"), new BlockItem(EVOLUTION_SAPLING_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
        System.out.println("Mod is initialized!");
    }
}
