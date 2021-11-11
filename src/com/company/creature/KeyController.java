package com.company.creature;

import com.company.GameLogic;
import com.company.map.MapFactory;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

import static com.company.GameLogic.autoMode;
import static com.company.GameLogic.pressedKey;
import static com.company.Main.*;

public class KeyController implements Runnable
{
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                KeyStroke keyStroke= screen.pollInput();
                if (keyStroke != null)
                {
                    pressedKey = keyStroke;
                    switch (GameLogic.pressedKey.getKeyType())
                    {
                        case EOF, Escape -> gameOver = true;
                        case Character -> {
                            if(pressedKey.getCharacter() == 'a') autoMode = !autoMode;
                            else if(pressedKey.getCharacter() == 'r') map = MapFactory.createMap();
                        }
                    }
                }
            }
            catch (IOException ignored) {}
        }
    }
}
