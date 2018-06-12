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
	public static ItemBlockEnderBox enderBox = itemBlock(new ItemBlockEnderBox());
	public static ItemBlockWithTooltip itemHolderOpaque = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.itemHolderOpaque));
	public static ItemBlockWithTooltip itemHolderTransparent = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.itemHolderTransparent));
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(enderBox);
		registry.register(itemHolderOpaque);
		registry.register(itemHolderTransparent);
	}
	
	public static Item item(Item item, String name) {
		item.setRegistryName(HandyMods.resourceLocation(name));
		item.setUnlocalizedName(HandyMods.namespaced(name));
		return item;
	}
	
	public static <IB extends ItemBlock> IB itemBlock(IB item) {
		item.setRegistryName(item.getBlock().getRegistryName());
		item.setUnlocalizedName(item.getBlock().getUnlocalizedName());
		return item;
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
