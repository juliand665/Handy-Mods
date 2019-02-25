package handymods.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public abstract class ModContainerTileEntity extends ModTileEntity {
	private static String NBT_KEY_CONTENTS = "contents";
	
	public final ItemStackHandler itemHandler = newItemHandler();
	
	protected abstract ItemStackHandler newItemHandler();
	
	@Override
	public void readFrom(NBTTagCompound container) {
		itemHandler.deserializeNBT(container.getCompoundTag(NBT_KEY_CONTENTS));
	}
	
	@Override
	public void writeTo(NBTTagCompound container) {
		container.setTag(NBT_KEY_CONTENTS, itemHandler.serializeNBT());
	}
	
	public void dropContents() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			InventoryHelper.spawnItemStack(
				world,
				(double) pos.getX(),
				(double) pos.getY(),
				(double) pos.getZ(),
				itemHandler.getStackInSlot(i)
			);
		}
	}
	
	public void contentsChanged() {
		if (world == null)
			return; // not loaded yet
		
		markDirty();
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 0b11);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		} else {
			return super.hasCapability(capability, facing);
		}
	}
	
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
		} else {
			return super.getCapability(capability, facing);
		}
	}
}
