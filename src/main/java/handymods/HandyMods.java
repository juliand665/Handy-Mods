package handymods;

import handymods.client.gui.GuiProxy;
import handymods.compat.craftingtweaks.CraftingTweaksCompatibility;
import handymods.tile.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = HandyMods.MOD_ID, version = "__VERSION__", useMetadata = true, dependencies = "after:theoneprobe")
public class HandyMods {
	public static final String MOD_ID = "handymods";
	
	@Mod.Instance
	public static HandyMods instance;
	public static Logger logger;
	
	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	
	public static String namespaced(String path) {
		return MOD_ID + "." + path;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		CraftingTweaksCompatibility.setUp();
	}
	
	@EventHandler
	public void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
		addEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiProxy());
	}
	
	private void addEntities() {
		GameRegistry.registerTileEntity(TileEntityEnderBox.class, resourceLocation("ender_box"));
		GameRegistry.registerTileEntity(TileEntityItemHolder.class, resourceLocation("item_holder"));
		GameRegistry.registerTileEntity(TileEntityItemHolderRendered.class, resourceLocation("item_holder_rendered"));
		GameRegistry.registerTileEntity(TileEntityChestyCraftingTable.class, resourceLocation("chesty_crafting_table"));
	}
}
