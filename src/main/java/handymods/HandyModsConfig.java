package handymods;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = HandyMods.MOD_ID)
public class HandyModsConfig {
	@Config.Name("Ender Box Blacklist")
	@Config.Comment("A list of blocks that can't be picked up with the ender box.")
	public static String[] enderBoxBlacklist = {
		"handymods:ender_box",
		"handymods:ender_boxed"
	};
	
	@Config.Name("Render Ender Box Contents")
	@Config.Comment("Controls whether or not the ender box should attempt to render its contents")
	public static boolean renderEnderBoxContents = true;
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.getModID().equals(HandyMods.MOD_ID)) return;
		
		boolean oldRenderEnderBoxContents = renderEnderBoxContents;
		ConfigManager.sync(HandyMods.MOD_ID, Config.Type.INSTANCE);
		if (event.isWorldRunning() && renderEnderBoxContents != oldRenderEnderBoxContents) {
			Minecraft.getMinecraft().renderGlobal.loadRenderers();
		}
	}
}
