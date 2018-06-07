package handymods;

import org.apache.logging.log4j.Logger;

import handymods.tile.TileEntityEnderBox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = HandyMods.MOD_ID, useMetadata = true)
public class HandyMods {
	public static final String MOD_ID = "handymods";
	
	private static Logger logger;
	
	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	
	public static String namespaced(String path) {
		return MOD_ID + "." + path;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}
	
	@EventHandler
	public void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
		addEntities();
	}
	
	private void addEntities() {
		GameRegistry.registerTileEntity(TileEntityEnderBox.class, resourceLocation("ender_box"));
	}
}
