package handymods.tile;

import handymods.HandyMods;
import handymods.util.NBTCodable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;

public final class BlockData extends NBTCodable {
	private static final String NBT_KEY_BLOCK_NAME = "blockName";
	private static final String NBT_KEY_METADATA = "metadata";
	private static final String NBT_KEY_TILE_ENTITY_NBT = "tileEntityNBT";
	
	// don't actually mutate these outside of inits and readFrom
	private Block block;
	private int metadata;
	private Optional<NBTTagCompound> tileEntityNBT;
	
	private Optional<TileEntity> tileEntity = Optional.empty();
	
	public BlockData() {
		this(Blocks.AIR, 0, Optional.empty());
	}
	
	public BlockData(NBTTagCompound container) {
		super(container);
	}
	
	public BlockData(Block block, int metadata, Optional<NBTTagCompound> tag) {
		this.block = block;
		this.metadata = metadata;
		this.tileEntityNBT = tag;
	}
	
	@Override
	public void readFrom(NBTTagCompound container) {
		final String name = container.getString(NBT_KEY_BLOCK_NAME);
		final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		if (block != null) {
			this.block = block;
		} else {
			HandyMods.logger.info("Found unknown block named " + name + "; using air as fallback.");
			this.block = Blocks.AIR;
		}
		
		metadata = container.getInteger(NBT_KEY_METADATA);
		
		tileEntityNBT = container.hasKey(NBT_KEY_TILE_ENTITY_NBT)
			? Optional.of(container.getCompoundTag(NBT_KEY_TILE_ENTITY_NBT))
			: Optional.empty();
	}
	
	@Override
	public void writeTo(NBTTagCompound container) {
		container.setString(NBT_KEY_BLOCK_NAME, block.getRegistryName().toString());
		
		container.setInteger(NBT_KEY_METADATA, metadata);
		
		tileEntityNBT.ifPresent(nbt -> container.setTag(NBT_KEY_TILE_ENTITY_NBT, nbt));
	}
	
	/** updates the contained tile entity's position, if applicable */
	public void updatePosition(BlockPos position) {
		if (!tileEntityNBT.isPresent())
			return;
		
		final NBTTagCompound container = tileEntityNBT.get();
		// this is kinda hacky, but there's no other option
		container.setInteger("x", position.getX());
		container.setInteger("y", position.getY());
		container.setInteger("z", position.getZ());
	}
	
	public ItemStack getPickedBlock(World world, BlockPos pos, @Nullable RayTraceResult target, @Nullable EntityPlayer player) {
		try {
			// try to simulate picking the block
			return block.getPickBlock(getStoredState(), target, world, pos, player);
		} catch (Exception e) {
			// could fail e.g. because the pick block code expects a certain tile entity
		}
		try {
			// try to get the dropped item
			return block.getItem(world, pos, getStoredState());
		} catch (Exception e) {
			// could fail e.g. because the pick block code expects a certain tile entity
		}
		// construct the dropped item ourselves
		return new ItemStack(block, 1, block.damageDropped(getStoredState()));
	}
	
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, Vec3d hit, EntityLivingBase placer, EnumHand hand) {
		return block.getStateForPlacement(
			world, pos, facing,
			(float) hit.x,
			(float) hit.y,
			(float) hit.z,
			metadata, placer, hand
		);
	}
	
	public Block getBlock() {
		return block;
	}
	
	public int getMetadata() {
		return metadata;
	}
	
	public Optional<NBTTagCompound> getTileEntityNBT() {
		return tileEntityNBT;
	}
	
	public IBlockState getStoredState() {
		return block.getStateFromMeta(metadata);
	}
	
	public Optional<TileEntity> getTileEntity(World world) {
		if (!tileEntityNBT.isPresent())
			return Optional.empty();
		NBTTagCompound nbt = tileEntityNBT.get();
		
		if (!tileEntity.isPresent()) {
			tileEntity = Optional.ofNullable(block.createTileEntity(world, getStoredState()));
			tileEntity.ifPresent(te -> te.readFromNBT(nbt));
		}
		
		return tileEntity;
	}
}
