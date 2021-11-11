package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Earth implements MagicElement
{
    @Override
    public int getDamage() {
        return ThreadLocalRandom.current().nextInt(14) + 2;
    }

    @Override
    public String getName() {
        return "Boulder";
    }
}
