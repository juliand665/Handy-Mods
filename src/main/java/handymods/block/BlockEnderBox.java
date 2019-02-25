package handymods.block;

import handymods.*;
import handymods.compat.theoneprobe.IProbeInfoAccessor;
import handymods.item.ItemBlockEnderBox;
import handymods.tile.BlockData;
import handymods.tile.TileEntityEnderBox;
import handymods.util.SoundHelpers;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Optional;
import java.util.function.Function;

public class BlockEnderBox extends BlockWithTileEntity<TileEntityEnderBox> implements IProbeInfoAccessor {
	public BlockEnderBox() {
		super(Material.CLOTH); // harvestable by hand; mostly for convenience
		
		setHardness(0.2F);
		setResistance(1F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabHandyMods.instance);
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return HandyModsConfig.renderEnderBoxContents ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
	}
	
	@Override
	public TileEntityEnderBox newTileEntity(IBlockAccess world, IBlockState state) {
		return new TileEntityEnderBox();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		final Function<BlockData, IBlockState> newState = blockData -> blockData.getStateForPlacement(world, pos, facing, new Vec3d(hitX, hitY, hitZ), player, hand);
		final BlockData placed = unwrapBlock(world, pos, newState);
		
		if (world.isRemote) {
			return true;
		}
		
		placed.getBlock().onBlockPlacedBy(world, pos, newState.apply(placed), player, placed.getPickedBlock(world, pos, null, player));
		
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
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		final TileEntityEnderBox tileEntity = tileEntity(world, data.getPos());
		final BlockData storedBlock = tileEntity.storedBlock;
		
		// i hate getters
		RayTraceResult target = new RayTraceResult(data.getHitVec(), data.getSideHit(), data.getPos());
		ItemStack containedBlock = storedBlock.getPickedBlock(world, data.getPos(), target, player);
		
		probeInfo.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
			.item(containedBlock)
			.itemLabel(containedBlock);
	}
	
	public boolean wrapBlock(World world, BlockPos targetPos, IBlockState newState) {
		final IBlockState targetState = world.getBlockState(targetPos);
		final Block targetBlock = targetState.getBlock();
		final int targetMetadata = targetBlock.getMetaFromState(targetState);
		
		System.out.println("wrapping");
		
		if (!ItemBlockEnderBox.canPickUp(targetState)) return false;
		
		if (world.isRemote) {
			// on the client, just assume it worked and play the sound
			SoundHelpers.playPlacementSound(world, targetPos, this);
			return true;
		}
		
		// capture tile entity
		final Optional<NBTTagCompound> targetTileEntityNBT = Optional
			.ofNullable(world.getTileEntity(targetPos))
			.map(prev -> prev.writeToNBT(new NBTTagCompound()));
		
		// replace block
		// We remove the tile entity before removing the block so the breakBlock() handler can't use it to drop items or cause flux etc.
		world.removeTileEntity(targetPos);
		// Since we removed the tile entity, we may be violating some assumptions, but that should only cause early exits from breakBlock(), which is called late enough that we don't break much.
		try {
			world.setBlockToAir(targetPos);
		} catch (Exception e) {
			HandyMods.logger.debug("ender boxing ignoring the following exception:");
			HandyMods.logger.debug(e);
		}
		world.setBlockState(targetPos, newState, 0b11);
		
		// store captured tile entity
		final TileEntityEnderBox newTileEntity = tileEntity(world, targetPos);
		newTileEntity.storedBlock = new BlockData(targetBlock, targetMetadata, targetTileEntityNBT);
		
		return true;
	}
	
	public BlockData unwrapBlock(World world, BlockPos targetPos, Function<BlockData, IBlockState> newState) {
		final TileEntityEnderBox tileEntity = tileEntity(world, targetPos);
		final BlockData blockData = tileEntity.storedBlock;
		
		System.out.println("unwrapping");
		
		if (world.isRemote) {
			SoundHelpers.playPlacementSound(world, targetPos, this);
			return blockData;
		}
		
		world.setBlockState(targetPos, newState.apply(blockData), 0b11);
		
		blockData.updatePosition(targetPos);
		blockData.getTileEntityNBT().ifPresent(nbt -> world.getTileEntity(targetPos).readFromNBT(nbt));
		
		world.scheduleUpdate(targetPos, blockData.getBlock(), 0); // e.g. makes sugar cane pop off if placed invalidly, but unfortunately doesn't affect cactus
		
		return blockData;
	}
	
	// drop handling
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getDroppedItem(world, pos);
	}
	
	protected ItemStack getDroppedItem(IBlockAccess world, BlockPos pos) {
		final TileEntityEnderBox tileEntity = tileEntity(world, pos);
		final ItemStack itemStack = new ItemStack(this);
		ItemBlockEnderBox.setBlockData(itemStack, tileEntity.storedBlock);
		return itemStack;
	}
	
	private ItemStack droppedItem;
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		droppedItem = getDroppedItem(world, pos); // have to store because the tile entity is removed before getDrops is called
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(droppedItem);
	}
}
