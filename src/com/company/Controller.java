package com.company;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Controller implements Runnable {

    private final ReadWriteLock pause = new ReentrantReadWriteLock();

    private volatile boolean cancelled = false;

    @Override
    public void run() {
        try {
            while (!cancelled) {

                blockIfPaused();

                step();

                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blockIfPaused() throws InterruptedException {
        try {
            pause.writeLock().lockInterruptibly();
        } finally {
            pause.writeLock().unlock();
        }

    }

    public void pause() {
        pause.readLock().lock();
    }

    public void resume() {
        pause.readLock().unlock();
    }

    public void cancel() {
        cancelled = true;
    }

    public void start() {
        new Thread(this).start();
    }

    public abstract void step() throws Exception;
}
