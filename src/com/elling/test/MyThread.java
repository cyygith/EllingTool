package com.elling.test;

public class MyThread extends Thread {
	private String name;
	
	public MyThread(String name){
		this.name = name;
	}
	
	public void run() {
		System.out.println(this.name +":���̿�ʼִ��-----------------");
		SyncO syncO = new SyncO(this.name);
		syncO.doFirstThing();
		
		syncO.doSynchronizedThings();
		
		syncO.doAnother();
		
		System.out.println(this.name +":���̽���ִ��-----------------");
	}
	
}
