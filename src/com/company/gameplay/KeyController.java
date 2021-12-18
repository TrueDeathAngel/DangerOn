package com.company.gameplay;

import com.company.map.CellTypes;
import com.company.objects.items.chests.Chest;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

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
        try {
            KeyStroke keyStroke= screen.pollInput();
            if (keyStroke != null)
            {
                if (keyStroke.getKeyType() == KeyType.EOF) gameOver = true;
                pressedKey = keyStroke;

                if (!isOpenedInventory)
                    switch (GameLogic.pressedKey.getKeyType()) {
                        case Escape -> gameOver = true;
                        case Character -> {
                            addPressedKey(pressedKey.getCharacter());
                            if(pressedKey.getCharacter() == 'a' || pressedKey.getCharacter() == 'ф') autoMode = !autoMode;
                            else if(pressedKey.getCharacter() == 'i' || pressedKey.getCharacter() == 'ш') {
                                hero.openInventoryMenu();
                            }
                            else if(pressedKey.getCharacter() == 'o' || pressedKey.getCharacter() == 'щ') {
                                floorEntities.stream()
                                        .filter(floorEntity -> floorEntity.getEntityType() == CellTypes.CHEST && floorEntity.isCloseToHero())
                                        .findAny()
                                        .ifPresent(chest -> ((Chest) chest).open());
                            }
                            else if (isAdmin)
                                if ((pressedKey.getCharacter() == 'r' || pressedKey.getCharacter() == 'к') && canReloadMap) {
                                    canReloadMap = false;
                                    refreshMap();
                                }
                                else if (pressedKey.getCharacter() == 'f' || pressedKey.getCharacter() == 'а') noWarFog = !noWarFog;
                                else if (pressedKey.getCharacter() == 'z' || pressedKey.getCharacter() == 'я') gamePlayControllers.forEach(Controller::resume);
                        }
                    }
            }
        }
        catch (NullPointerException | IOException ignored) {}
    }
}
