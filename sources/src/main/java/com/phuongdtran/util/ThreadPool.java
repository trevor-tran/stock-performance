package com.phuongdtran.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
}
