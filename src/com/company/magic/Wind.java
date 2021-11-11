package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Wind implements MagicElement
{
    @Override
    public int getDamage() {
        return ThreadLocalRandom.current().nextInt(14) + 2;
    }

    @Override
    public String getName() {
        return "Wind gust";
    }
}
