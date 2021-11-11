package com.company.magic;

import java.util.concurrent.ThreadLocalRandom;

public class Water implements MagicElement
{
    @Override
    public int getDamage() {
        return ThreadLocalRandom.current().nextInt(6) + 6;
    }

    @Override
    public String getName() {
        return "Water flow";
    }
}
