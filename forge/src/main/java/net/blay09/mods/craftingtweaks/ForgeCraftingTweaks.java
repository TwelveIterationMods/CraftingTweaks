package net.blay09.mods.craftingtweaks;

import net.blay09.mods.craftingtweaks.client.CraftingTweaksClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(CraftingTweaks.MOD_ID)
public class ForgeCraftingTweaks {
    public ForgeCraftingTweaks() {
        CraftingTweaks.initialize();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CraftingTweaksClient::initialize);
    }

}
