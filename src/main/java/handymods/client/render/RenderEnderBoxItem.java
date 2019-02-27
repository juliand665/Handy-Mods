package handymods.client.render;

import handymods.block.HandyModsBlocks;
import handymods.item.ItemBlockEnderBox;
import handymods.tile.BlockData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class RenderEnderBoxItem extends TileEntityItemStackRenderer {
	private static IBakedModel enderBoxModel;
	
	@Override
	public void renderByItem(ItemStack itemStack) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (enderBoxModel == null) {
			ModelManager modelManager = mc.getRenderItem().getItemModelMesher().getModelManager();
			ResourceLocation enderBoxLocation = HandyModsBlocks.enderBox.getRegistryName();
			assert enderBoxLocation != null;
			enderBoxModel = new ModelWrapper(modelManager.getModel(new ModelResourceLocation(enderBoxLocation, null)));
		}
		
		// store old values
		int oldAlphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float oldAlphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		boolean oldDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
		
		// render opaque parts
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 1F);
		renderModel(enderBoxModel, -1, itemStack);
		
		if (ItemBlockEnderBox.hasBlockData(itemStack)) {
			BlockData blockData = ItemBlockEnderBox.getBlockData(itemStack);
			
			// render contained item
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			RenderEnderBox.render(blockData);
			GlStateManager.popMatrix();
		}
		
		// render translucent parts
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false); //
		GlStateManager.alphaFunc(GL11.GL_LESS, 1F);
		renderModel(enderBoxModel, -1, itemStack);
		GlStateManager.disableBlend();
		
		// reset values
		GlStateManager.alphaFunc(oldAlphaFunc, oldAlphaRef);
		GlStateManager.depthMask(oldDepthMask);
	}
	
	// copy-pasted private methods from RenderItem
	
	private void renderModel(IBakedModel model, int color, ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		
		for (EnumFacing enumfacing : EnumFacing.values()) {
			this.renderQuads(bufferbuilder, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
		}
		
		this.renderQuads(bufferbuilder, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
		tessellator.draw();
	}
	
	private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		boolean flag = color == -1 && !stack.isEmpty();
		int i = 0;
		
		for (int j = quads.size(); i < j; ++i) {
			BakedQuad bakedquad = quads.get(i);
			int k = color;
			
			if (flag && bakedquad.hasTintIndex()) {
				k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());
				
				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}
				
				k = k | -16777216;
			}
			
			net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}
	
	private class ModelWrapper implements IBakedModel {
		private final IBakedModel wrapped;
		
		private ModelWrapper(IBakedModel wrapped) {
			this.wrapped = wrapped;
		}
		
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			return wrapped.getQuads(state, side, rand);
		}
		
		@Override
		public boolean isAmbientOcclusion() {
			return wrapped.isAmbientOcclusion();
		}
		
		@Override
		public boolean isGui3d() {
			return wrapped.isGui3d();
		}
		
		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}
		
		@Override
		public TextureAtlasSprite getParticleTexture() {
			return wrapped.getParticleTexture();
		}
		
		@Override
		@Deprecated
		public ItemCameraTransforms getItemCameraTransforms() {
			return wrapped.getItemCameraTransforms();
		}
		
		@Override
		public ItemOverrideList getOverrides() {
			return wrapped.getOverrides();
		}
		
		@Override
		public boolean isAmbientOcclusion(IBlockState state) {
			return wrapped.isAmbientOcclusion(state);
		}
		
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
			return wrapped.handlePerspective(cameraTransformType);
		}
	}
}
