package handymods;

import handymods.item.HandyModsItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabHandyMods extends CreativeTabs {
	public static CreativeTabHandyMods instance = new CreativeTabHandyMods();
	
	private CreativeTabHandyMods() {
		super("tabHandyMods");
	}
	
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(HandyModsItems.paperBox);
	}
}
