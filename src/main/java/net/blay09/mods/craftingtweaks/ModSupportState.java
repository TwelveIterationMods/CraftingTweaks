package net.blay09.mods.craftingtweaks;

import java.util.Locale;

public enum ModSupportState {
    ENABLED,
    BUTTONS_ONLY,
    HOTKEYS_ONLY,
    DISABLED;

    public static ModSupportState fromName(String name) {
            try {
            return valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return ENABLED;
        }
    }

    public static String[] getValidValues() {
        ModSupportState[] values = ModSupportState.values();
        String[] validValues = new String[values.length];
        for(int i = 0; i < values.length; i++) {
            validValues[i] = values[i].name().toLowerCase();
        }
        return validValues;
    }
}
