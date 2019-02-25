package handymods.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnderBoxed extends BlockEnderBox {
	public BlockEnderBoxed() {
		super();
		
		setBlockUnbreakable();
		setResistance(1000F);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
	protected ItemStack getDroppedItem(IBlockAccess world, BlockPos pos) {
		return ItemStack.EMPTY;
	}
}
