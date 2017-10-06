package de.thm.calc;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ExecutorPool {

    private static final int threadCount;
    private static ExecutorPool instance;

    static {
        if(System.getenv("HOME").contains("menzel")) {
            threadCount = 16; //local
        } else {
            threadCount = 64; //remote
        }
    }

    private final int timeoutSeconds = 30* 1000;
    private final Logger logger = LoggerFactory.getLogger(ExecutorPool.class);
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1024);
    private ThreadPoolExecutor exe = new ThreadPoolExecutor(threadCount, threadCount, 30L, TimeUnit.SECONDS, queue);

    private ExecutorPool(){


        Thread load = new Thread(() -> {
            while(true) {
                if(exe.getActiveCount() > 0)
                    logger.debug("currently running " + exe.getActiveCount() + " computation threads");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        load.start();



    }

    public static ExecutorPool getInstance() {
        if(instance == null)
            instance = new ExecutorPool();
        return instance;
    }

    public Future submit(Runnable task) {
        return this.exe.submit(task);
    }
}
