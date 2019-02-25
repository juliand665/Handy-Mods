package handymods.util;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoundHelpers {
	public static void playPlacementSound(EntityPlayer player, BlockPos pos, Block block) {
		playPlacementSound(player.world, player, pos, block);
	}
	
	public static void playPlacementSound(World world, BlockPos pos, Block block) {
		playPlacementSound(world, null, pos, block);
	}
	
	public static void playPlacementSound(World world, @Nullable EntityPlayer player, BlockPos pos, Block block) {
		SoundType soundType = block.getSoundType(block.getDefaultState(), world, pos, null);
		world.playSound(
			player,
			pos,
			soundType.getPlaceSound(),
			SoundCategory.BLOCKS,
			(soundType.getVolume() + 1.0F) / 2.0F,
			soundType.getPitch() * 0.8F);
	}
}
