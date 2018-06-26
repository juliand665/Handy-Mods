package handymods.inventory;

import handymods.tile.TileEntityChestyCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerChestyCraftingTable extends Container {
	public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public final InventoryCraftResult craftResult = new InventoryCraftResult();
	private final World world;
	private final BlockPos pos;
	private final EntityPlayer player;
	private final TileEntityChestyCraftingTable tileEntity;
	private boolean isLoading = true;
	
	public ContainerChestyCraftingTable(InventoryPlayer playerInventory, World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
		this.player = playerInventory.player;
		this.tileEntity = (TileEntityChestyCraftingTable) world.getTileEntity(pos);
		
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}
	
	private void addOwnSlots() {
		addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 124, 35));
		
		for (int i = 0; i < 9; i++) {
			System.out.println("Contents of " + i + ": " + tileEntity.itemHandler.getStackInSlot(i));
			craftMatrix.setInventorySlotContents(i, tileEntity.itemHandler.getStackInSlot(i));
		}
		isLoading = false;
		slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlotToContainer(new Slot(craftMatrix, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}
	}
	
	private void addPlayerSlots(InventoryPlayer playerInventory) {
		// inventory
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		
		// hotbar
		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
		}
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		if (isLoading)
			return;
		System.out.println("Crafting matrix changed!");
		for (int i = 0; i < 9; i++) {
			System.out.println("\t" + craftMatrix.getStackInSlot(i));
			tileEntity.itemHandler.setStackInSlot(i, craftMatrix.getStackInSlot(i));
		}
		slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
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
