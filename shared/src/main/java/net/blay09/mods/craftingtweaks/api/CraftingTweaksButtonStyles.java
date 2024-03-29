package net.blay09.mods.craftingtweaks.api;

public class CraftingTweaksButtonStyles {
    public static final ButtonStyle DEFAULT = new ButtonStyle(18, 18, 1, 1)
            .withTweak(TweakType.Rotate, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 16, 0)
                    .withState(ButtonState.HOVER, 16, 16)
                    .withState(ButtonState.DISABLED, 16, 32))
            .withTweak(TweakType.Clear, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 32, 0)
                    .withState(ButtonState.HOVER, 32, 16)
                    .withState(ButtonState.DISABLED, 32, 32))
            .withTweak(TweakType.Balance, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 48, 0)
                    .withState(ButtonState.HOVER, 48, 16)
                    .withState(ButtonState.DISABLED, 48, 32))
            .withTweak(TweakType.RotateCounterClockwise, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 64, 0)
                    .withState(ButtonState.HOVER, 64, 16)
                    .withState(ButtonState.DISABLED, 64, 32))
            .withTweak(TweakType.ForceClear, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 80, 0)
                    .withState(ButtonState.HOVER, 80, 16)
                    .withState(ButtonState.DISABLED, 80, 32))
            .withTweak(TweakType.Spread, new ButtonProperties(16, 16)
                    .withState(ButtonState.NORMAL, 96, 0)
                    .withState(ButtonState.HOVER, 96, 16)
                    .withState(ButtonState.DISABLED, 96, 32));

    public static final ButtonStyle SMALL_HEIGHT = new ButtonStyle(18, 12, 1, 0)
            .withTweak(TweakType.Rotate, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 16, 48)
                    .withState(ButtonState.HOVER, 16, 58)
                    .withState(ButtonState.DISABLED, 16, 68))
            .withTweak(TweakType.Clear, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 32, 48)
                    .withState(ButtonState.HOVER, 32, 58)
                    .withState(ButtonState.DISABLED, 32, 68))
            .withTweak(TweakType.Balance, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 48, 48)
                    .withState(ButtonState.HOVER, 48, 58)
                    .withState(ButtonState.DISABLED, 48, 68))
            .withTweak(TweakType.RotateCounterClockwise, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 64, 48)
                    .withState(ButtonState.HOVER, 64, 58)
                    .withState(ButtonState.DISABLED, 64, 68))
            .withTweak(TweakType.ForceClear, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 80, 48)
                    .withState(ButtonState.HOVER, 80, 58)
                    .withState(ButtonState.DISABLED, 80, 68))
            .withTweak(TweakType.Spread, new ButtonProperties(16, 10)
                    .withState(ButtonState.NORMAL, 96, 48)
                    .withState(ButtonState.HOVER, 96, 58)
                    .withState(ButtonState.DISABLED, 96, 68));

    public static final ButtonStyle SMALL_WIDTH = new ButtonStyle(12, 18, 0, 1)
            .withTweak(TweakType.Rotate, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 10, 78)
                    .withState(ButtonState.HOVER, 10, 94)
                    .withState(ButtonState.DISABLED, 10, 110))
            .withTweak(TweakType.Clear, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 20, 78)
                    .withState(ButtonState.HOVER, 20, 94)
                    .withState(ButtonState.DISABLED, 30, 110))
            .withTweak(TweakType.Balance, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 30, 78)
                    .withState(ButtonState.HOVER, 30, 94)
                    .withState(ButtonState.DISABLED, 30, 110))
            .withTweak(TweakType.RotateCounterClockwise, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 40, 78)
                    .withState(ButtonState.HOVER, 40, 94)
                    .withState(ButtonState.DISABLED, 40, 110))
            .withTweak(TweakType.ForceClear, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 50, 78)
                    .withState(ButtonState.HOVER, 50, 94)
                    .withState(ButtonState.DISABLED, 50, 110))
            .withTweak(TweakType.Spread, new ButtonProperties(10, 16)
                    .withState(ButtonState.NORMAL, 60, 78)
                    .withState(ButtonState.HOVER, 60, 94)
                    .withState(ButtonState.DISABLED, 60, 158110));

    public static final ButtonStyle SMALL = new ButtonStyle(12, 12, 0, 0)
            .withTweak(TweakType.Rotate, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 10, 126)
                    .withState(ButtonState.HOVER, 10, 136)
                    .withState(ButtonState.DISABLED, 10, 146))
            .withTweak(TweakType.Clear, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 20, 126)
                    .withState(ButtonState.HOVER, 20, 136)
                    .withState(ButtonState.DISABLED, 20, 146))
            .withTweak(TweakType.Balance, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 30, 126)
                    .withState(ButtonState.HOVER, 30, 136)
                    .withState(ButtonState.DISABLED, 30, 146))
            .withTweak(TweakType.RotateCounterClockwise, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 40, 126)
                    .withState(ButtonState.HOVER, 40, 136)
                    .withState(ButtonState.DISABLED, 40, 146))
            .withTweak(TweakType.ForceClear, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 50, 126)
                    .withState(ButtonState.HOVER, 50, 136)
                    .withState(ButtonState.DISABLED, 50, 146))
            .withTweak(TweakType.Spread, new ButtonProperties(10, 10)
                    .withState(ButtonState.NORMAL, 60, 126)
                    .withState(ButtonState.HOVER, 60, 136)
                    .withState(ButtonState.DISABLED, 60, 146));

}
