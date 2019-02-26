package handymods.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/* provides a foolproof implementation of a simple block with a tile entity */
public abstract class BlockWithTileEntity<TE extends TileEntity> extends Block {
	public BlockWithTileEntity(Material material) {
		super(material);
	}
	
	public BlockWithTileEntity(Material material, MapColor mapColor) {
		super(material, mapColor);
	}
	
	@Override
	public final boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public final TileEntity createTileEntity(World world, IBlockState state) {
		return newTileEntity(world, state);
	}
	
	public abstract TE newTileEntity(IBlockAccess world, IBlockState state);
	
	@SuppressWarnings("unchecked")
	public final TE tileEntity(IBlockAccess world, BlockPos pos) {
		TE tileEntity = (TE) world.getTileEntity(pos);
		assert tileEntity != null;
		return tileEntity;
	}
}
