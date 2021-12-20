package com.company.magic;

public class Spell<T extends MagicElement> {
    private T spell;

    public Spell(T spell) { this.spell = spell; }

    public T getSpell() { return spell; }

    public void setSpell(T spell) { this.spell = spell; }
}
