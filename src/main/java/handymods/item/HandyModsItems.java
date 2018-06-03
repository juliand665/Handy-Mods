package handymods.item;

import handymods.HandyMods;
import net.minecraft.item.Item;
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
		
		registry.register(item(paperBox, "paper_box")); // TODO don't set names here
	}
	
	public static Item item(Item item, String name) {
		return item
				.setUnlocalizedName(name)
				.setRegistryName(HandyMods.resourceLocation(name));
	}
}
