package handymods.tile;

import java.util.Optional;

import handymods.HandyMods;
import handymods.util.NBTCodable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TileEntityEnderBox extends ModTileEntity {
	private static final String NBT_KEY_STORED_BLOCK = "storedBlock";
	
	public BlockData storedBlock = new BlockData(); // boxes are never empty, but this avoids crashing if you place them using commands
	
	@Override
	public void readFrom(NBTTagCompound container) {
		storedBlock = new BlockData(container.getCompoundTag(NBT_KEY_STORED_BLOCK));
	}
	
	@Override
	public void writeTo(NBTTagCompound container) {
		assert (container.hasKey(NBT_KEY_STORED_BLOCK));
		container.setTag(NBT_KEY_STORED_BLOCK, storedBlock.getNBT());
	}
	
	public static class BlockData extends NBTCodable {
		private static final String NBT_KEY_BLOCK_NAME = "blockName";
		private static final String NBT_KEY_METADATA = "metadata";
		private static final String NBT_KEY_TILE_ENTITY_NBT = "tileEntityNBT";
		
		public Block block;
		public int metadata;
		public Optional<NBTTagCompound> tileEntityNBT;
		
		private BlockData() {
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
		
		public ItemStack getPickBlockFallback(IBlockState state) {
			return new ItemStack(block, 1, block.damageDropped(state));
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
	}
}
