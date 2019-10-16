package com.elling.tableOperator.analyze;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ��ȡ�ļ��в����ı���
 * ���ܣ�
 * 	���ƶ��ļ������磺xml��html��java�����漰����Ĳ���������Ĳ�����ָ�������е�insert��update��merge��delete��selete����ȡ����
 * ���ˣ�Ϊ���ǽ��������ȡ�����������Щ�ļ��ܹ���������Щ��
 * ����insert��update��merge��delete��������ѯ����Щ����һ��͸��ں����һ�ű��������һ�в������ű����������Ժ���һ��
 * �����procedure��function���о��������ģ�ÿ��ϵͳ����һ��������������Ҫ�޸ģ���������Ŀ�洢������SP_��ͷ��������SF_��ͷ
 * ����select��ѯ��ԱȽϸ��ӣ�����ֶ��ű�inner/left/right join�ȶ�����������Ե�����һ���ѯ
 * ���Զ�һ���ļ�����ֱ�������������������ȽϺ�
 * @author cyy
 * @date  20191010
 */
public class GetTableFromSb {
	private HashMap<String,Object> allTableMap = new HashMap();//���б����ر�
	private HashMap<String,Object> insertMap = new HashMap();//insert����ر�
	private HashMap<String,Object> updateMap = new HashMap();//update����ر�
	private HashMap<String,Object> mergeMap = new HashMap();//merge����ر�
	private HashMap<String,Object> deleteMap = new HashMap();//delete����ر�
	private HashMap<String,Object> selectMap = new HashMap();//select����ر�
	private HashMap<String,Object> procedureMap = new HashMap();//procedure����ر�
	private HashMap<String,Object> functionMap = new HashMap();//function����ر�
	
	/**
	 * ��ȡ���еı�
	 * @param sb
	 */
	public void getAllTable(String sb) {
		getOpeTable(sb);
		getSelectTable(sb);
	}
	
	
	/**
	 * ��ȡ�洢���̡�xml��xml�ļ��еı���
	 * ƥ��������ʽ
	 * ������ʽ��Ҫ���⼸�֣�
	 * 1��ȡ��insert�еı�
	 *  ��ʽ���£�
	 *  	1)INSERT INTO XXX (COL1,COL2...)
	 *  	2)INSERT INTO XXX SELECT XXX
	 * 	������ʽ���£�INSERT\s+INTO\s+(\b\w+\b) -> ȡ��group(1)
	 * 
	 * 2��ȡ��update�еı�,
	 *	��ʽ���£�
	 *		1��UPDATE XXX SET ...
	 *		2��MERGE INTO XXX 
	 *		3���ų��� MERGE INTO XXXX CONDITION THEN UPDATE SET...
	 *		4��������Ҫ����update�������  when matched then update set a.clt_zmt = xxx...
	 * 	������ʽ���£�
	 * 		1��UPDATE\s+(\b\w+\b)(|\s+\b\w+\b)\s+SET  ->ȡ��group(1)
	 * 		2��MERGE\s+INTO\s+(\b\w+\b) ->ȡ��group(1)
	 * 
	 * 3��ȡ��delete�еı�
	 * 	��ʽ���£�1��DELETE FROM XXX WHERE ....
	 * 	������ʽ���£�DELETE\s+FROM\s+(\b\w+\b)	->ȡ��group(1)
	 * 
	 * 4��ȡ���洢����/����
	 * 	��ʽ���£�
	 * 		1��SP_XXX_XXX
	 * 		2��SF_XXX_XXX
	 * 	������ʽ:
	 * 		(SP_\w+\b)  ->ȡ��group(1)
	 * 		(SF_\w+\b)  ->ȡ��group(1)
	 */
	public void getOpeTable(String sb) {
		String pattern = "INSERT\\s+INTO\\s+(\\b\\w+\\b)|UPDATE\\s+(\\b\\w+\\b)(|\\s+\\b\\w+\\b)\\s+SET|MERGE\\s+INTO\\s+(\\b\\w+\\b)|DELETE\\s+FROM\\s+(\\b\\w+\\b)|(SP_\\w+\\b)|(SF_\\w+\\b)";
		Pattern opeP = Pattern.compile(pattern);
		
		Matcher opeM = opeP.matcher(sb);
		String tableName = "";
		while(opeM.find()) {
			if(opeM.group(1)!=null) {
				tableName = opeM.group(1);
				insertMap.put(tableName, tableName);
			}else if(opeM.group(2)!=null) {
				tableName = opeM.group(2);
				updateMap.put(tableName, tableName);
			}else if(opeM.group(4)!=null) {
				tableName = opeM.group(4);
				mergeMap.put(tableName, tableName);
			}else if(opeM.group(5)!=null) {
				tableName = opeM.group(5);
				deleteMap.put(tableName, tableName);
			}else if(opeM.group(6)!=null) {
				tableName = opeM.group(6);
				procedureMap.put(tableName, tableName);
			}else if(opeM.group(7)!=null) {
				tableName = opeM.group(7);
				functionMap.put(tableName, tableName);
			}
		}
	}
	
