package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Ether implements MagicElement
{
    @Override
    public int getDamage() {
        return ThreadLocalRandom.current().nextInt(16) + 1;
    }

    @Override
    public String getName() {
        return "Ether destruction";
    }
}
