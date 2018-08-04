package handymods.inventory;

import handymods.tile.TileEntityChestyCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerChestyCraftingTable extends Container implements TileEntityChestyCraftingTable.Listener {
	public final InventoryCrafting craftMatrix;
	public final InventoryCraftResult craftResult = new InventoryCraftResult();
	private final World world;
	private final BlockPos pos;
	private final EntityPlayer player;
	private final TileEntityChestyCraftingTable tileEntity;
	
	public ContainerChestyCraftingTable(InventoryPlayer playerInventory, World world, BlockPos pos) {
		super();
		
		this.world = world;
		this.pos = pos;
		this.player = playerInventory.player;
		this.tileEntity = (TileEntityChestyCraftingTable) world.getTileEntity(pos);
		assert tileEntity != null;
		this.craftMatrix = new InventoryChestyCrafting(this, tileEntity.itemHandler);
		
		addOwnSlots();
		addPlayerSlots(playerInventory);
		slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
		
		tileEntity.addListener(this);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		tileEntity.removeListener(this);
		
		super.onContainerClosed(player);
	}
	
	private void addOwnSlots() {
		// crafting result
		addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 124, 35));
		
		// crafting grid
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlotToContainer(new Slot(craftMatrix, x + 3 * y, 30 + 18 * x, 17 + 18 * y));
			}
		}
	}
	
	private void addPlayerSlots(InventoryPlayer playerInventory) {
		// inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(playerInventory, 9 + x + 9 * y, 8 + 18 * x, 84 + 18 * y));
			}
		}
		
		// hotbar
		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(playerInventory, x, 8 + 18 * x, 142));
		}
	}
	
	@Override
	public void tileEntityContentsChanged() {
		onCraftMatrixChanged(craftMatrix);
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSqToCenter(pos) <= 64.0D;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		// shamelessly copied from ContainerWorkbench
		
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (index == 0) {
				itemstack1.getItem().onCreated(itemstack1, world, player);
				
				if (!mergeItemStack(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
				
				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 10 && index < 37) {
				if (!mergeItemStack(itemstack1, 37, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 37 && index < 46) {
				if (!mergeItemStack(itemstack1, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(itemstack1, 10, 46, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			ItemStack itemstack2 = slot.onTake(player, itemstack1);
			
			if (index == 0) {
				player.dropItem(itemstack2, false);
			}
		}
		
		return itemstack;
	}
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return slot.inventory != craftResult && super.canMergeSlot(stack, slot);
	}
}
