package com.company.objects;

import com.company.objects.items.Item;
import com.company.recources.Colors;

import java.util.ArrayList;

import static com.company.gameplay.GameLogic.addToLog;

public class Container {
    private final int maxSize;
    private String name;
    private final ArrayList<Item> items = new ArrayList<>();

    public Container(String name, int maxSize) {
        this.name = name;
        this.maxSize = maxSize;
    }

    public Container(String name) {
        this(name, 10);
    }

    public void clear() {
        addToLog(name + " cleared. Number of deleted items: " + Colors.RED + getSize() + Colors.RESET + "!");
        items.clear();
    }

    public void addItem(Item item) {
        if (items.size() < maxSize) items.add(item);
    }

    public void addAllItems(ArrayList<Item> items) {
        this.items.addAll(items);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Item getByIndex(int index) { return items.get(index); }

    public void removeByIndex(int index) {
        items.remove(index);
    }

    public int getSize() { return items.size(); }

    public String getName() { return name; }

    public boolean isNotEmpty() { return !items.isEmpty(); }
}
