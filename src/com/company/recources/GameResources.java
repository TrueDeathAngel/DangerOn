package com.company.recources;

import com.company.objects.items.chests.Chest;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;

import java.util.HashMap;
import java.util.Map;

public class GameResources {
    public static final String version = "5.1.0 alpha";
    // [global version - big update].[regular updates before next version].[small changes and bug fixes before next update]

    public static final String[] mobNames = {"Zombie", "Ogre", "Skeleton", "Cyclops", "Goblin", "Centaurs", "Fauns", "Minotaur", "Demon", "Imp"};
    public static String[] weaponNames = {"Sword", "Axe", "Pickaxe", "Bow", "Crossbow", "Spear", "Dagger", "Mace", "Hammer", "Sickle"};
    public static String[] chestNames = {"Small", "Regular", "Big"};

    public static HashMap<Chest.ChestTypes, TextCharacter> chestModels = new HashMap<>(Map.of(
            Chest.ChestTypes.REGULAR,
            new TextCharacter(
                    '☐',
                    TextColor.ANSI.WHITE,
                    TextColor.ANSI.DEFAULT),
            Chest.ChestTypes.GRAVE,
            new TextCharacter(
                    '†',
                    TextColor.ANSI.WHITE,
                    TextColor.ANSI.DEFAULT),
            Chest.ChestTypes.HERO,
            new TextCharacter(
                    '☐',
                    new TextColor.RGB(0, 200, 100),
                    TextColor.ANSI.DEFAULT)));
}