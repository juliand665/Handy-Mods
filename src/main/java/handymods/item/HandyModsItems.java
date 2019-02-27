package handymods.item;

import handymods.block.HandyModsBlocks;
import handymods.client.render.RenderEnderBoxItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@Mod.EventBusSubscriber
public class HandyModsItems {
	private static final List<Item> ITEMS = new ArrayList<>(); // has to be on top so it's loaded first
	
	public static final ItemBlockEnderBox enderBox = itemBlock(new ItemBlockEnderBox());
	public static final ItemBlockWithTooltip itemHolderOpaque = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.itemHolderOpaque));
	public static final ItemBlockWithTooltip itemHolderTransparent = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.itemHolderTransparent));
	public static final ItemBlockWithTooltip chestyCraftingTable = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.chestyCraftingTable));
	public static final ItemBlock enderBoxer = itemBlock(new ItemBlockWithTooltip(HandyModsBlocks.enderBoxer));
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		ITEMS.forEach(registry::register);
	}
	
	/*
	private static Item item(Item item, String name) {
		item.setRegistryName(HandyMods.resourceLocation(name));
		item.setUnlocalizedName(HandyMods.namespaced(name));
		ITEMS.add(item);
		return item;
	}
	*/
	
	private static <IB extends ItemBlock> IB itemBlock(IB item) {
		ResourceLocation registryName = item.getBlock().getRegistryName();
		assert registryName != null;
		item.setRegistryName(registryName);
		item.setTranslationKey(item.getBlock().getTranslationKey());
		ITEMS.add(item);
		return item;
	}
	
	@SubscribeEvent
	@SideOnly(CLIENT)
	public static void registerModels(@SuppressWarnings("unused") ModelRegistryEvent event) {
		ITEMS.forEach(item -> {
			ResourceLocation registryName = item.getRegistryName();
			assert registryName != null;
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(registryName, "inventory"));
		});
		
		enderBox.setTileEntityItemStackRenderer(new RenderEnderBoxItem());
	}
}
