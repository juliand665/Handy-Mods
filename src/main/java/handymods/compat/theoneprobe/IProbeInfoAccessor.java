package handymods.compat.theoneprobe;

import net.minecraftforge.fml.common.Optional;

/** wrapper around TOP API's identically named interface that doesn't crash if TOP isn't installed */
@net.minecraftforge.fml.common.Optional.Interface(modid = "theoneprobe", iface = "mcjty.theoneprobe.api.IProbeInfoAccessor")
public interface IProbeInfoAccessor extends mcjty.theoneprobe.api.IProbeInfoAccessor {}
