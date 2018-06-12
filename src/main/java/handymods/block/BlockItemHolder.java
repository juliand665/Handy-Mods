package handymods.block;

import handymods.CreativeTabHandyMods;
import handymods.tile.TileEntityItemHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockItemHolder extends BlockWithTileEntity<TileEntityItemHolder> {
	public BlockItemHolder() {
		super(Material.IRON);
		
		setHardness(1F);
		setResistance(3F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabHandyMods.instance);
	}
	
	@Override
	public TileEntityItemHolder newTileEntity(IBlockAccess world, IBlockState state) {
		return new TileEntityItemHolder();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(
				0.125f, 0.125f, 0.125f,
				0.875f, 0.875f, 0.875f
		);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking())
			return false;
		
		if (world.isRemote)
			return true;
		
		TileEntityItemHolder tileEntity = tileEntity(world, pos);
		ItemStack current = tileEntity.getItemStack();
		if (current.isEmpty()) {
			ItemStack taken = player.inventory.getCurrentItem().splitStack(1);
			tileEntity.setItemStack(taken);
		} else {
			tileEntity.setItemStack(ItemStack.EMPTY);
			boolean couldAdd = player.inventory.addItemStackToInventory(current);
			if (!couldAdd) {
				InventoryHelper.spawnItemStack(
						world,
						pos.getX() + hitX,
						pos.getY() + hitY,
						pos.getZ() + hitZ,
						current
				);
			}
		}
		return true;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		TileEntityItemHolder tileEntity = tileEntity(world, pos);
		return tileEntity.getItemStack().isEmpty() ? 0 : 15;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		tileEntity(world, pos).dropContents();
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT; // for transparency
	}
}
