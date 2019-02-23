package handymods.tile;

import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class TileEntityChestyCraftingTable extends ModContainerTileEntity {
	private List<Listener> listeners = new ArrayList<>();
	
	@Override
	protected ItemStackHandler newItemHandler() {
		return new ItemStackHandler(9) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				
				contentsChanged();
			}
			
			@Override
			protected void onLoad() {
				super.onLoad();
				
				contentsChanged();
			}
		};
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void contentsChanged() {
		super.contentsChanged();
		
		listeners.forEach(Listener::tileEntityContentsChanged);
	}
	
	public interface Listener {
		void tileEntityContentsChanged();
	}
}
