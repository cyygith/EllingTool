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
 * ���Է������ݿ��д洢���̻����ļ������õ��ı�
 * @author Administrator
 *
 */
public class GetTableTest {
	
	
	public static void main(String[] args) {
		GetTableTest test = new GetTableTest();
		String sourcePath = "E:\\WORK\\workspace\\EllingTool\\src";
		//�����ݿ���ȡ�洢���̲���
		String[] pNames= new String[] {"SP_INTND_SS_LOAD"};
		test.getTableFromType("2", pNames, sourcePath);
		
		//�Ӵ���disk��ȡsb
//		test.getTableFromType("1", pNames, sourcePath);
	}
	/**
	 * ������������Դ�������з���ȡ���洢���̡�������
	 * @param type  1���Ӵ�����ȡ��    2�������ݿ���ȡ�洢���̵Ķ���
	 * @param pNames
	 * @param pftype
	 */
	public void getTableFromType(String type,String[] pNames,String sourcePath) {
		GetTableFromSb getTableFromSb = new GetTableFromSb();
		FileUtil fileUtil = new FileUtil();
		HashMap<String,Object> allTableMap = new HashMap();//���б����ر�
		HashMap<String,Object> insertMap = new HashMap();//insert����ر�
		HashMap<String,Object> updateMap = new HashMap();//update����ر�
		HashMap<String,Object> mergeMap = new HashMap();//merge����ر�
		HashMap<String,Object> deleteMap = new HashMap();//delete����ر�
		HashMap<String,Object> selectMap = new HashMap();//select����ر�
		HashMap<String,Object> procedureMap = new HashMap();//procedure����ر�
		HashMap<String,Object> functionMap = new HashMap();//function����ر�
		
		//1��������Ӧ�ķ��������
		StringBuffer sb = new StringBuffer();
		if(type.equals("1")) {
			System.out.println("ָ���洢���̵ĸ�����" + pNames.length);
			for(int i=0,len=pNames.length;i<len;i++) {
				sb = StringBufUtil.getSbFromDatabase(pNames[i]);
				getTableFromSb.getAllTable(sb.toString());
			}
		}else if(type.equals("2")){
			fileUtil.find(sourcePath, 1, "sql|xml");
			ArrayList list = fileUtil.getList();
			System.out.println("ָ���ļ��ĸ�����" + list.size());
			for(int i=0,len=list.size();i<len;i++) {
				sb = StringBufUtil.getSbFromDisk(list.get(i).toString(),StringBufUtil.NO_KEEPFORMAT);
				getTableFromSb.getAllTable(sb.toString());
			}
		}
		
		//2��ȡ��Ӧ�ı�����Ӧ��map��
		insertMap.putAll(getTableFromSb.getInsertMap());
		updateMap.putAll(getTableFromSb.getUpdateMap());
		mergeMap.putAll(getTableFromSb.getMergeMap());
		deleteMap.putAll(getTableFromSb.getDeleteMap());
		selectMap.putAll(getTableFromSb.getSelectMap());
		procedureMap.putAll(getTableFromSb.getProcedureMap());
		functionMap.putAll(getTableFromSb.getFunctionMap());
		allTableMap.putAll(getTableFromSb.getAllTableMap());
		
		//3����ӡ��Ӧ���е�map��Ϣ
		Iterator insertIt= insertMap.entrySet().iterator();
		System.out.println("�ܹ�insert�ı��ж��ٸ���"+insertMap.size());
		while(insertIt.hasNext()) {
			Map.Entry entry = (Map.Entry)insertIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator updateIt= updateMap.entrySet().iterator();
		System.out.println("�ܹ�update�ı��ж��ٸ���"+updateMap.size());
		while(updateIt.hasNext()) {
			Map.Entry entry = (Map.Entry)updateIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator mergeIt= mergeMap.entrySet().iterator();
		System.out.println("�ܹ�merge�ı��ж��ٸ���"+mergeMap.size());
		while(mergeIt.hasNext()) {
			Map.Entry entry = (Map.Entry)mergeIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator deleteIt= deleteMap.entrySet().iterator();
		System.out.println("�ܹ�delete�ı��ж��ٸ���"+deleteMap.size());
		while(deleteIt.hasNext()) {
			Map.Entry entry = (Map.Entry)deleteIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator selectIt= selectMap.entrySet().iterator();
		System.out.println("�ܹ�select�ı��ж��ٸ���"+selectMap.size());
		while(selectIt.hasNext()) {
			Map.Entry entry = (Map.Entry)selectIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator procedureIt= procedureMap.entrySet().iterator();
		System.out.println("�ܹ�procedure�ж��ٸ���"+procedureMap.size());
		while(procedureIt.hasNext()) {
			Map.Entry entry = (Map.Entry)procedureIt.next();
			System.out.println(entry.getValue());
		}
		
		Iterator functionIt= functionMap.entrySet().iterator();
		System.out.println("�ܹ�function�ж��ٸ���"+functionMap.size());
		while(functionIt.hasNext()) {
			Map.Entry entry = (Map.Entry)functionIt.next();
			System.out.println(entry.getValue());
		}
		
		//4.��ѯ�����еı��ע��
		if(allTableMap.size()>0) {
			String tableParam = StringUtil.join(allTableMap).toString();
			String tablesql = "SELECT T.TABLE_NAME,T.COMMENTS FROM USER_TAB_COMMENTS T WHERE T.TABLE_NAME IN ("+tableParam+")";
			QueryOracleTable query = new QueryOracleTable();
			List<Map<String,Object>> tableList = query.getBySql(tablesql, null);
			System.out.println("�ܹ��漰���ٸ���"+allTableMap.size());
			for(int i=0,len=tableList.size();i<len;i++) {
				Map resultMap = tableList.get(i);
				System.out.println(resultMap.get("TABLE_NAME")+"\t"+resultMap.get("COMMENTS"));
			}
			
		}
		
		
		
	}
	
	
	
	
	
	
}
