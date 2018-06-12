package handymods.util;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class SoundHelpers {
	public static void playPlacementSound(EntityPlayer player, BlockPos pos, Block block) {
		SoundType soundType = block.getSoundType(null, null, null, null);
		player.world.playSound(
				player,
				pos,
				soundType.getPlaceSound(),
				SoundCategory.BLOCKS,
				(soundType.getVolume() + 1.0F) / 2.0F,
				soundType.getPitch() * 0.8F);
	}
}
