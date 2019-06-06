package com.hicsc.os;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemphoreTrend {

    public static void main(String[] args) {

        // 定义7个信号量，用于控制6个任务相互之间的前趋关系
        Semaphore t1t2 = new Semaphore(0);
        Semaphore t1t3 = new Semaphore(0);
        Semaphore t1t4 = new Semaphore(0);
        Semaphore t2t5 = new Semaphore(0);
        Semaphore t3t5 = new Semaphore(0);
        Semaphore t5t6 = new Semaphore(0);
        Semaphore t4t6 = new Semaphore(0);

        Task task1 = new Task(1, null, new Semaphore[]{t1t2,t1t3,t1t4});
        Task task2 = new Task(2, new Semaphore[]{t1t2}, new Semaphore[]{t2t5});
        Task task3 = new Task(3, new Semaphore[]{t1t3}, new Semaphore[]{t3t5});
        Task task4 = new Task(4, new Semaphore[]{t1t4}, new Semaphore[]{t4t6});
        Task task5 = new Task(5, new Semaphore[]{t2t5,t3t5}, new Semaphore[]{t5t6});
        Task task6 = new Task(6, new Semaphore[]{t5t6,t4t6}, null);

        new Thread(task1).start();
        new Thread(task2).start();
        new Thread(task3).start();
        new Thread(task4).start();
        new Thread(task5).start();
        new Thread(task6).start();

    }


    public static class Task implements Runnable {
        // 任务名称
        private int number;
        // 当前任务需要执行wait操作的信号量列表
        private Semaphore[] ps;
        // 当前任务执行完成后，需要执行signal的信号量列表
        private Semaphore[] vs;


        public Task(int number,Semaphore[] ps,Semaphore[] vs) {
            this.number = number;
            this.ps = ps;
            this.vs = vs;
        }

        @Override
        public void run() {
            try {
                if (ps != null) {
                    // 等待
                    while (true) {
                        for (Semaphore semaphore : ps) {
                            if (!semaphore.tryAcquire()) {
                                TimeUnit.MICROSECONDS.sleep(100);
                                break;
                            }
                        }
                        break;
                    }
                    // 执行wait操作
                    for (Semaphore semaphore : ps) {
                        semaphore.acquire();
                    }
                }
                runTask();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 具体的任务
         */
        public void runTask() throws InterruptedException {
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
            System.out.println("task " + number + " done");
            if (vs != null) {
                for (Semaphore semaphore : vs) {
                    semaphore.release();
                }
            }
        }
    }
}
