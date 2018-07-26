package handymods.tile;

import net.minecraftforge.items.ItemStackHandler;

public class TileEntityChestyCraftingTable extends ModContainerTileEntity {
	@Override
	protected ItemStackHandler newItemHandler() {
		return new ItemStackHandler(9) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				
				System.out.println("Contents changed!");
				contentsChanged();
			}
			
			@Override
			protected void onLoad() {
				super.onLoad();
				
				System.out.println("Contents loaded!");
				contentsChanged();
			}
		};
	}
}
