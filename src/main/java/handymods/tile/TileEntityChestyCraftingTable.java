package handymods.tile;

import net.minecraftforge.items.ItemStackHandler;

public class TileEntityChestyCraftingTable extends ModContainerTileEntity {
	@Override
	protected ItemStackHandler newItemHandler() {
		return new ItemStackHandler(9) {
			@Override
			protected void onContentsChanged(int slot) {
				contentsChanged();
				super.onContentsChanged(slot);
			}
		};
	}
}
