package com.company.gameplay;

import com.googlecode.lanterna.TextColor;

public class LavaEffectController extends Controller {
    private static final int reloadingCoolDownTime = 10; // in centiseconds
    private static final int numberOfLavaColors = 125;
    private static byte deltaLavaColor = 1;
    private static byte currentLavaColor = 0;
    private static int reloadingCounter = 0;

    @Override
    public void step() {
        if(reloadingCounter++ >= reloadingCoolDownTime) {
            reloadingCounter = 0;
            currentLavaColor += deltaLavaColor;
            if(currentLavaColor >= numberOfLavaColors || currentLavaColor < 0) deltaLavaColor *= -1;
        }
    }

    public static TextColor getLavaColor()
    {
        return new TextColor.RGB(75 + currentLavaColor, 8, 16);
    }
}
