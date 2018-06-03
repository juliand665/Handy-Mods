package handymods.item;

import java.util.List;
import java.util.Optional;

import handymods.block.HandyModsBlocks;
import handymods.tile.TileEntityPaperBox;
import handymods.tile.TileEntityPaperBox.BlockData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemBlockPaperBox extends ItemBlock {
	static final String BLOCK_DATA_KEY = "blockData";
	
	public ItemBlockPaperBox() {
		super(HandyModsBlocks.paperBox);
		MinecraftForge.EVENT_BUS.register(this);
		
		setRegistryName(block.getRegistryName());
		setUnlocalizedName(block.getUnlocalizedName());
	}
	
	@Override
	public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(itemStack, world, tooltip, flag);
		
		String contentsDesc; // TODO localize
		if (hasBlockData(itemStack)) {
			BlockData blockData = getBlockData(itemStack);
			contentsDesc = "Contains " + blockData.block.getLocalizedName();
		} else {
			contentsDesc = "Empty";
		}
		
		tooltip.add(contentsDesc);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (world.isRemote)
			return true;
		
		if (!hasBlockData(itemStack)) // disallow placing empty boxes
			return false;
		
		boolean shouldPlace = super.placeBlockAt(itemStack, player, world, pos, side, hitX, hitY, hitZ, newState);
		
		if (shouldPlace) {
			TileEntityPaperBox tileEntity = (TileEntityPaperBox) world.getTileEntity(pos);
			tileEntity.storedBlock = getBlockData(itemStack);
		}
		
		return shouldPlace;
	}
	
	// placing around block
	
	private boolean isCancellingItemDrops = false;
	
	// onItemUseFirst is necessary to avoid opening the block's GUI instead
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (world.isRemote)
			return EnumActionResult.PASS;
		
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int metadata = block.getMetaFromState(state);
		
		// only pick up valid blocks
		// FIXME prevent recursive boxing
		if (world.isAirBlock(pos) || state.getBlockHardness(world, pos) < 0)
			return EnumActionResult.PASS;
		
		ItemStack itemStack = player.getHeldItem(hand);
		
		// already contains block
		if (hasBlockData(itemStack))
			return EnumActionResult.PASS;
		
		Optional<NBTTagCompound> tileEntityNBT = Optional.ofNullable(world.getTileEntity(pos))
				.map(prev -> prev.writeToNBT(new NBTTagCompound()));
		
		if (!player.capabilities.isCreativeMode) {
			itemStack.shrink(1);
		}
		
		// TODO this might cause duping glitches with some multiblock machines
		// first, remove the block without causing updates, voiding any ensuing drops
		isCancellingItemDrops = true;
		IBlockState newState = HandyModsBlocks.paperBox.getDefaultState();
		world.setBlockState(pos, newState, 0b00010);
		isCancellingItemDrops = false;
		// now, cause the update we prevented earlier, to make sure everything is in a nice state
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, newState, 0b11101);
		
		TileEntityPaperBox newTileEntity = (TileEntityPaperBox) world.getTileEntity(pos);
		newTileEntity.storedBlock = new BlockData(block, metadata, tileEntityNBT);
		
		return EnumActionResult.SUCCESS;
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if (isCancellingItemDrops && event.getEntity() instanceof EntityItem) {
			// If the block we're wrapping drops something on being destroyed, we have to cancel it to avoid duping.
			event.setCanceled(true);
		}
	}
	
	// ItemStack helpers
	
	private static NBTTagCompound tagOf(ItemStack itemStack) {
		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		return itemStack.getTagCompound();
	}
	
	public static boolean hasBlockData(ItemStack itemStack) {
		return tagOf(itemStack).hasKey(BLOCK_DATA_KEY);
	}
	
	public static BlockData getBlockData(ItemStack itemStack) {
		NBTTagCompound tag = tagOf(itemStack).getCompoundTag(BLOCK_DATA_KEY);
		return new BlockData(tag);
	}
	
	public static void setBlockData(ItemStack itemStack, BlockData blockData) {
		NBTTagCompound tag = blockData.getNBT();
		tagOf(itemStack).setTag(BLOCK_DATA_KEY, tag);
	}
}
