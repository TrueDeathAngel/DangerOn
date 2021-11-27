package com.company.gameplay;

import static com.company.Main.gameOver;
import static com.company.gameplay.GameLogic.*;

public class ContextMenu extends Controller {
    protected int cursorPosition = 0;

    @Override
    public void step() {
        if(gameOver) cancel();
        // try { TimeUnit.MILLISECONDS.sleep(1); }
        // catch (InterruptedException ignored) {}
        if(pressedKey != null) {
            switch (pressedKey.getKeyType()) {
                case Escape, EOF -> {
                    isOpenedContextMenu = false;
                    gamePlayControllers.forEach(Controller::resume);
                    enemiesControllers.forEach(Controller::resume);
                    cancel();
                }
                case ArrowUp -> {
                    if(cursorPosition > 0)
                        cursorPosition--;
                }
                case ArrowDown -> {
                    if(cursorPosition < hero.inventory.size() - 1)
                        cursorPosition++;
                }
            }
            pressedKey = null;
        }
    }
}
