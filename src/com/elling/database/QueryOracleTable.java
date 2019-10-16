package com.elling.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryOracleTable {
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@127.0.0.1:1521:testdb";
	private String user = "test";
	private String password = "test";
	private static Connection conn = null;
	
	public Connection getConn() {
		if(conn == null) {
			createConn();
		}
		return conn;
	}
	/**
	 * ��ȡ����
	 * @return
	 */
	public Connection createConn() {
		Connection conn = null;
		try {
			Class.forName(driver);
			System.out.println("�ɹ�����OracleSQL����");
			
			conn = DriverManager.getConnection(url,user,password);
			System.out.println("�ɹ����ӵ����ݿ�");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * ����sql��䣬��ȡ���ݿ��е����ݣ����ﷵ�ص�ΪList���͵�Map���ݽṹ
	 * �������ӵĽṹ��
	 * @param sql
	 * @param arr
	 * @return
	 */
	public List<Map<String,Object>> getBySql(String sql,String[] arr){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Connection conn = createConn();//��������Ż�������Ҫÿ�ζ����½���������ô���֮���ٿ�
		PreparedStatement pstat = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		try {
			pstat = conn.prepareStatement(sql);
			System.out.println("��ѯ��sqlΪ��"+sql);
			
			//ѭ����ֵ������λ����Ҫ�ϸ����
			if(arr!=null&&arr.length>0) {
				for(int i=0,len=arr.length;i<len;i++) {
					pstat.setNString((i+1), arr[i]);
				}
			}
			
			rs = pstat.executeQuery();
			md = rs.getMetaData();//�������ݶ���
			int columnCount = md.getColumnCount();
			while(rs.next()) {
				Map<String,Object> rowData = new HashMap<String,Object>();
				for(int i=1;i<=columnCount;i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstat != null) pstat.close();
				if(conn != null) conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * ���ݴ洢��������ȡ����Ӧ�Ĵ洢����Text
	 * ��ѯ��SQL������£�SELECT TEXT FROM USER_SOURCE WHERE NAME = UPPER(?) AND TYPE = ? ORDER BY LINE;
	 * @param pName:�洢���������磺sp_get_repl
	 * @param type�����ͣ��洢���̻��ߺ�������PROCEDURE  FUNCTION
	 * @return
	 */
	public StringBuffer getProcedureText(String pName,String type) {
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstat= null;
		ResultSet rs = null;
		try {
			conn = createConn();
			String sql = "SELECT TEXT FROM USER_SOURCE WHERE NAME = UPPER(?) ORDER BY LINE";
			pstat = conn.prepareStatement(sql);
			pstat.setString(1, pName);
			System.out.println("��ѯ��sql���Ϊ��"+sql);
			
			rs = pstat.executeQuery();
			int i=0;
			while(rs.next()) {
				sb.append(rs.getString(1));
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstat != null) pstat.close();
				if(conn != null) conn.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return sb;
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		QueryOracleTable query = new QueryOracleTable();
		
		//1�����ݴ���ı����б���ѯ������Ƽ�ע��
		String sql = "SELECT T.TABLE_NAME,T.COMMENTS FROM USER_TAB_COMMENTS T WHERE T.TABLE_NAME IN (?)";
		String[] arr = new String[] {"'SYS_USER','SYS_DICT'"};
		List<Map<String,Object>> list = query.getBySql(sql, arr);
		for(int i=0,len=list.size();i<len;i++) {
			Map map = list.get(i);
			System.out.println(map.get("TABLE_NAME")+":"+map.get("COMMENTS"));
		}
		
		//2.���ݴ洢��������ѯ�洢���̵�Text����
		StringBuffer sb = query.getProcedureText("SP_BEFORE_SS_FORBID", "PROCEDURE");
		System.out.println("�洢����view��"+sb.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
}
