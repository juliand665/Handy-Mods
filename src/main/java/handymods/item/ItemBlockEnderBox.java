package handymods.item;

import handymods.block.HandyModsBlocks;
import handymods.tile.TileEntityEnderBox;
import handymods.tile.TileEntityEnderBox.BlockData;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static handymods.util.Localization.localized;

@Mod.EventBusSubscriber
public class ItemBlockEnderBox extends ItemBlock {
	private static final String NBT_KEY_BLOCK_DATA = "blockData";
	
	public ItemBlockEnderBox() {
		super(HandyModsBlocks.enderBox);

		// noinspection ConstantConditions
		setRegistryName(block.getRegistryName());
		setUnlocalizedName(block.getUnlocalizedName());
	}
	
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(itemStack, world, tooltip, flag);
		
		final String contentsDesc;
		if (hasBlockData(itemStack)) {
			final BlockData blockData = getBlockData(itemStack);
			final Block block = blockData.block;
			if (flag.isAdvanced()) {
				contentsDesc = localized("tooltip", this, "contains_block.advanced", block.getLocalizedName(), block.getRegistryName());
			} else {
				contentsDesc = localized("tooltip", this, "contains_block", block.getLocalizedName());
			}
		} else {
			contentsDesc = localized("tooltip", this, "empty");
		}
		
		tooltip.add(contentsDesc);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		if (hasBlockData(stack)) {
			return super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
		} else {
			return true; // TODO blacklist
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (world.isRemote)
			return true;
		
		if (!hasBlockData(itemStack)) // disallow placing empty boxes
			return false;
		
		final boolean shouldPlace = super.placeBlockAt(itemStack, player, world, pos, side, hitX, hitY, hitZ, newState);
		
		if (shouldPlace) {
			final TileEntityEnderBox tileEntity = HandyModsBlocks.enderBox.tileEntity(world, pos);
			tileEntity.storedBlock = getBlockData(itemStack);
		}
		
		return shouldPlace;
	}
	
	// placing around block
	
	private static boolean isCancellingItemDrops = false;
	
	// onItemUseFirst is necessary to avoid opening the block's GUI instead
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		final ItemStack itemStack = player.getHeldItem(hand);

		// already contains block
		if (hasBlockData(itemStack))
			return EnumActionResult.PASS;

		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();
		final int metadata = block.getMetaFromState(state);

		if (world.isRemote) {
			// just assume it worked and play the sound
			SoundType soundtype = this.block.getSoundType(null, null, null, null);
			world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			return EnumActionResult.SUCCESS;
		}

		// only pick up valid blocks
		// FIXME prevent recursive boxing
		if (world.isAirBlock(pos) || state.getBlockHardness(world, pos) < 0)
			return EnumActionResult.PASS;
		
		final Optional<NBTTagCompound> tileEntityNBT = Optional
				.ofNullable(world.getTileEntity(pos))
				.map(prev -> prev.writeToNBT(new NBTTagCompound()));
		
		if (!player.capabilities.isCreativeMode) {
			itemStack.shrink(1);
		}
		
		// TODO this might cause duping glitches with some multiblock machines
		// first, remove the block without causing updates, voiding any ensuing drops
		isCancellingItemDrops = true;
		final IBlockState newState = HandyModsBlocks.enderBox.getDefaultState();
		world.setBlockState(pos, newState, 0b00010);
		isCancellingItemDrops = false;
		// now, cause the update we prevented earlier, to make sure everything is in a nice state
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, newState, 0b11101);
		
		final TileEntityEnderBox tileEntity = HandyModsBlocks.enderBox.tileEntity(world, pos);
		final TileEntityEnderBox newTileEntity = (TileEntityEnderBox) world.getTileEntity(pos);
		assert newTileEntity != null;
		newTileEntity.storedBlock = new BlockData(block, metadata, tileEntityNBT);
		
		return EnumActionResult.SUCCESS;
	}
	
	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
		// this is kinda hacky, but it's the cleanest way i can think of
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
		return tagOf(itemStack).hasKey(NBT_KEY_BLOCK_DATA);
	}
	
	public static BlockData getBlockData(ItemStack itemStack) {
		final NBTTagCompound tag = tagOf(itemStack).getCompoundTag(NBT_KEY_BLOCK_DATA);
		return new BlockData(tag);
	}
	
	public static void setBlockData(ItemStack itemStack, BlockData blockData) {
		final NBTTagCompound tag = blockData.getNBT();
		tagOf(itemStack).setTag(NBT_KEY_BLOCK_DATA, tag);
	}
}
