package handymods.block;

import handymods.CreativeTabHandyMods;
import handymods.item.ItemBlockEnderBox;
import handymods.tile.TileEntityEnderBox;
import handymods.tile.TileEntityEnderBox.BlockData;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockEnderBox extends BlockWithTileEntity<TileEntityEnderBox> {
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
		
		if (world.isRemote)
			return true;
		
		final TileEntityEnderBox tileEntity = tileEntity(world, pos);
		final BlockData blockData = tileEntity.storedBlock;
		
		final IBlockState newState = blockData.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, blockData.metadata, player, hand);
		world.setBlockState(pos, newState, 0b11);
		
		blockData.updatePosition(pos);
		blockData.tileEntityNBT.ifPresent(nbt -> world.getTileEntity(pos).readFromNBT(nbt));
		
		blockData.block.onBlockPlacedBy(world, pos, newState, player, new ItemStack(blockData.block, 1, blockData.metadata));
		world.scheduleUpdate(pos, blockData.block, 0); // e.g. makes sugar cane pop off if placed invalidly, but unfortunately doesn't affect cactus
		
		if (!player.capabilities.isCreativeMode) {
			InventoryHelper.spawnItemStack(world,
					pos.getX() + hitX,
					pos.getY() + hitY,
					pos.getZ() + hitZ,
					new ItemStack(HandyModsBlocks.enderBox));
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
}
