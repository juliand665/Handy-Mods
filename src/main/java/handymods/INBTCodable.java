package handymods;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTCodable {
	public void readFrom(NBTTagCompound container);
	public void writeTo(NBTTagCompound container);
}
