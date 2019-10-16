package com.elling.tableOperator.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StringUtil {
	/**
	 * 根据指定字符分隔，组合成需要的串
	 * 如：["sys_user","sys_dict"] -> 'sys_user','sys_dict'
	 * @param arr
	 * @return
	 */
	public static StringBuffer join(String[] arr) {
		StringBuffer sb = new StringBuffer();
		for(int i=0,len=arr.length;i<len;i++) {
			sb.append("'").append(arr[i]).append("'");
			if(i!=len-1) {
				sb.append(",");
			}
		}
		return sb;
	}
	/**
	 * 根据指定字符分隔，组合成需要的串
	 * 如：["sys_user","sys_dict"] -> 'sys_user','sys_dict'
	 * @param arr
	 * @return
	 */
	public static StringBuffer join(List list) {
		StringBuffer sb = new StringBuffer();
		for(int i=0,len=list.size();i<len;i++) {
			sb.append("'").append(list.get(i)).append("'");
			if(i!=len-1) {
				sb.append(",");
			}
		}
		return sb;
	}
	/**
	 * 根据指定字符分隔，组合成需要的串
	 * 如：["sys_user","sys_dict"] -> 'sys_user','sys_dict'
	 * @param arr
	 * @return
	 */
	public static StringBuffer join(Map<String,Object> map) {
		StringBuffer sb = new StringBuffer();
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			sb.append("'").append(entry.getValue().toString().toUpperCase()).append("'");
			if(it.hasNext()) {
				sb.append(",");
			}
		}
		return sb;
	}
	
	/**
	 * 拼写成正则表达式串，如：new String[]{"aaa","bbb"} --> aaa|bbb
	 * @param arr
	 * @return
	 */
	public static String joinReg(String[] arr) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(int i=0,len=arr.length;i<len;i++) {
			sb.append(arr[i]);
			if(i!=len-1) {
				sb.append("|");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		String[] arr = new String[] {"sys_user","sys_dict"};
		System.out.println(StringUtil.joinReg(arr));
	}
	
	
}
