package org.firstinspires.ftc.teamcode.debug.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.RobotBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Async {

    private static final List<Thread> all = new ArrayList<>();

    public static void runTasksAsync(List<Runnable> fns) {
        ExecutorService executorService = Executors.newFixedThreadPool(fns.size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        fns.forEach(fn -> futures.add(CompletableFuture.runAsync(fn, executorService)));

        CompletableFuture<Void> allThreads = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allThreads.join();
        executorService.shutdown();
    }
    public static void runTasksAsync(Runnable... fns) {
        runTasksAsync(Arrays.stream(fns).collect(Collectors.toList()));
    }

    public static void async(Runnable fn) {
        Thread tF = new Thread(fn);
        tF.start();
        all.add(tF);
    }

    public static void stopAll() {
        all.forEach(Thread::interrupt);
        all.clear();
    }

    public static void sleep(long ms) {
        /*
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            System.out.println("failed to sleep");
        }*/
        long startTime = System.currentTimeMillis();
        while (!RobotBase.isStopRequested() && System.currentTimeMillis() - startTime < ms) {
            if (RobotBase.isStopRequested() || Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("failed to sleep");
            }
        }
    }
    /*
    // In Async.java
    public static void sleep(long millis, OpMode opMode) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < millis) {
            // This is the critical check!
            if (Thread.currentThread().isInterrupted() || (opMode.)) {
                break;
            }
            try {
                Thread.sleep(10); // Sleep in small chunks to keep the loop responsive
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }*/

}
