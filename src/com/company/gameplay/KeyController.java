package com.company.gameplay;

import com.company.GameLogic;
import com.googlecode.lanterna.input.KeyStroke;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.company.GameLogic.*;
import static com.company.Main.*;

public class KeyController implements Runnable
{
    private static final int reloadingCoolDownTime = 5; // in deciseconds
    private static int currentTime = 0;
    private static final Timer reloadMapTimer = new Timer(100, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(currentTime++ >= reloadingCoolDownTime)
            {
                currentTime = 0;
                canReloadMap = true;
            }
        }
    });

    private static boolean canReloadMap = false;

    @Override
    public void run()
    {
        reloadMapTimer.start();

        while (!gameOver)
        {
            try
            {
                KeyStroke keyStroke= screen.pollInput();
                if (keyStroke != null)
                {
                    pressedKey = keyStroke;
                    switch (GameLogic.pressedKey.getKeyType())
                    {
                        case EOF, Escape -> {
                            reloadMapTimer.stop();
                            gameOver = true;
                        }
                        case Character -> {
                            if(pressedKey.getCharacter() == 'a') autoMode = !autoMode;
                            else if(pressedKey.getCharacter() == 'r' && canReloadMap)
                            {
                                canReloadMap = false;
                                refreshMap();
                                currentTime = 0;
                                reloadMapTimer.restart();
                            }
                        }
                    }
                }

                try { TimeUnit.MILLISECONDS.sleep(10); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            catch (NullPointerException | IOException ignored) {}
        }
    }
}
