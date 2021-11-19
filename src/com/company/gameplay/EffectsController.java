package com.company.gameplay;

import com.company.GameLogic;
import com.googlecode.lanterna.TextColor;
import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.company.Main.gameOver;

public class EffectsController implements Runnable
{
    private static final int reloadingCoolDownTime = 1; // in deciseconds
    private static final int numberOfLavaColors = 40;
    private static byte deltaLavaColor = 1;
    private static byte currentLavaColor = 0;
    private static int currentTime = 0;
    private static final Timer reloadEffectsTimer = new Timer(100, e -> {
        if(currentTime++ >= reloadingCoolDownTime)
        {
            currentLavaColor += deltaLavaColor;
            currentTime = 0;
            if(currentLavaColor >= numberOfLavaColors || currentLavaColor <= 0) deltaLavaColor *= -1;
        }
    });

    @Override
    public void run()
    {
        reloadEffectsTimer.start();
        while (!gameOver)
        {
            try { TimeUnit.MILLISECONDS.sleep(1); }
            catch (InterruptedException e){ e.printStackTrace(); }
        }
        reloadEffectsTimer.stop();
    }

    public static TextColor getLavaColor()
    {
        return new TextColor.RGB(75 + currentLavaColor, 8, 16);
    }
}
