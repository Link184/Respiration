package com.link184.respiration.repository.local;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.Semaphore;

/**
 * Created by eugeniu on 3/5/18.
 */

class WriteHandler extends Handler {
    WriteHandler(String name) {
        this(name, Process.THREAD_PRIORITY_BACKGROUND);
    }

    WriteHandler(String handlerName, int handlerPriority) {
        super(startHandlerThread(handlerName, handlerPriority));
    }

    private static Looper startHandlerThread(String name, int priority) {
        final Semaphore semaphore = new Semaphore(0);
        HandlerThread handlerThread = new HandlerThread(name, priority) {
            protected void onLooperPrepared() {
                semaphore.release();
            }
        };
        handlerThread.start();
        semaphore.acquireUninterruptibly();
        return handlerThread.getLooper();
    }

    public void quit() {
        getLooper().quit();
    }
}
