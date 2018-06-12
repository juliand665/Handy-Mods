package handymods.block;

import handymods.HandyMods;
import handymods.block.render.RenderItemHolder;
import handymods.tile.TileEntityItemHolder;
import handymods.tile.TileEntityItemHolderRendered;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class HandyModsBlocks {
	public static BlockEnderBox enderBox = new BlockEnderBox();
	public static Block chestyCraftingTable; // TODO
	public static Block itemHolderOpaque = new BlockItemHolder();
	public static Block itemHolderTransparent = new BlockItemHolderRendered();
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		
		registry.register(block(enderBox, "ender_box"));
		//registry.register(block(chestyCraftingTable, "chesty_crafting_table"));
		registry.register(block(itemHolderOpaque, "item_holder_opaque"));
		registry.register(block(itemHolderTransparent, "item_holder_transparent"));
	}
	
	@SubscribeEvent
	public static void registerModels(@SuppressWarnings("unused") ModelRegistryEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemHolderRendered.class, new RenderItemHolder());
	}
	
	public static Block block(Block block, String name) {
		return block
				.setRegistryName(HandyMods.resourceLocation(name))
				.setUnlocalizedName(HandyMods.namespaced(name));
	}
}
