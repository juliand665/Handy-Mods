package handymods.tile;

import handymods.HandyModsConfig;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEnderBox extends ModTileEntity {
	private static final String NBT_KEY_STORED_BLOCK = "storedBlock";
	
	public BlockData storedBlock = new BlockData(); // boxes are never empty, but this avoids crashing if you place them using commands
	
	@Override
	public void readFrom(NBTTagCompound container) {
		storedBlock = new BlockData(container.getCompoundTag(NBT_KEY_STORED_BLOCK));
	}
	
	@Override
	public void writeTo(NBTTagCompound container) {
		assert (container.hasKey(NBT_KEY_STORED_BLOCK));
		container.setTag(NBT_KEY_STORED_BLOCK, storedBlock.getNBT());
	}
	
	@Override
	public double getMaxRenderDistanceSquared() {
		return HandyModsConfig.renderEnderBoxContents ? super.getMaxRenderDistanceSquared() : 0f;
	}
}
