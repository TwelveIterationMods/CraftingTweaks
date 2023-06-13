package net.blay09.mods.craftingtweaks.api.impl;

public class DefaultFourByFourGridRotateHandler extends DefaultGridRotateHandler {

    protected boolean ignoresSlotId(int slotId) {
        return false;
    }

    protected int rotateSlotId(int slotId, boolean counterClockwise) {
        if (!counterClockwise) {
            switch (slotId) {
                case 0:
                    return 1;
                case 1:
                    return 3;
                case 2:
                    return 0;
                case 3:
                    return 2;
            }
        } else {
            switch (slotId) {
                case 1:
                    return 0;
                case 3:
                    return 1;
                case 0:
                    return 2;
                case 2:
                    return 3;
            }
        }
        return 0;
    }

}
