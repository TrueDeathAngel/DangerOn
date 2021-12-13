package com.company.gameplay;

import java.util.concurrent.TimeUnit;

public abstract class Controller implements Runnable {

    private volatile boolean paused = false;

    private volatile boolean cancelled = false;

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        try {
            while (!cancelled) {

                if(!paused)
                    step();

                try {
                    TimeUnit.MILLISECONDS.sleep(10);
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
