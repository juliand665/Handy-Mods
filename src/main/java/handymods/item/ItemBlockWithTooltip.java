package handymods.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static handymods.util.Localization.localized;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ItemBlockWithTooltip extends ItemBlock {
	public ItemBlockWithTooltip(Block block) {
		super(block);
	}
	
	@Override
	@SideOnly(CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		tooltip.add(localized("tooltip", this, "default"));
	}
}
