package com.company.gameplay;

import com.company.objects.items.Chest;
import com.company.objects.items.Equipment;
import com.company.objects.items.Item;
import com.company.objects.items.Weapon;

import java.util.ArrayList;

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
                    if(inventoryCursorPosition < (inventoryWindowIsActive ? hero.inventory.size() : chest.items.size()) - 1)
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
                        if (inventoryWindowIsActive && !hero.inventory.isEmpty()) {
                            Item item = hero.inventory.get(inventoryCursorPosition);
                            hero.inventory.remove(item);
                            chest.items.add(item);
                        } else if (!chest.items.isEmpty()) {
                            Item item = chest.items.get(inventoryCursorPosition);
                            chest.items.remove(item);
                            hero.inventory.add(item);
                        }
                        inventoryCursorPosition = Math.max(inventoryCursorPosition - 1, 0);
                    }
                    else if (pressedKey.getCharacter() == 'e' || pressedKey.getCharacter() == 'у') {
                        ArrayList<Item> container;
                        if (inventoryWindowIsActive) {
                            container = hero.inventory;
                            }
                        else container = chest.items;
                        if (!container.isEmpty()) {

                            Item item = container.get(inventoryCursorPosition);
                            if (item instanceof Equipment) {
                                Equipment.EquipmentType type = ((Equipment) item).getEquipmentType();
                                if (hero.equipmentItems.containsKey(type)) {
                                    container.add(hero.equipmentItems.get(type));
                                }
                                hero.equipmentItems.put(type, (Equipment) item);
                                container.remove(item);
                            } else if (item instanceof Weapon) {
                                if (hero.weapon != null) container.add(hero.weapon);
                                hero.weapon = (Weapon) item;
                                container.remove(item);
                            }
                        }
                    } else if (pressedKey.getCharacter() == 'c' || pressedKey.getCharacter() == 'с') {
                        if (inventoryWindowIsActive) hero.inventory.clear();
                        else chest.items.clear();
                    }
                }
            }
            pressedKey = null;
        }
    }
}
