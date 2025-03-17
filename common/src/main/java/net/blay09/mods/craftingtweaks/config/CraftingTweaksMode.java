package net.blay09.mods.craftingtweaks.config;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum CraftingTweaksMode implements StringRepresentable {
    DEFAULT,
    BUTTONS,
    HOTKEYS,
    DISABLED;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
