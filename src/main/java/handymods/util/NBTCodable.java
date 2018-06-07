package handymods.util;

import net.minecraft.nbt.NBTTagCompound;

public abstract class NBTCodable implements INBTCodable {
	protected NBTCodable() {}
	
	public NBTCodable(NBTTagCompound container) {
		readFrom(container);
	}
	
	public NBTTagCompound getNBT() {
		NBTTagCompound container = new NBTTagCompound();
		writeTo(container);
		return container;
	}
}
