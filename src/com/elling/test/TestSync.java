package com.elling.test;

public class TestSync {
	
	
	public static void main(String[] args) {
		TestSync testSync = new TestSync();
		testSync.threadTest();//thread test
		testSync.RunnableTest();//runnable test
	}
	/**
	 * Thread  �̵߳Ĳ��Է���
	 */
	public  void threadTest(){
		for(int i=0;i<5;i++){
			Thread thread = new MyThread(i+"");
			thread.start();
		}
	}
	/**
	 * runnable  �̵߳Ĳ��Է���
	 */
	public  void RunnableTest(){
		MyRunThread mythread = new MyRunThread("");
		for(int i=0;i<5;i++){
			Thread thread = new Thread(mythread,"thread"+i);
			thread.start();
		}
	}
}
