package com.company.gameplay;

import com.company.objects.items.Container;
import com.company.objects.items.Chest;
import com.company.objects.items.Equipment;
import com.company.objects.items.Item;
import com.company.objects.items.Weapon;

import static com.company.Main.gameOver;
import static com.company.gameplay.GameLogic.*;

public class InventoryMenu extends Controller {
    public Chest chest = null;

    public InventoryMenu() {
        inventoryCursorPosition = 0;
    }

    public InventoryMenu(Chest chest) {
        this();
        this.chest = chest;
    }

    @Override
    public void step() {
        if(gameOver) cancel();
        if(pressedKey != null) {
            switch (pressedKey.getKeyType()) {
                case Escape, EOF -> {
                    chest = null;
                    hero.closeInventory();
                    cancel();
                }
                case ArrowUp -> {
                    if(inventoryCursorPosition > 0)
                        inventoryCursorPosition--;
                }
                case ArrowDown -> {
                    if(inventoryCursorPosition < (inventoryWindowIsActive ? hero.inventory.getSize() : chest.inventory.getSize()) - 1)
                        inventoryCursorPosition++;
                }
                case ArrowLeft, ArrowRight -> {
                    if(chest != null) {
                        inventoryWindowIsActive = !inventoryWindowIsActive;
                        inventoryCursorPosition = 0;
                    }
                }
                case Character -> {
                    if (chest != null && (pressedKey.getCharacter() == 's' || pressedKey.getCharacter() == 'ы')) {
                        if (inventoryWindowIsActive && hero.inventory.isNotEmpty()) {
                            Item item = hero.inventory.getByIndex(inventoryCursorPosition);
                            hero.inventory.removeByIndex(inventoryCursorPosition);
                            chest.inventory.addItem(item);
                        } else if (chest.inventory.isNotEmpty()) {
                            Item item = chest.inventory.getByIndex(inventoryCursorPosition);
                            chest.inventory.removeByIndex(inventoryCursorPosition);
                            hero.inventory.addItem(item);
                        }
                        inventoryCursorPosition = Math.max(inventoryCursorPosition - 1, 0);
                    }
                    else if (pressedKey.getCharacter() == 'e' || pressedKey.getCharacter() == 'у') {
                        Container container;
                        if (inventoryWindowIsActive) {
                            container = hero.inventory;
                            }
                        else container = chest.inventory;
                        if (container.isNotEmpty()) {
                            Item item = container.getByIndex(inventoryCursorPosition);
                            if (item instanceof Equipment) {
                                hero.equip(item);
                            } else if (item instanceof Weapon) {
                                hero.setWeapon((Weapon) item);
                                container.removeByIndex(inventoryCursorPosition);
                            }
                        }
                    } else if (pressedKey.getCharacter() == 'c' || pressedKey.getCharacter() == 'с') {
                        if (inventoryWindowIsActive) hero.inventory.clear();
                        else chest.inventory.clear();
                    }
                }
            }
            pressedKey = null;
        }
    }
}
