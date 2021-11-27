package com.company.gameplay;

import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

import static com.company.gameplay.GameLogic.*;
import static com.company.Main.*;

public class KeyController extends Controller
{
    private static final int reloadingCoolDownTime = 50; // in centiseconds
    private static int reloadMapCounter = 0;

    private static boolean canReloadMap = false;

    @Override
    public void step() {
        if(reloadMapCounter++ >= reloadingCoolDownTime) {
            canReloadMap = true;
            reloadMapCounter = 0;
        }
        try
        {
            KeyStroke keyStroke= screen.pollInput();
            if (keyStroke != null)
            {
                pressedKey = keyStroke;
                switch (GameLogic.pressedKey.getKeyType())
                {
                    case EOF -> gameOver = true;
                    case Escape -> { if(!isOpenedContextMenu) gameOver = true; }
                    case Character -> {
                        addPressedKey(pressedKey.getCharacter());
                        if(pressedKey.getCharacter() == 'a' || pressedKey.getCharacter() == 'ф') autoMode = !autoMode;
                        else if((pressedKey.getCharacter() == 'i' || pressedKey.getCharacter() == 'ш') && !isOpenedContextMenu) {
                            isOpenedContextMenu = true;
                            enemiesControllers.forEach(Controller::pause);
                            gamePlayControllers.forEach(Controller::pause);
                            new ContextMenu().start();
                        }
                        else if (isAdmin)
                            if ((pressedKey.getCharacter() == 'r' || pressedKey.getCharacter() == 'к') && canReloadMap)
                            {
                                canReloadMap = false;
                                refreshMap();
                            }
                            else if(pressedKey.getCharacter() == 'f' || pressedKey.getCharacter() == 'а') noWarFog = !noWarFog;
                    }
                }
            }
        }
        catch (NullPointerException | IOException ignored) {}
    }
}
