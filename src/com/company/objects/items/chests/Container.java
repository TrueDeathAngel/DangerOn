package com.company.objects.items.chests;

import com.company.objects.items.Item;
import com.company.recources.colors.StringColors;

import java.util.ArrayList;

import static com.company.gameplay.GameLogic.addToLog;

public class Container {
    private final int maxSize;
    private final String name;
    private final ArrayList<Item> items = new ArrayList<>();

    public Container(String name, int maxSize) {
        this.name = name;
        this.maxSize = maxSize;
    }

    public void clear() {
        addToLog(name + " cleared. Number of deleted items: " + StringColors.RED + getSize() + StringColors.RESET + "!");
        items.clear();
    }

    public void addItem(int index, Item item) {
        if (items.size() < maxSize) items.add(index, item);
        else addToLog(name + " is full!");
    }

    public void addItem(Item item) {
        addItem(0, item);
    }

    public void addAllItems(ArrayList<Item> items) {
        this.items.addAll(items);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Item getByIndex(int index) { return items.get(index); }

    public void remove(int index) {
        items.remove(index);
    }

    public int getSize() { return items.size(); }

    public String getName() { return name; }

    public boolean isNotEmpty() { return !items.isEmpty(); }
}
