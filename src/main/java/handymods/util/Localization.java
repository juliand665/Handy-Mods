package handymods.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Localization {
	/** e.g. keyString("tooltip", enderBox, "empty") -> "tooltip.handymods.ender_box.empty") */
	public static String keyString(String domain, IForgeRegistryEntry entry, String path) {
		final ResourceLocation location = entry.getRegistryName();
		return domain + "." + location.getResourceDomain() + "." + location.getResourcePath() + "." + path;
	}
	
	public static String localized(String domain, IForgeRegistryEntry entry, String path, Object... params) {
		return I18n.format(keyString(domain, entry, path), params);
	}
}
