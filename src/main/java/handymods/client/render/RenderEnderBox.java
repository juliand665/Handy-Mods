package handymods.client.render;

import handymods.tile.BlockData;
import handymods.tile.TileEntityEnderBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class RenderEnderBox extends TileEntitySpecialRenderer<TileEntityEnderBox> {
	@Override
	public void render(TileEntityEnderBox tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		final BlockData blockData = tileEntity.storedBlock;
		final IBlockState state = blockData.getStoredState();
		final BlockPos pos = new BlockPos(x, y, z);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d); // center
		
		final long cycleLength = 5000; // milliseconds
		final long phase = Minecraft.getSystemTime() % cycleLength;
		final float angle = 360f * phase / cycleLength; // yes, it's in degrees. yes, i checked. yes, i hate it.
		GlStateManager.rotate(angle, 0, 1, 0);
		
		final double scale = 0.9d;
		GlStateManager.scale(scale, scale, scale);
		ItemStack blockStack = blockData.getPickedBlock(tileEntity.getWorld(), pos, null, null);
		Minecraft.getMinecraft().getRenderItem().renderItem(blockStack, ItemCameraTransforms.TransformType.FIXED);
		
		GlStateManager.popMatrix();
	}
}
