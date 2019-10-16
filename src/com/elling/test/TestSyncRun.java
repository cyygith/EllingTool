package com.elling.test;

public class TestSyncRun {
	
	
	public static void main(String[] args) {
		
		MyRunThread mythread = new MyRunThread("");
		for(int i=0;i<5;i++){
			Thread thread = new Thread(mythread,"thread"+i);
			thread.start();
		}
		
	}
	
	
}
