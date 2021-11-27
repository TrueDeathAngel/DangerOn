package com.company.objects;

public abstract class GameObject
{
    private String name;

    public String getName() { return name; }

    public GameObject(String name) { this.name = name; }
}
