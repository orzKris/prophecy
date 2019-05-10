package com.kris.prophecy.lock;

/**
 * @author Kris
 * @date 2019/4/30
 */
public class SynchronizedLock {

    static class Lock1 implements Runnable {
        /**
         * 共享资源变量
         */
        private int count = 0;

        /**
         * 【1】synchronized 修饰实例方法
         * 需要获取对象锁才可以执行该run方法
         */
        @Override
        public synchronized void run() {
            for (int i = 0; i < 5; i++) {
                //count++不是原子操作，先读取值，再写回一个新值
                System.out.println(Thread.currentThread().getName() + ":" + count++);
                try {
                    //此处sleep是为了引发线程切换，并且sleep不会释放对象锁
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(String[] args) {
            Lock1 lock1 = new Lock1();
            Thread thread1 = new Thread(lock1);
            Thread thread2 = new Thread(lock1);
            thread1.start();
            thread2.start();
        }
    }

    static class Lock2 implements Runnable {
        /**
         * 共享资源变量
         */
        private int count = 0;

        /**
         * 不用synchronized修饰该方法的话，在count++操作时会出现线程不安全的情况
         * 上一个线程在写回值之前，该值被下一个线程读取
         */
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + count++);
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(String[] args) {
            Lock2 lock2 = new Lock2();
            Thread thread1 = new Thread(lock2);
            Thread thread2 = new Thread(lock2);
            thread1.start();
            thread2.start();
        }
    }

    static class Lock3 implements Runnable {
        /**
         * 共享资源变量
         */
        private static int count = 0;

        @Override
        public void run() {
            increaseCount();
        }

        /**
         * 【2】synchronized 修饰静态方法
         * 需要获取类锁才可以执行该方法
         */
        public static synchronized void increaseCount() {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + count++);
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void main(String[] args) {
            Lock3 lockA = new Lock3();
            Lock3 lockB = new Lock3();
            Thread thread1 = new Thread(lockA);
            Thread thread2 = new Thread(lockB);
            thread1.start();
            thread2.start();
        }
    }
}
