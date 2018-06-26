package handymods.block.render;

import handymods.tile.TileEntityItemHolderRendered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class RenderItemHolder extends TileEntitySpecialRenderer<TileEntityItemHolderRendered> {
	@Override
	public void render(TileEntityItemHolderRendered tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack itemStack = tileEntity.getItemStack();
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d); // center
			
			final long cycleLength = 5000; // milliseconds
			long phase = Minecraft.getSystemTime() % cycleLength;
			float angle = 360f * phase / cycleLength; // yes, it's in degrees. yes, i checked. yes, i hate it.
			GlStateManager.rotate(angle, 0, 1, 0);
			
			double scale = 0.5d;
			GlStateManager.scale(scale, scale, scale);
			Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
			
			GlStateManager.popMatrix();
		}
	}
}
