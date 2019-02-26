package handymods;

import handymods.block.HandyModsBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabHandyMods extends CreativeTabs {
	public static CreativeTabHandyMods instance = new CreativeTabHandyMods();
	
	private CreativeTabHandyMods() {
		super("tabHandyMods");
	}
	
	@Override
	public ItemStack createIcon() {
		return new ItemStack(HandyModsBlocks.enderBox);
	}
}
