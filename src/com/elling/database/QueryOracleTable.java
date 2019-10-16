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
	 * 获取连接
	 * @return
	 */
	public Connection createConn() {
		Connection conn = null;
		try {
			Class.forName(driver);
			System.out.println("成功加载OracleSQL驱动");
			
			conn = DriverManager.getConnection(url,user,password);
			System.out.println("成功连接到数据库");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 根据sql语句，获取数据库中的数据，这里返回的为List类型的Map数据结构
	 * 其他复杂的结构是
	 * @param sql
	 * @param arr
	 * @return
	 */
	public List<Map<String,Object>> getBySql(String sql,String[] arr){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Connection conn = createConn();//这里可以优化，不需要每次都从新建，至于怎么设计之后再看
		PreparedStatement pstat = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		try {
			pstat = conn.prepareStatement(sql);
			System.out.println("查询的sql为："+sql);
			
			//循环赋值参数，位置需要严格控制
			if(arr!=null&&arr.length>0) {
				for(int i=0,len=arr.length;i<len;i++) {
					pstat.setNString((i+1), arr[i]);
				}
			}
			
			rs = pstat.executeQuery();
			md = rs.getMetaData();//创建数据对象
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
	 * 根据存储过程名获取到对应的存储过程Text
	 * 查询的SQL语句如下：SELECT TEXT FROM USER_SOURCE WHERE NAME = UPPER(?) AND TYPE = ? ORDER BY LINE;
	 * @param pName:存储过程名，如：sp_get_repl
	 * @param type：类型（存储过程或者函数）：PROCEDURE  FUNCTION
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
			System.out.println("查询的sql语句为："+sql);
			
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
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		QueryOracleTable query = new QueryOracleTable();
		
		//1、根据传入的表名列表，查询表的名称及注释
		String sql = "SELECT T.TABLE_NAME,T.COMMENTS FROM USER_TAB_COMMENTS T WHERE T.TABLE_NAME IN (?)";
		String[] arr = new String[] {"'SYS_USER','SYS_DICT'"};
		List<Map<String,Object>> list = query.getBySql(sql, arr);
		for(int i=0,len=list.size();i<len;i++) {
			Map map = list.get(i);
			System.out.println(map.get("TABLE_NAME")+":"+map.get("COMMENTS"));
		}
		
		//2.根据存储过程名查询存储过程的Text内容
		StringBuffer sb = query.getProcedureText("SP_BEFORE_SS_FORBID", "PROCEDURE");
		System.out.println("存储过程view："+sb.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
}
