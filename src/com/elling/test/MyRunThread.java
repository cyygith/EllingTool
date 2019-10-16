package com.elling.test;

public class MyRunThread implements Runnable {
	private String name;
	
	public MyRunThread(String name){
		this.name = name;
	}
	
	public void run() {
		System.out.println(Thread.currentThread().getName() +":进程开始执行-----------------");
		SyncO syncO = new SyncO(Thread.currentThread().getName());
		syncO.doFirstThing();
		
		syncO.doSynchronizedThings();
		
		syncO.doAnother();
		
		System.out.println(Thread.currentThread().getName() +":进程结束执行-----------------");
	}
	
}
