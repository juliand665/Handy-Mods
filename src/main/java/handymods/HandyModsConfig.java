package handymods;

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
			"minecraft:bedrock",
			"handymods:ender_box"
	};
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(HandyMods.MOD_ID)) {
			ConfigManager.sync(HandyMods.MOD_ID, Config.Type.INSTANCE);
		}
	}
}
