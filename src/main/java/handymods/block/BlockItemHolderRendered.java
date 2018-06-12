package handymods.block;

import handymods.tile.TileEntityItemHolder;
import handymods.tile.TileEntityItemHolderRendered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;

public class BlockItemHolderRendered extends BlockItemHolder {
	@Override
	public TileEntityItemHolder newTileEntity(IBlockAccess world, IBlockState state) {
		return new TileEntityItemHolderRendered();
	}
}
