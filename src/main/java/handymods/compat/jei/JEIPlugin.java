package handymods.compat.jei;

import handymods.inventory.ContainerChestyCraftingTable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(
			ContainerChestyCraftingTable.class,
			VanillaRecipeCategoryUid.CRAFTING,
			1, 9,
			10, 36
		);
	}
}
