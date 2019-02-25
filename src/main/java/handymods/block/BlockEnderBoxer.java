package handymods.block;

import handymods.CreativeTabHandyMods;
import handymods.tile.BlockData;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockEnderBoxer extends BlockDirectional {
	public BlockEnderBoxer() {
		super(Material.ROCK);
		
		setHardness(0.2F);
		setResistance(1F);
		setSoundType(SoundType.STONE);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
		setCreativeTab(CreativeTabHandyMods.instance);
	}
	
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}
	
	public IBlockState withRotation(IBlockState state, Rotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withProperty(FACING, mirror.mirror(state.getValue(FACING)));
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		updateState(world, pos, state);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		releaseBlock(world, pos, state);
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, block, fromPos);
		
		updateState(world, pos, state);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
		
		updateState(world, pos, state);
	}
	
	private void updateState(World world, BlockPos pos, IBlockState state) {
		boolean hasBlock = isTargetBoxed(world, pos, state);
		boolean isPowered = world.isBlockPowered(pos);
		
		if (hasBlock && !isPowered) {
			releaseBlock(world, pos, state);
		} else if (!hasBlock && isPowered) {
			captureBlock(world, pos, state);
		}
	}
	
	private boolean isTargetBoxed(World world, BlockPos pos, IBlockState state) {
		final BlockPos targetPos = pos.offset(state.getValue(FACING));
		final IBlockState targetState = world.getBlockState(targetPos);
		return targetState.getBlock() instanceof BlockEnderBoxed;
	}
	
	private void captureBlock(World world, BlockPos pos, IBlockState state) {
		if (isTargetBoxed(world, pos, state)) return; // already boxed
		
		final BlockPos targetPos = pos.offset(state.getValue(FACING));
		HandyModsBlocks.enderBox.wrapBlock(world, targetPos, HandyModsBlocks.enderBoxed.getDefaultState());
	}
	
	private void releaseBlock(World world, BlockPos pos, IBlockState state) {
		if (!isTargetBoxed(world, pos, state)) return; // not boxed
		
		final BlockPos targetPos = pos.offset(state.getValue(FACING));
		HandyModsBlocks.enderBox.unwrapBlock(world, targetPos, BlockData::getStoredState);
	}
}
