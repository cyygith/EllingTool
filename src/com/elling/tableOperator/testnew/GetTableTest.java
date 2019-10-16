package com.elling.tableOperator.testnew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elling.database.QueryOracleTable;
import com.elling.tableOperator.analyze.GetTableFromSb;
import com.elling.tableOperator.utils.FileUtil;
import com.elling.tableOperator.utils.StringBufUtil;
import com.elling.tableOperator.utils.StringUtil;

/**
 * 测试分析数据库中存储过程或者文件中有用到的表
 * @author Administrator
 *
 */
public class GetTableTest {
	
	
	public static void main(String[] args) {
		GetTableTest test = new GetTableTest();
		String sourcePath = "E:\\WORK\\workspace\\EllingTool\\src";
		//从数据库中取存储过程测试
		String[] pNames= new String[] {"SP_INTND_SS_LOAD"};
		test.getTableFromType("2", pNames, sourcePath);
		
		//从磁盘disk中取sb
//		test.getTableFromType("1", pNames, sourcePath);
	}
	/**
	 * 根据数据流来源渠道进行分析取表、存储过程、函数等
	 * @param type  1：从磁盘中取数    2：从数据库中取存储过程的定义
	 * @param pNames
	 * @param pftype
	 */
	public void getTableFromType(String type,String[] pNames,String sourcePath) {
		GetTableFromSb getTableFromSb = new GetTableFromSb();
		FileUtil fileUtil = new FileUtil();
		HashMap<String,Object> allTableMap = new HashMap();//所有表的相关表
		HashMap<String,Object> insertMap = new HashMap();//insert的相关表
		HashMap<String,Object> updateMap = new HashMap();//update的相关表
		HashMap<String,Object> mergeMap = new HashMap();//merge的相关表
		HashMap<String,Object> deleteMap = new HashMap();//delete的相关表
		HashMap<String,Object> selectMap = new HashMap();//select的相关表
		HashMap<String,Object> procedureMap = new HashMap();//procedure的相关表
		HashMap<String,Object> functionMap = new HashMap();//function的相关表
		
		//1、操作对应的分析表操作
		StringBuffer sb = new StringBuffer();
		if(type.equals("1")) {
			System.out.println("指定存储过程的个数：" + pNames.length);
			for(int i=0,len=pNames.length;i<len;i++) {
				sb = StringBufUtil.getSbFromDatabase(pNames[i]);
				getTableFromSb.getAllTable(sb.toString());
			}
		}else if(type.equals("2")){
			fileUtil.find(sourcePath, 1, "sql|xml");
			ArrayList list = fileUtil.getList();
			System.out.println("指定文件的个数：" + list.size());
			for(int i=0,len=list.size();i<len;i++) {
				sb = StringBufUtil.getSbFromDisk(list.get(i).toString(),StringBufUtil.NO_KEEPFORMAT);
				getTableFromSb.getAllTable(sb.toString());
			}
		}
		
		//2、取对应的表放入对应的map中
		insertMap.putAll(getTableFromSb.getInsertMap());
		updateMap.putAll(getTableFromSb.getUpdateMap());
		mergeMap.putAll(getTableFromSb.getMergeMap());
		deleteMap.putAll(getTableFromSb.getDeleteMap());
		selectMap.putAll(getTableFromSb.getSelectMap());
		procedureMap.putAll(getTableFromSb.getProcedureMap());
		functionMap.putAll(getTableFromSb.getFunctionMap());
		allTableMap.putAll(getTableFromSb.getAllTableMap());
		
		//3、打印对应表中的map信息
		Iterator insertIt= insertMap.entrySet().iterator();
		System.out.println("总共insert的表有多少个："+insertMap.size());
		while(insertIt.hasNext()) {
			Map.Entry entry = (Map.Entry)insertIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator updateIt= updateMap.entrySet().iterator();
		System.out.println("总共update的表有多少个："+updateMap.size());
		while(updateIt.hasNext()) {
			Map.Entry entry = (Map.Entry)updateIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator mergeIt= mergeMap.entrySet().iterator();
		System.out.println("总共merge的表有多少个："+mergeMap.size());
		while(mergeIt.hasNext()) {
			Map.Entry entry = (Map.Entry)mergeIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator deleteIt= deleteMap.entrySet().iterator();
		System.out.println("总共delete的表有多少个："+deleteMap.size());
		while(deleteIt.hasNext()) {
			Map.Entry entry = (Map.Entry)deleteIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator selectIt= selectMap.entrySet().iterator();
		System.out.println("总共select的表有多少个："+selectMap.size());
		while(selectIt.hasNext()) {
			Map.Entry entry = (Map.Entry)selectIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator procedureIt= procedureMap.entrySet().iterator();
		System.out.println("总共procedure有多少个："+procedureMap.size());
		while(procedureIt.hasNext()) {
			Map.Entry entry = (Map.Entry)procedureIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator functionIt= functionMap.entrySet().iterator();
		System.out.println("总共function有多少个："+functionMap.size());
		while(functionIt.hasNext()) {
			Map.Entry entry = (Map.Entry)functionIt.next();
			System.out.println(entry.getValue());
		}
		
		//4.查询出所有的表的注释
		if(allTableMap.size()>0) {
			String tableParam = StringUtil.join(allTableMap).toString();
			String tablesql = "SELECT T.TABLE_NAME,T.COMMENTS FROM USER_TAB_COMMENTS T WHERE T.TABLE_NAME IN ("+tableParam+")";
			QueryOracleTable query = new QueryOracleTable();
			List<Map<String,Object>> tableList = query.getBySql(tablesql, null);
			System.out.println("总共涉及多少个表："+allTableMap.size());
			for(int i=0,len=tableList.size();i<len;i++) {
				Map resultMap = tableList.get(i);
				System.out.println(resultMap.get("TABLE_NAME")+"\t"+resultMap.get("COMMENTS"));
			}
			
		}
		
		
		
	}
	
	
	
	
	
	
}
