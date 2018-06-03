package handymods.item;

import handymods.HandyMods;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class HandyModsItems {
	public static ItemBlockPaperBox paperBox = new ItemBlockPaperBox();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(paperBox);
	}
	
	public static Item item(Item item, String name) {
		return item
				.setUnlocalizedName(name)
				.setRegistryName(HandyMods.resourceLocation(name));
	}
	
	@SubscribeEvent
	public static void registerModels(@SuppressWarnings("unused") ModelRegistryEvent event) {
		registerItemModel(HandyModsItems.paperBox);
	}
	
	private static void registerItemModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
