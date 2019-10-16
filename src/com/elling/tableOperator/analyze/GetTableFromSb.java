package com.elling.tableOperator.analyze;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取文件中操作的表名
 * 功能：
 * 	将制定文件（比如：xml、html、java）中涉及到表的操作（这里的操作，指的是其中的insert、update、merge、delete、selete）提取出来
 * 过滤，为的是将表快速提取，方便分析这些文件总共操作了哪些表
 * 这里insert、update、merge、delete合起来查询，这些操作一般就跟在后面的一张表，不会出现一行操作多张表的情况，所以合在一起
 * 这里的procedure、function是有具体特征的，每个系统都不一样，所以这里需要修改，如现在项目存储过程以SP_开头，函数以SF_开头
 * 这里select查询相对比较复杂，会出现多张表，inner/left/right join等多种情况，所以单独做一遍查询
 * 所以对一个文件会出现遍历两遍的情况，这样会比较好
 * @author cyy
 * @date  20191010
 */
public class GetTableFromSb {
	private HashMap<String,Object> allTableMap = new HashMap();//所有表的相关表
	private HashMap<String,Object> insertMap = new HashMap();//insert的相关表
	private HashMap<String,Object> updateMap = new HashMap();//update的相关表
	private HashMap<String,Object> mergeMap = new HashMap();//merge的相关表
	private HashMap<String,Object> deleteMap = new HashMap();//delete的相关表
	private HashMap<String,Object> selectMap = new HashMap();//select的相关表
	private HashMap<String,Object> procedureMap = new HashMap();//procedure的相关表
	private HashMap<String,Object> functionMap = new HashMap();//function的相关表
	
	/**
	 * 获取所有的表
	 * @param sb
	 */
	public void getAllTable(String sb) {
		getOpeTable(sb);
		getSelectTable(sb);
	}
	
	
	/**
	 * 获取存储过程、xml等xml文件中的表名
	 * 匹配正则表达式
	 * 正则表达式主要有这几种：
	 * 1、取出insert中的表，
	 *  格式如下：
	 *  	1)INSERT INTO XXX (COL1,COL2...)
	 *  	2)INSERT INTO XXX SELECT XXX
	 * 	正则表达式如下：INSERT\s+INTO\s+(\b\w+\b) -> 取出group(1)
	 * 
	 * 2、取出update中的表,
	 *	格式如下：
	 *		1）UPDATE XXX SET ...
	 *		2）MERGE INTO XXX 
	 *		3）排除掉 MERGE INTO XXXX CONDITION THEN UPDATE SET...
	 *		4）这里需要考虑update这种情况  when matched then update set a.clt_zmt = xxx...
	 * 	正则表达式如下：
	 * 		1）UPDATE\s+(\b\w+\b)(|\s+\b\w+\b)\s+SET  ->取出group(1)
	 * 		2）MERGE\s+INTO\s+(\b\w+\b) ->取出group(1)
	 * 
	 * 3、取出delete中的表
	 * 	格式如下：1）DELETE FROM XXX WHERE ....
	 * 	正则表达式如下：DELETE\s+FROM\s+(\b\w+\b)	->取出group(1)
	 * 
	 * 4、取出存储过程/函数
	 * 	格式如下：
	 * 		1）SP_XXX_XXX
	 * 		2）SF_XXX_XXX
	 * 	正则表达式:
	 * 		(SP_\w+\b)  ->取出group(1)
	 * 		(SF_\w+\b)  ->取出group(1)
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
	 * 正则表达式
	 * 1、取出select中的表
	 * 	格式如下：
	 * 	1）SELECT XXX FROM SYS_DICT A,SYS_USER B,SYS_CONFIG C...
	 * 	2）SELECT XXX FROM SYS_DICT A,SYS_USER B LEFT JOIN SYS_MENU M ON M.ID = A.ID....
	 * 
	 * 2、正则表达式说明如下：分两步走，第一步找出长串，第二步根据逗号分割，第三步在解析对应的表出来
	 *  1)第一步正则从FROM后面开始( A , XXX)由多个这个组成
	 *  2)第二步把整个大串整个取出来,使用逗号分隔
	 *  3)第三步，取出具体的表
	 * note:
	 * 	1）这里有个问题就是如果是会出换行符就有点搞不定了，一个好的方法就是解析StringBuffer的时候，不加\n\t，连续读取到内存中
	 * 	2）去掉delete from后的表（其实这个不去掉也可以的，反正都是表，多一个也无所谓）
	 * 3、正则表达式如下
	 * 	第一步：(?<!DELETE)\s+FROM\s+((\b\w+\b)(\s*\b\w+\b\s*,\s*(\b\w+\b))*)|(LEFT|RIGHT|INNER)\s+JOIN\s+(\b\w+\b)
	 * 	第二步：SYS_DICT A,SYS_USER B,SYS_CONFIG C   -> group(1).toString().split(",");
	 *  第三步：\s*(\b\w+\b)  ->group(1)
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
			if(m1.group(5)!=null) {//如果是left/right/inner join xxx的情况下
				tableName = m1.group(6);
				selectMap.put(tableName, tableName);
			}else {//如果是table1 t1, table2 t2, table3 t3...的情况下
				tableName = m1.group(2);
				selectMap.put(tableName, tableName);
				
				if(m1.group(3)!=null) {//存在查询多张表的情况下才执行下面的代码
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
