package com.example.chupa_lupa.server;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class IncrementThread  extends Thread{
    final double REDUCE_VALUE=0.1;
    Random random;
    AtomicReference<Double> value;

    public IncrementThread(AtomicReference<Double> value){
        this.value=value;
        random= new Random();
    }

    @Override
    public void run() {
        while (value.get()>=0) {
            randIncrement();
        }
    }

    synchronized void randIncrement() {
        int time=1000 * random.nextInt(1,5);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        value.updateAndGet(value->value-REDUCE_VALUE);
        System.out.println(value.get() + Thread.currentThread().getName() + " " + time);
    }
}