	/**
	 * ������ʽ
	 * 1��ȡ��select�еı�
	 * 	��ʽ���£�
	 * 	1��SELECT XXX FROM SYS_DICT A,SYS_USER B,SYS_CONFIG C...
	 * 	2��SELECT XXX FROM SYS_DICT A,SYS_USER B LEFT JOIN SYS_MENU M ON M.ID = A.ID....
	 * 
	 * 2��������ʽ˵�����£��������ߣ���һ���ҳ��������ڶ������ݶ��ŷָ�������ڽ�����Ӧ�ı����
	 *  1)��һ�������FROM���濪ʼ( A , XXX)�ɶ��������
	 *  2)�ڶ���������������ȡ����,ʹ�ö��ŷָ�
	 *  3)��������ȡ������ı�
	 * note:
	 * 	1�������и������������ǻ�����з����е�㲻���ˣ�һ���õķ������ǽ���StringBuffer��ʱ�򣬲���\n\t��������ȡ���ڴ���
	 * 	2��ȥ��delete from��ı���ʵ�����ȥ��Ҳ���Եģ��������Ǳ���һ��Ҳ����ν��
	 * 3��������ʽ����
	 * 	��һ����(?<!DELETE)\s+FROM\s+((\b\w+\b)(\s*\b\w+\b\s*,\s*(\b\w+\b))*)|(LEFT|RIGHT|INNER)\s+JOIN\s+(\b\w+\b)
	 * 	�ڶ�����SYS_DICT A,SYS_USER B,SYS_CONFIG C   -> group(1).toString().split(",");
	 *  ��������\s*(\b\w+\b)  ->group(1)
	 *
	 */
	public void getSelectTable(String sb) {
		String pattern = "(?<!DELETE)\\s+FROM\\s+((\\b\\w+\\b)(\\s*\\b\\w+\\b\\s*,\\s*(\\b\\w+\\b))*)|(LEFT|RIGHT|INNER)\\s+JOIN\\s+(\\b\\w+\\b)";
		String pattern2 = "\\s*(\\b\\w+\\b)";
		Pattern p1 = Pattern.compile(pattern);
		Pattern p2 = Pattern.compile(pattern2);
		Matcher m1 = p1.matcher(sb);
		Matcher m2 = null;
		int i=0;
		String tableName = "";
		while(m1.find()) {
			if(m1.group(5)!=null) {//�����left/right/inner join xxx�������
				tableName = m1.group(6);
				selectMap.put(tableName, tableName);
			}else {//�����table1 t1, table2 t2, table3 t3...�������
				tableName = m1.group(2);
				selectMap.put(tableName, tableName);
				
				if(m1.group(3)!=null) {//���ڲ�ѯ���ű������²�ִ������Ĵ���
					String secondStr = m1.group(1);
					String[] secondTables = secondStr.split(",");
					for(int j=0,len=secondTables.length;j<len;j++) {
						m2 = p2.matcher(secondTables[i]);
						if(m2.find()) {
							tableName= m2.group(1);
							selectMap.put(tableName, tableName);
						}
					}
				}
			}
		}
		
	}

	public HashMap<String, Object> getInsertMap() {
		return insertMap;
	}

	public void setInsertMap(HashMap<String, Object> insertMap) {
		this.insertMap = insertMap;
	}

	public HashMap<String, Object> getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(HashMap<String, Object> updateMap) {
		this.updateMap = updateMap;
	}

	public HashMap<String, Object> getMergeMap() {
		return mergeMap;
	}

	public void setMergeMap(HashMap<String, Object> mergeMap) {
		this.mergeMap = mergeMap;
	}

	public HashMap<String, Object> getDeleteMap() {
		return deleteMap;
	}

	public void setDeleteMap(HashMap<String, Object> deleteMap) {
		this.deleteMap = deleteMap;
	}

	public HashMap<String, Object> getSelectMap() {
		return selectMap;
	}

	public void setSelectMap(HashMap<String, Object> selectMap) {
		this.selectMap = selectMap;
	}

	public HashMap<String, Object> getProcedureMap() {
		return procedureMap;
	}

	public void setProcedureMap(HashMap<String, Object> procedureMap) {
		this.procedureMap = procedureMap;
	}

	public HashMap<String, Object> getFunctionMap() {
		return functionMap;
	}

	public void setFunctionMap(HashMap<String, Object> functionMap) {
		this.functionMap = functionMap;
	}


	public HashMap<String, Object> getAllTableMap() {
		allTableMap.putAll(insertMap);
		allTableMap.putAll(updateMap);
		allTableMap.putAll(mergeMap);
		allTableMap.putAll(deleteMap);
		allTableMap.putAll(selectMap);
		return allTableMap;
	}


	public void setAllTableMap(HashMap<String, Object> allTableMap) {
		this.allTableMap = allTableMap;
	}
	
	
	
	
}
