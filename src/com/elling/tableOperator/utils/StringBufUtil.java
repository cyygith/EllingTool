package com.elling.tableOperator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.elling.database.QueryOracleTable;

public class StringBufUtil {
	//����ԭ�и�ʽ
	public static boolean KEEPFORMAT = true;
	//������ԭ�и�ʽ-���س����з�ȥ��
	public static boolean NO_KEEPFORMAT = false;
	/**
	 * �����ݿ���ȡ���ĵ������磺�洢��������
	 * @param pName  �洢������
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
	 * �Ӵ�����ȡ���ĵ���
	 * @param path	�ļ�·��������ָ���������Ĵ��ڵ��ļ���·����
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
