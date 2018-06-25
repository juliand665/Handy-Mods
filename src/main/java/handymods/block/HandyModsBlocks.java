package handymods.block;

import handymods.HandyMods;
import handymods.block.render.RenderItemHolder;
import handymods.tile.TileEntityItemHolderRendered;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class HandyModsBlocks {
	private static final List<Block> BLOCKS = new ArrayList<>(); // has to be on top so it's loaded first
	
	public static BlockEnderBox enderBox = block(new BlockEnderBox(), "ender_box");
	public static BlockItemHolder itemHolderOpaque = block(new BlockItemHolder(), "item_holder_opaque");
	public static BlockItemHolderRendered itemHolderTransparent = block(new BlockItemHolderRendered(), "item_holder_transparent");
	public static BlockChestyCraftingTable chestyCraftingTable = block(new BlockChestyCraftingTable(), "chesty_crafting_table");
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		BLOCKS.forEach(registry::register);
	}
	
	@SubscribeEvent
	public static void registerModels(@SuppressWarnings("unused") ModelRegistryEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemHolderRendered.class, new RenderItemHolder());
	}
	
	public static <B extends Block> B block(B block, String name) {
		block.setRegistryName(HandyMods.resourceLocation(name));
		block.setUnlocalizedName(HandyMods.namespaced(name));
		BLOCKS.add(block);
		return block;
	}
}
