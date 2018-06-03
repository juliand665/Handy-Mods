package handymods.block;

import handymods.HandyMods;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class HandyModsBlocks {
	public static BlockPaperBox paperBox = new BlockPaperBox();
	public static Block chestyCraftingTable; // TODO
	public static Block itemHolder; // TODO
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		
		registry.register(block(paperBox, "paper_box"));
		//registry.register(block(chestyCraftingTable, "ChestyCraftingTable"));
		//registry.register(block(itemHolder, "ItemHolder"));
	}
	
	public static Block block(Block block, String name) {
		return block
				.setUnlocalizedName(name)
				.setRegistryName(HandyMods.resourceLocation(name));
	}
}