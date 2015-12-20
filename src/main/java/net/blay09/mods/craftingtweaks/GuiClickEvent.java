package net.blay09.mods.craftingtweaks;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiScreen;

@Cancelable
public class GuiClickEvent extends Event {

    public final GuiScreen gui;
    public final int mouseX;
    public final int mouseY;
    public final int button;

    public GuiClickEvent(GuiScreen gui, int mouseX, int mouseY, int button) {
        this.gui = gui;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }

}
