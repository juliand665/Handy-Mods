package handymods.item;

import handymods.HandyMods;
import handymods.HandyModsConfig;
import handymods.block.HandyModsBlocks;
import handymods.tile.TileEntityEnderBox;
import handymods.tile.TileEntityEnderBox.BlockData;
import handymods.util.SoundHelpers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static handymods.util.Localization.localized;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ItemBlockEnderBox extends ItemBlock {
	private static final String NBT_KEY_BLOCK_DATA = "blockData";
	
	public ItemBlockEnderBox() {
		super(HandyModsBlocks.enderBox);
	}
	
	@Override
	@SideOnly(CLIENT)
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
	
	private boolean canPickUp(IBlockState blockState) {
		String registryName = blockState.getBlock().getRegistryName().toString();
		return !isBlacklisted(registryName);
	}
	
	private boolean isBlacklisted(String blockName) {
		for (String glob : HandyModsConfig.enderBoxBlacklist) {
			StringBuilder pattern = new StringBuilder(glob.length());
			for (String part : glob.split("\\*", -1)) {
				if (!part.isEmpty()) { // not necessary
					pattern.append(Pattern.quote(part));
				}
				pattern.append(".*");
			}
			
			// delete last ".*" wildcard
			pattern.delete(pattern.length() - 2, pattern.length());
			
			if (Pattern.matches(pattern.toString(), blockName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@SideOnly(CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		if (hasBlockData(stack)) {
			return super.canPlaceBlockOnSide(world, pos, side, player, stack);
		} else {
			return canPickUp(world.getBlockState(pos));
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
		
		// only pick up valid blocks
		if (world.isAirBlock(pos) || !canPickUp(state))
			return EnumActionResult.PASS;
		
		if (world.isRemote) {
			// on the client, just assume it worked and play the sound
			SoundHelpers.playPlacementSound(player, pos, block);
			return EnumActionResult.SUCCESS;
		}
		
		// capture tile entity
		final Optional<NBTTagCompound> tileEntityNBT = Optional
				.ofNullable(world.getTileEntity(pos))
				.map(prev -> prev.writeToNBT(new NBTTagCompound()));
		
		if (!player.capabilities.isCreativeMode) {
			itemStack.shrink(1);
		}
		
		// replace block
		// We remove the tile entity before removing the block so the breakBlock() handler can't use it to drop items or cause flux etc.
		world.removeTileEntity(pos);
		final IBlockState newState = HandyModsBlocks.enderBox.getDefaultState();
		// Since we removed the tile entity, we may be violating some assumptions, but that should only cause early exits from breakBlock(), which is called late enough that we don't break much.
		try {
			world.setBlockState(pos, newState, 0b00011);
		} catch (Exception e) {
			HandyMods.logger.debug("ender box placement ignoring the following exception:");
			HandyMods.logger.debug(e);
		}
		
		// store captured tile entity
		final TileEntityEnderBox newTileEntity = HandyModsBlocks.enderBox.tileEntity(world, pos);
		assert newTileEntity != null;
		newTileEntity.storedBlock = new BlockData(block, metadata, tileEntityNBT);
		
		return EnumActionResult.SUCCESS;
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
