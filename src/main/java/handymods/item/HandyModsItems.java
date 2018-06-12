package handymods.item;

import handymods.HandyMods;
import handymods.block.HandyModsBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class HandyModsItems {
	public static ItemBlockEnderBox enderBox = new ItemBlockEnderBox();
	public static ItemBlock itemHolderOpaque = itemBlock(HandyModsBlocks.itemHolderOpaque);
	public static ItemBlock itemHolderTransparent = itemBlock(HandyModsBlocks.itemHolderTransparent);
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(enderBox);
		registry.register(itemHolderOpaque);
		registry.register(itemHolderTransparent);
	}
	
	public static Item item(Item item, String name) {
		return item
				.setRegistryName(HandyMods.resourceLocation(name))
				.setUnlocalizedName(HandyMods.namespaced(name));
	}
	
	public static ItemBlock itemBlock(Block block) {
		return (ItemBlock) new ItemBlock(block)
				.setRegistryName(block.getRegistryName())
				.setUnlocalizedName(block.getUnlocalizedName());
	}
	
	@SubscribeEvent
	public static void registerModels(@SuppressWarnings("unused") ModelRegistryEvent event) {
		registerItemModel(enderBox);
		registerItemModel(itemHolderOpaque);
		registerItemModel(itemHolderTransparent);
	}
	
	private static void registerItemModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
