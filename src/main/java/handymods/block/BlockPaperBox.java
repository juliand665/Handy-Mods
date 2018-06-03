package handymods.block;

import handymods.CreativeTabHandyMods;
import handymods.item.ItemBlockPaperBox;
import handymods.tile.TileEntityPaperBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockPaperBox extends Block {
	public BlockPaperBox() {
		super(Material.CLOTH);
		MinecraftForge.EVENT_BUS.register(this);
		
		setHardness(0.5F);
		setResistance(1F);
		setCreativeTab(CreativeTabHandyMods.instance);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPaperBox();
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(getDroppedItem(world, pos));
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getDroppedItem(world, pos);
	}
	
	private ItemStack getDroppedItem(IBlockAccess world, BlockPos pos) {
		TileEntityPaperBox tileEntity = (TileEntityPaperBox) world.getTileEntity(pos);
		ItemStack itemStack = new ItemStack(this);
		ItemBlockPaperBox.setBlockData(itemStack, tileEntity.storedBlock);
		return itemStack;
	}
}
