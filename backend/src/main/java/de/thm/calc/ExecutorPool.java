// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.calc;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Holds a pool of threads for all computations for Enhort
 */
public class ExecutorPool {

    private static final int threadCount;
    private volatile static ExecutorPool instance;

    static {
        if(System.getenv("HOME").contains("menzel")) {
            threadCount = 16; //local
        } else {
            threadCount = 64; //remote
        }
    }

    private final Logger logger = LoggerFactory.getLogger(ExecutorPool.class);
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1024);
    private ThreadPoolExecutor exe = new ThreadPoolExecutor(threadCount, threadCount, 30L, TimeUnit.SECONDS, queue);

    private ExecutorPool(){

        // Monitoring thread
        Thread load = new Thread(() -> {
            while(true) {
                if(exe.getActiveCount() > 0)
                    logger.debug("currently running " + exe.getActiveCount() + " computation threads");
                try {
                    Thread.sleep(500);
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

    /**
     * Submits a task to the thread pool with a fixed timeout of 2 minutes
     *
     * @param task - task to submit
     *
     * @return returns a Future (without result) to check if the task is finished
     */
    public Future submit(Runnable task) {
        Future f = this.exe.submit(task);

        try {
            f.get(2, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warn("Stopped a thread after Exception " + e.toString() + "\t");
            e.printStackTrace();
        }

        return f;
    }
}
