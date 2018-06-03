package handymods.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockWithTileEntity<TE extends TileEntity> extends Block {
	public BlockWithTileEntity(Material material) {
		super(material);
	}
	
	public BlockWithTileEntity(Material material, MapColor mapColor) {
		super(material, mapColor);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	public TE tileEntity(IBlockAccess world, BlockPos pos) {
		return (TE) world.getTileEntity(pos);
	}
}
