package com.elling.tableOperator.analyze;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elling.tableOperator.utils.StringUtil;

/**
 * 正则表达替换
 * 功能：匹配输入的字符数组，替换掉对应数组的字符串
 * 至于为什么使用这个替换是因为经过分析，那些insert/update/merge/delete 这些表全部都是要替换掉的，但是有一些比如错误日志表、跟踪日志表等就不需要替换，
 * 然后有些存储过程、方法都是操作公共类的也不需要提替换，所以综合考虑出来，这里前文分析比较重要，替换的话就是直接将需要替换掉表表罗列出来再处理就可以了
 * 1）可以使用前面的类GetTableFromSb取出操作都有哪些表、存储过程、函数，当然需要分析一下拿些需要替换，哪些不需要替换
 * 2）然后将需要替换表、存储过程、function列出来，放到一个字符数组中arr，
 * 3）使用StringUtil.joinReg(arr),然后使用正则表达式(aaaa|bbbb|cccc)这种方式查询然后替换成你需要替换（这里由外部传入一个map）
 * 4）使用替换掉map.get(group(1))
 * 
 * @author cyy
 * @date  20191010
 */
public class UpdateXmlUseReg {
	
	/**
	 * 获取传递过来的需要替换的字符串数组，然后替换掉
	 * 
	 * 格式如下：
	 *  1)例如：(TABLE1|SYS_USER|SYS_MENU|SP_GET_REPL|SF_VIEW)
	 * 	2)正则：(XXX|XXXX)  -->取出group(1)
	 * @param sb		需要正则表达式查询的串
	 * @param arr		需要替换的数组
	 * @param changeMap	需要替换成的map
	 * @return
	 */
	public StringBuffer operateXml(StringBuffer sb,String[] arr,HashMap changeMap) {
		StringBuffer sbb = new StringBuffer();
		
		String pattern = StringUtil.joinReg(arr);
		Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sb);
		int start = 0;
		int end = 0;
		int count = 0;//总共更新了多少个
		while(m.find()) {
			count++;
			String tableName = "";
			String chuan = m.group(0);
			tableName = m.group(1);
			System.out.println("查询到的串：" + chuan+",表名：" + tableName);
			
			start = m.start(1);
			end = m.end(1);
			System.out.println("开始位置："+start+",结束位置："+end+",子串："+sb.substring(start, end));
			
			m.appendReplacement(sbb, changeMap.get(tableName.toUpperCase())+"");
		}
		m.appendTail(sbb);
		System.out.println("总共替换了多少个："+count+",匹配的串："+pattern);
		
		return sbb;
	}
	
	
	
}
