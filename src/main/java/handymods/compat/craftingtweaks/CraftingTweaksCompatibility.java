package handymods.compat.craftingtweaks;

import handymods.inventory.ContainerChestyCraftingTable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class CraftingTweaksCompatibility {
	public static void setUp() {
		NBTTagCompound body = new NBTTagCompound();
		body.setString("ContainerClass", ContainerChestyCraftingTable.class.getName());
		body.setString("AlignToGrid", "left");
		FMLInterModComms.sendMessage("craftingtweaks", "RegisterProvider", body);
	}
}
