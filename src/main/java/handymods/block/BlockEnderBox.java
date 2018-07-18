package handymods.block;

import handymods.CreativeTabHandyMods;
import handymods.compat.theoneprobe.IProbeInfoAccessor;
import handymods.item.ItemBlockEnderBox;
import handymods.tile.TileEntityEnderBox;
import handymods.tile.TileEntityEnderBox.BlockData;
import handymods.util.SoundHelpers;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnderBox extends BlockWithTileEntity<TileEntityEnderBox> implements IProbeInfoAccessor {
	public BlockEnderBox() {
		super(Material.CLOTH); // harvestable by hand; mostly for convenience
		
		setHardness(0.2F);
		setResistance(1F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabHandyMods.instance);
	}
	
	@Override
	public TileEntityEnderBox newTileEntity(IBlockAccess world, IBlockState state) {
		return new TileEntityEnderBox();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!player.isSneaking())
			return false;
		
		if (world.isRemote) {
			SoundHelpers.playPlacementSound(player, pos, this);
			return true;
		}
		
		final TileEntityEnderBox tileEntity = tileEntity(world, pos);
		final BlockData blockData = tileEntity.storedBlock;
		
		final IBlockState newState = blockData.getStateForPlacement(world, pos, facing, new Vec3d(hitX, hitY, hitZ), player, hand);
		world.setBlockState(pos, newState, 0b11);
		
		blockData.updatePosition(pos);
		blockData.tileEntityNBT.ifPresent(nbt -> world.getTileEntity(pos).readFromNBT(nbt));
		
		blockData.block.onBlockPlacedBy(world, pos, newState, player, new ItemStack(blockData.block, 1, blockData.metadata));
		world.scheduleUpdate(pos, blockData.block, 0); // e.g. makes sugar cane pop off if placed invalidly, but unfortunately doesn't affect cactus
		
		if (!player.capabilities.isCreativeMode) {
			InventoryHelper.spawnItemStack(
					world,
					pos.getX() + hitX,
					pos.getY() + hitY,
					pos.getZ() + hitZ,
					new ItemStack(HandyModsBlocks.enderBox)
			);
		}
		
		return true;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getDroppedItem(world, pos);
	}
	
	private ItemStack getDroppedItem(IBlockAccess world, BlockPos pos) {
		final TileEntityEnderBox tileEntity = tileEntity(world, pos);
		final ItemStack itemStack = new ItemStack(this);
		ItemBlockEnderBox.setBlockData(itemStack, tileEntity.storedBlock);
		return itemStack;
	}
	
	// drop handling
	
	private ItemStack droppedItem;
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		droppedItem = getDroppedItem(world, pos);
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(droppedItem);
	}
	
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		final TileEntityEnderBox tileEntity = tileEntity(world, data.getPos());
		final BlockData storedBlock = tileEntity.storedBlock;
		
		// i hate getters
		final IBlockState state = storedBlock.getStateForPlacement(world, data.getPos(), data.getSideHit(), data.getHitVec(), player, player.getActiveHand());
		ItemStack containedBlock;
		try {
			// try to simulate picking the block
			containedBlock = storedBlock.block.getPickBlock(state, new RayTraceResult(data.getHitVec(), data.getSideHit(), data.getPos()), world, data.getPos(), player);
		} catch (Exception e) { // could fail e.g. because the pick block code expects a certain tile entity
			// fallback to simpler means
			containedBlock = storedBlock.getPickBlockFallback(state);
		}
		
		probeInfo.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
				.item(containedBlock)
				.itemLabel(containedBlock);
	}
}
