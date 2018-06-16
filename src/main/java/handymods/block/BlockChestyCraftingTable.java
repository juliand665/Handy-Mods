package handymods.block;

import handymods.CreativeTabHandyMods;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockChestyCraftingTable extends Block {
	public static final PropertyDirection PROPERTY_FACING = BlockHorizontal.FACING;
	
	// for more accurate bounding boxes
	private static final List<AxisAlignedBB> bounds = new ArrayList<>();
	static {
		bounds.add(new AxisAlignedBB(0.00, 0.75, 0.00, 1.00, 1.00, 1.00)); // top
		bounds.add(new AxisAlignedBB(0.00, 0.00, 0.00, 0.25, 0.75, 0.25)); // leg 00
		bounds.add(new AxisAlignedBB(0.00, 0.00, 0.75, 0.25, 0.75, 1.00)); // leg 01
		bounds.add(new AxisAlignedBB(0.75, 0.00, 0.00, 1.00, 0.75, 0.25)); // leg 10
		bounds.add(new AxisAlignedBB(0.75, 0.00, 0.75, 1.00, 0.75, 1.00)); // leg 11
		bounds.add(new AxisAlignedBB(0.25, 0.00, 0.25, 0.75, 0.50, 0.75)); // chest
	}
	
	public BlockChestyCraftingTable() {
		super(Material.WOOD);
		
		setHardness(2F);
		setResistance(1F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabHandyMods.instance);
		
		setDefaultState(blockState.getBaseState()
				.withProperty(PROPERTY_FACING, EnumFacing.NORTH));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		// necessary? no. fancy? hell yes.
		for (AxisAlignedBB box : bounds) {
			RayTraceResult result = rayTrace(pos, start, end, box);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PROPERTY_FACING);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(PROPERTY_FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing facing = state.getValue(PROPERTY_FACING);
		return facing.getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta);
		return getDefaultState().withProperty(PROPERTY_FACING, facing);
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rotation) {
		EnumFacing facing = state.getValue(PROPERTY_FACING);
		return state.withProperty(PROPERTY_FACING, rotation.rotate(facing));
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		EnumFacing facing = state.getValue(PROPERTY_FACING);
		return state.withProperty(PROPERTY_FACING, mirror.mirror(facing));
	}
}
