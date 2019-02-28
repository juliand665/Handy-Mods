package handymods.client.render;

import handymods.tile.BlockData;
import handymods.tile.TileEntityEnderBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class RenderEnderBox extends TileEntitySpecialRenderer<TileEntityEnderBox> {
	@Override
	public void render(TileEntityEnderBox tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		final BlockData blockData = tileEntity.storedBlock;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d); // center
		
		render(blockData);
		
		GlStateManager.popMatrix();
	}
	
	public static void render(BlockData blockData) {
		GlStateManager.pushMatrix();
		final long cycleLength = 5000; // milliseconds
		final long phase = Minecraft.getSystemTime() % cycleLength;
		final float angle = 360f * phase / cycleLength; // yes, it's in degrees. yes, i checked. yes, i hate it.
		GlStateManager.rotate(angle, 0, 1, 0);
		
		RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
		ItemStack blockStack = blockData.getPickedBlock(Minecraft.getMinecraft().world, BlockPos.ORIGIN, null, null);
		IBakedModel model = renderer.getItemModelWithOverrides(blockStack, null, null);
		
		final double scale = is2DModel(model) ? 0.6D : 1.2D;
		GlStateManager.scale(scale, scale, scale);
		renderer.renderItem(blockStack, ItemCameraTransforms.TransformType.FIXED);
		
		GlStateManager.popMatrix();
	}
	
	private static boolean is2DModel(IBakedModel model) {
		return model instanceof BakedItemModel;
	}
}
