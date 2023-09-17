package net.blay09.mods.craftingtweaks.registry;

import com.google.gson.annotations.SerializedName;

public class CraftingTweaksRegistrationData {
    public static class TweakData {
        private boolean enabled = true;
        private boolean showButton = true;
        private Integer buttonX;
        private Integer buttonY;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isShowButton() {
            return showButton;
        }

        public void setShowButton(boolean showButton) {
            this.showButton = showButton;
        }

        public Integer getButtonX() {
            return buttonX;
        }

        public void setButtonX(Integer buttonX) {
            this.buttonX = buttonX;
        }

        public Integer getButtonY() {
            return buttonY;
        }

        public void setButtonY(Integer buttonY) {
            this.buttonY = buttonY;
        }
    }

    @SerializedName("modid")
    private String modId;

    private boolean silent;

    private String containerClass = "";
    private String containerCallbackClass = "";
    private String validContainerPredicateClass = "";
    private String getGridStartFunctionClass = "";
    private int gridSlotNumber = 1;
    private int gridSize = 9;
    private Integer buttonOffsetX;
    private Integer buttonOffsetY;
    private String alignToGrid = "left";
    private String buttonStyle = "default";
    private boolean hideButtons;
    private boolean phantomItems;
    private TweakData tweakRotate = new TweakData();
    private TweakData tweakBalance = new TweakData();
    private TweakData tweakClear = new TweakData();

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public boolean isSilent() {
        return silent;
    }

    public String getContainerClass() {
        return containerClass;
    }

    public void setContainerClass(String containerClass) {
        this.containerClass = containerClass;
    }

    public String getContainerCallbackClass() {
        return containerCallbackClass;
    }

    public void setContainerCallbackClass(String containerCallbackClass) {
        this.containerCallbackClass = containerCallbackClass;
    }

    public String getValidContainerPredicateClass() {
        return validContainerPredicateClass;
    }

    public void setValidContainerPredicateClass(String validContainerPredicateClass) {
        this.validContainerPredicateClass = validContainerPredicateClass;
    }

    public String getGetGridStartFunctionClass() {
        return getGridStartFunctionClass;
    }

    public void setGetGridStartFunctionClass(String getGridStartFunctionClass) {
        this.getGridStartFunctionClass = getGridStartFunctionClass;
    }

    public int getGridSlotNumber() {
        return gridSlotNumber;
    }

    public void setGridSlotNumber(int gridSlotNumber) {
        this.gridSlotNumber = gridSlotNumber;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public Integer getButtonOffsetX() {
        return buttonOffsetX;
    }

    public void setButtonOffsetX(Integer buttonOffsetX) {
        this.buttonOffsetX = buttonOffsetX;
    }

    public Integer getButtonOffsetY() {
        return buttonOffsetY;
    }

    public void setButtonOffsetY(Integer buttonOffsetY) {
        this.buttonOffsetY = buttonOffsetY;
    }

    public String getAlignToGrid() {
        return alignToGrid;
    }

    public void setAlignToGrid(String alignToGrid) {
        this.alignToGrid = alignToGrid;
    }

    public String getButtonStyle() {
        return buttonStyle;
    }

    public void setButtonStyle(String buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public boolean isHideButtons() {
        return hideButtons;
    }

    public void setHideButtons(boolean hideButtons) {
        this.hideButtons = hideButtons;
    }

    public boolean isPhantomItems() {
        return phantomItems;
    }

    public void setPhantomItems(boolean phantomItems) {
        this.phantomItems = phantomItems;
    }

    public TweakData getTweakRotate() {
        return tweakRotate;
    }

    public void setTweakRotate(TweakData tweakRotate) {
        this.tweakRotate = tweakRotate;
    }

    public TweakData getTweakBalance() {
        return tweakBalance;
    }

    public void setTweakBalance(TweakData tweakBalance) {
        this.tweakBalance = tweakBalance;
    }

    public TweakData getTweakClear() {
        return tweakClear;
    }

    public void setTweakClear(TweakData tweakClear) {
        this.tweakClear = tweakClear;
    }
}
