package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Fire implements MagicElement
{
    @Override
    public int getDamage()
    {
        return ThreadLocalRandom.current().nextInt(7) + 5;
    }

    @Override
    public String getName() {
        return "Fiery incineration";
    }
}
