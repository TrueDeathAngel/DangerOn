package com.company.items;

import com.company.GameObject;

public class Item extends GameObject
{
    private int price = 1;

    public Item(String name, int price)
    {
        super(name);
        this.price = price;
    }

    public int getPrice() { return price; }
}
