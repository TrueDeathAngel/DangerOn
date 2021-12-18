package com.company.objects;

public abstract class GameObject
{
    private final String name;

    public String getName() { return name; }

    public GameObject(String name) { this.name = name; }
}
