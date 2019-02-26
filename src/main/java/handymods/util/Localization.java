package handymods.util;

import handymods.HandyMods;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Localization {
	/** e.g. keyString("tooltip", "ender_box.empty") -> "tooltip.handymods.ender_box.empty") */
	public static String keyString(String domain, String path) {
		return domain + "." + HandyMods.MOD_ID + "." + path;
	}
	
	public static String localized(String domain, IForgeRegistryEntry entry, String path, Object... params) {
		final ResourceLocation location = entry.getRegistryName();
		assert location != null;
		return localized(domain, location.getResourcePath() + "." + path, params);
	}
	
	public static String localized(String domain, String path, Object... params) {
		return I18n.format(keyString(domain, path), params);
	}
}
