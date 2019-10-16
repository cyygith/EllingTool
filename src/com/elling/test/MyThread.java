package com.elling.test;

public class MyThread extends Thread {
	private String name;
	
	public MyThread(String name){
		this.name = name;
	}
	
	public void run() {
		System.out.println(this.name +":进程开始执行-----------------");
		SyncO syncO = new SyncO(this.name);
		syncO.doFirstThing();
		
		syncO.doSynchronizedThings();
		
		syncO.doAnother();
		
		System.out.println(this.name +":进程结束执行-----------------");
	}
	
}
