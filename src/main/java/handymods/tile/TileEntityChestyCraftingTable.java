package handymods.tile;

import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

public class TileEntityChestyCraftingTable extends ModContainerTileEntity {
	private Optional<Listener> listener = Optional.empty();
	
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
	
	public void setListener(Listener listener) {
		this.listener = Optional.of(listener);
	}
	
	public void removeListener() {
		this.listener = Optional.empty();
	}
	
	@Override
	public void contentsChanged() {
		super.contentsChanged();
		
		listener.ifPresent(Listener::tileEntityContentsChanged);
	}
	
	public interface Listener {
		void tileEntityContentsChanged();
	}
}
