package com.elling.tableOperator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
	public  int depth = 1;
	public  String filetype = "xml|html";
	private  ArrayList<String> list = new ArrayList<String>();
	private  ArrayList<Map> listMap = new ArrayList<Map>();
	
	/**
	 * ��ȡ�ļ���·�������Ҹ����ļ�����ȡ��ָ�����ļ��ŵ�list��
	 * 
	 * @param path		�ļ�·��
	 * @param depth		�ļ����	
	 * @param filetype  �ļ�����
	 */
	public void find(String path,int depth,String filetype){
		int filecount = 0;
		
		try{
			File dirFile = new File(path);
			if(!dirFile.exists()){
				System.out.println("do not exist");
				return;
			}
			
			for(int j=0;j<depth;j++){
				System.out.print(" ");
			}
			System.out.print("|---");
			System.out.println(dirFile.getName());
			
			String[] fileList = dirFile.list();
			int currentDepth = depth + 1;
			if(null!=fileList&&fileList.length>0){
				for(int i=0;i<fileList.length;i++){
					String string = fileList[i];
					File file = new File(dirFile.getPath(),string);
					String name = file.getName();
					
					if(file.isDirectory()){
						find(file.getCanonicalPath(),currentDepth,filetype);
					}else{
						for(int j=0;j<currentDepth;j++){
							System.out.print(" ");
						}
						System.out.print("|--");
						System.out.println(name);
						
						if(name.matches(".*[\\.]+("+filetype+")")){
							list.add(file.getCanonicalPath());   //ȡ���ļ��ľ���·��
							Map m = new HashMap();
							m.put(name, file.getCanonicalPath());
							listMap.add(m);
						}
						
					}
				}
			}else{
				if(dirFile.getName().matches(".*[\\.]+("+filetype+")")){
					list.add(dirFile.getCanonicalPath());   //ȡ���ļ��ľ���·��
					Map m = new HashMap();
					m.put(dirFile.getName(), dirFile.getCanonicalPath());
					listMap.add(m);
				}
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			
		}
	}
	
	/**
	 * �����ļ�
	 * @param path  ·��
	 * @param name  �ļ���
	 * @param sb	�ļ�����
	 */
	public static void saveFile(String path,String name,StringBuffer sb) {
		File dirFile = new File(path+File.separator+name);
		try {
			if(!dirFile.exists()){
				File dir = new File(dirFile.getParent());
				dir.mkdirs();
				dirFile.createNewFile();
			}
			PrintWriter p = new PrintWriter(new FileOutputStream(dirFile));
			p.write(sb.toString());
			p.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ȡ
	 * @return
	 */
	public ArrayList getList(){
		return list;
	}
	
	/**
	 * ��ȡ
	 * @return
	 */
	public ArrayList getListMap(){
		return listMap;
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		FileUtil util = new FileUtil();
//		find("E:\\WORK\\workspace\\MyProject\\src",depth,filetype);
//		find("C:\\Users\\Administrator\\Desktop\\�������\\PopUpOnline\\proj\\emaster",depth,filetype);
		util.find("C:\\Users\\Administrator\\Desktop\\�������\\PopUpOnline\\proj\\ellingmaster",1,"xml|html");
		System.out.println("ָ���ļ��ĸ�����"+util.getList().size());
		List list = util.getList();
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
}
