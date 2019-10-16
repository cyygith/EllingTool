package com.elling.test;

public class SyncO {
	private String name;
	
	public SyncO(String name){
		this.name = name;
	}
	
	public void doFirstThing(){
		System.out.println(this.name+":do firstThings start...");
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(this.name+":do firstThings end...");
	}
	
	public  void doSynchronizedThings(){
		synchronized(SyncO.class){
			System.out.println(this.name+":do 同步事情   start  ************ ");
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(this.name+":do 同步事情   end  ************ ");
		}
	}
	
	
	public void doAnother(){
		System.out.println(this.name+":同步完成做其他事情了 start...");
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(this.name+":同步完成做其他事情了 end...");
	}
	
	
}
