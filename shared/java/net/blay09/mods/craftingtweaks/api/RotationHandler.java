package net.blay09.mods.craftingtweaks.api;

public interface RotationHandler {

    /**
     * @param slotId the slot id being checked
     * @return true if this slot should stay untouched during rotation (i.e. center for 9x9 grid)
     */
    boolean ignoreSlotId(int slotId);

    /**
     * @deprecated Use rotateSlotId with counterClockwise parameter instead.
     * @param slotId the slot id being rotated
     * @return the resulting slot id after clockwise-rotation
     */
    default int rotateSlotId(int slotId) {
        return rotateSlotId(slotId, false);
    }

    /**
     * @param slotId the slot id being rotated
     * @param counterClockwise true if the rotation should happen counter-clockwise
     * @return the resulting slot id after clockwise-rotation
     */
    int rotateSlotId(int slotId, boolean counterClockwise);

}
