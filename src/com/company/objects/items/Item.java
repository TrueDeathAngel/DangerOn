package com.company.objects.items;

import com.company.objects.GameObject;

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
