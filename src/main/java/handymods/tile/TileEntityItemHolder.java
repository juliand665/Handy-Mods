package handymods.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityItemHolder extends ModContainerTileEntity {
	public ItemStackHandler newItemHandler() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				contentsChanged();
				super.onContentsChanged(slot);
			}
			
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};
	}
	
	public ItemStack getItemStack() {
		return itemHandler.getStackInSlot(0);
	}
	
	public void setItemStack(ItemStack itemStack) {
		itemHandler.setStackInSlot(0, itemStack);
	}
}