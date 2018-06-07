package handymods.tile;

import handymods.util.INBTCodable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/** handles a bunch of NBT coding boilerplate */
public abstract class ModTileEntity extends TileEntity implements INBTCodable {
	@Override
	public void readFromNBT(NBTTagCompound container) {
		super.readFromNBT(container);
		readFrom(container);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		final NBTTagCompound container = super.writeToNBT(compound);
		writeTo(container);
		return container;
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		final NBTTagCompound container = super.getUpdateTag();
		writeTo(container);
		return container;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		readFrom(tag);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		assert super.getUpdatePacket() == null;
		final NBTTagCompound container = new NBTTagCompound();
		writeTo(container);
		return new SPacketUpdateTileEntity(pos, 0, container);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFrom(packet.getNbtCompound());
	}
}
