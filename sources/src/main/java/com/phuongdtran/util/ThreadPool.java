package com.phuongdtran.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

	private static ThreadPoolExecutor instance;

	private ThreadPool() {
	}

	public static ThreadPoolExecutor getInstance() {
		if(instance == null){
			synchronized (ThreadPool.class) {
				if(instance == null)
					instance = (ThreadPoolExecutor)Executors.newCachedThreadPool();
			}
		}
		return instance;
	}

	//http://www.baeldung.com/java-executor-wait-for-threads
	public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException ex) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
