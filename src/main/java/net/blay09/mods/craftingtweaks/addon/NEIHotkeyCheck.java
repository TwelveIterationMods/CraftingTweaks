package net.blay09.mods.craftingtweaks.addon;

import codechicken.nei.LayoutManager;

public class NEIHotkeyCheck implements HotkeyCheck {
    @Override
    public boolean allowHotkeys() {
        return LayoutManager.getInputFocused() == null;
    }
}
