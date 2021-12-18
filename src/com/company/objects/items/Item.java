package com.company.objects.items;

import com.company.objects.GameObject;

public abstract class Item extends GameObject
{
    private final int price;

    public Item(String name, int price)
    {
        super(name);
        this.price = price;
    }

    public int getPrice() { return price; }
}
