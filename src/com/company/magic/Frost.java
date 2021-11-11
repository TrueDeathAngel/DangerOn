package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Frost implements MagicElement
{
    @Override
    public int getDamage() {
        return ThreadLocalRandom.current().nextInt(4) + 12;
    }

    @Override
    public String getName() {
        return "Refrigeration";
    }
}
