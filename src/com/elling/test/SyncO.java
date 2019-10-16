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
			System.out.println(this.name+":do ͬ������   start  ************ ");
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(this.name+":do ͬ������   end  ************ ");
		}
	}
	
	
	public void doAnother(){
		System.out.println(this.name+":ͬ����������������� start...");
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(this.name+":ͬ����������������� end...");
	}
	
	
}
