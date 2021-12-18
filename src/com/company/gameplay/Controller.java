package com.company.gameplay;

import java.util.concurrent.TimeUnit;

public abstract class Controller implements Runnable {
    private final int updateFrequency;  // in milliseconds

    private volatile boolean paused = false;

    private volatile boolean cancelled = false;

    public Controller(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public Controller() {
        this(10);
    }

    @Override
    public void run() {
        try {
            while (!cancelled) {

                if(!paused)
                    step();

                try {
                    TimeUnit.MILLISECONDS.sleep(updateFrequency);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public void start() {
        new Thread(this).start();
    }

    public abstract void step();
}
