package com.elling.tableOperator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.elling.database.QueryOracleTable;

public class StringBufUtil {
	//保持原有格式
	public static boolean KEEPFORMAT = true;
	//不保持原有格式-即回车换行符去掉
	public static boolean NO_KEEPFORMAT = false;
	/**
	 * 从数据库中取出文档流（如：存储过程流）
	 * @param pName  存储过程名
	 */
	public static StringBuffer getSbFromDatabase(String pName) {
		StringBuffer sb = new StringBuffer();
		QueryOracleTable query = new QueryOracleTable();
		try {
			sb = query.getProcedureText(pName, null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sb;
	}
	/**
	 * 从磁盘中取出文档流
	 * @param path	文件路径（这里指的是完整的存在的文件的路径）
	 * @return
	 */
	public static StringBuffer getSbFromDisk(String path,boolean keepFormat) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(path)));
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while((line = br.readLine())!=null) {
				sb.append(line);
				if(keepFormat) {
					sb.append("\r\n");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sb;
	}
}
