package handymods;

import org.apache.logging.log4j.Logger;

import handymods.tile.TileEntityPaperBox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = HandyMods.MOD_ID, name = HandyMods.NAME, version = HandyMods.VERSION)
public class HandyMods {
	public static final String MOD_ID = "handymods";
	public static final String NAME = "Handy Mods";
	public static final String VERSION = "1.0";
	
	private static Logger logger;
	
	public static ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(MOD_ID, path);
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
		GameRegistry.registerTileEntity(TileEntityPaperBox.class, resourceLocation("paper_box"));
	}
}
