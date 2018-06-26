package handymods.client.gui;

import handymods.block.BlockChestyCraftingTable;
import handymods.inventory.ContainerChestyCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiProxy implements IGuiHandler {
	@Nullable
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (id) {
			case BlockChestyCraftingTable.GUI_ID:
				return new ContainerChestyCraftingTable(player.inventory, world, pos);
			default:
				return null;
		}
	}
	
	@Nullable
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (id) {
			case BlockChestyCraftingTable.GUI_ID:
				return new GuiChestyCraftingTable(new ContainerChestyCraftingTable(player.inventory, world, pos));
			default:
				return null;
		}
	}
}
