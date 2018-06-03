package handymods.tile;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityPaperBox extends TileEntity {
	public BlockData storedBlock = new BlockData(); // boxes are never empty, but this avoids crashing if you place them using commands
	
	@Override
	public void readFromNBT(NBTTagCompound container) {
		super.readFromNBT(container);

		storedBlock = new BlockData(container.getCompoundTag("storedBlock"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound container) {
		container.setTag("storedBlock", storedBlock.getNBT());
		
		return super.writeToNBT(container);
	}
	
	public static class BlockData {
		public Block block;
		public int metadata;
		public Optional<NBTTagCompound> tag;
		
		private BlockData() {
			this(Blocks.AIR, 0, Optional.empty());
		}
		
		public BlockData(Block block, int metadata, Optional<NBTTagCompound> tag) {
			this.block = block;
			this.metadata = metadata;
			this.tag = tag;
		}
		
		public BlockData(NBTTagCompound container) {
			String name = container.getString("name"); // TODO what happens if name is null?
			block = Block.REGISTRY.getObject(new ResourceLocation(name));
			
			metadata = container.getInteger("metadata");
			
			tag = container.hasKey("tag") ? Optional.of(container.getCompoundTag("tag")) : Optional.empty();
		}
		
		public NBTTagCompound getNBT() {
			NBTTagCompound container = new NBTTagCompound();
			
			String name = Block.REGISTRY.getNameForObject(block).toString();
			container.setString("name", name);
			
			container.setInteger("metadata", metadata);
			
			if (tag.isPresent()) {
				container.setTag("tag", tag.get());
			}
			
			return container;
		}
	}
}
