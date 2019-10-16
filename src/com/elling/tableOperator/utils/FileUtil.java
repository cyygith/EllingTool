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
	 * 获取文件的路径，并且根据文件类型取出指定的文件放到list中
	 * 
	 * @param path		文件路径
	 * @param depth		文件深度	
	 * @param filetype  文件类型
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
							list.add(file.getCanonicalPath());   //取出文件的具体路径
							Map m = new HashMap();
							m.put(name, file.getCanonicalPath());
							listMap.add(m);
						}
						
					}
				}
			}else{
				if(dirFile.getName().matches(".*[\\.]+("+filetype+")")){
					list.add(dirFile.getCanonicalPath());   //取出文件的具体路径
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
	 * 保存文件
	 * @param path  路径
	 * @param name  文件名
	 * @param sb	文件内容
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
	 * 获取
	 * @return
	 */
	public ArrayList getList(){
		return list;
	}
	
	/**
	 * 获取
	 * @return
	 */
	public ArrayList getListMap(){
		return listMap;
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		FileUtil util = new FileUtil();
//		find("E:\\WORK\\workspace\\MyProject\\src",depth,filetype);
//		find("C:\\Users\\Administrator\\Desktop\\上线相关\\PopUpOnline\\proj\\emaster",depth,filetype);
		util.find("C:\\Users\\Administrator\\Desktop\\上线相关\\PopUpOnline\\proj\\ellingmaster",1,"xml|html");
		System.out.println("指定文件的个数："+util.getList().size());
		List list = util.getList();
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
}
