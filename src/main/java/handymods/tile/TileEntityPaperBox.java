package handymods.tile;

import java.util.Optional;

import handymods.INBTCodable;
import handymods.NBTCodable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityPaperBox extends ModTileEntity {
	private static final String NBT_KEY_STORED_BLOCK = "storedBlock";
	
	public BlockData storedBlock = new BlockData(); // boxes are never empty, but this avoids crashing if you place them using commands
	
	@Override
	public void readFrom(NBTTagCompound container) {
		storedBlock = new BlockData(container.getCompoundTag(NBT_KEY_STORED_BLOCK));
	}
	
	@Override
	public void writeTo(NBTTagCompound container) {
		assert(container.hasKey(NBT_KEY_STORED_BLOCK));
		container.setTag(NBT_KEY_STORED_BLOCK, storedBlock.getNBT());
	}
	
	public static class BlockData extends NBTCodable {
		public Block block;
		public int metadata;
		public Optional<NBTTagCompound> tag;
		
		private BlockData() {
			this(Blocks.AIR, 0, Optional.empty());
		}
		
		public BlockData(NBTTagCompound container) {
			super(container);
		}
		
		public BlockData(Block block, int metadata, Optional<NBTTagCompound> tag) {
			this.block = block;
			this.metadata = metadata;
			this.tag = tag;
		}

		@Override
		public void readFrom(NBTTagCompound container) {
			String name = container.getString("name"); // TODO what happens if name is null?
			block = Block.REGISTRY.getObject(new ResourceLocation(name));
			
			metadata = container.getInteger("metadata");
			
			tag = container.hasKey("tag") ? Optional.of(container.getCompoundTag("tag")) : Optional.empty();
		}

		@Override
		public void writeTo(NBTTagCompound container) {
			String name = Block.REGISTRY.getNameForObject(block).toString();
			container.setString("name", name);
			
			container.setInteger("metadata", metadata);
			
			if (tag.isPresent()) {
				container.setTag("tag", tag.get());
			}
		}
	}
}
