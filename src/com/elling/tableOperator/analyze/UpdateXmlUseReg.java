package com.elling.tableOperator.analyze;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elling.tableOperator.utils.StringUtil;

/**
 * �������滻
 * ���ܣ�ƥ��������ַ����飬�滻����Ӧ������ַ���
 * ����Ϊʲôʹ������滻����Ϊ������������Щinsert/update/merge/delete ��Щ��ȫ������Ҫ�滻���ģ�������һЩ���������־��������־��ȾͲ���Ҫ�滻��
 * Ȼ����Щ�洢���̡��������ǲ����������Ҳ����Ҫ���滻�������ۺϿ��ǳ���������ǰ�ķ����Ƚ���Ҫ���滻�Ļ�����ֱ�ӽ���Ҫ�滻��������г����ٴ���Ϳ�����
 * 1������ʹ��ǰ�����GetTableFromSbȡ������������Щ���洢���̡���������Ȼ��Ҫ����һ����Щ��Ҫ�滻����Щ����Ҫ�滻
 * 2��Ȼ����Ҫ�滻���洢���̡�function�г������ŵ�һ���ַ�������arr��
 * 3��ʹ��StringUtil.joinReg(arr),Ȼ��ʹ��������ʽ(aaaa|bbbb|cccc)���ַ�ʽ��ѯȻ���滻������Ҫ�滻���������ⲿ����һ��map��
 * 4��ʹ���滻��map.get(group(1))
 * 
 * @author cyy
 * @date  20191010
 */
public class UpdateXmlUseReg {
	
	/**
	 * ��ȡ���ݹ�������Ҫ�滻���ַ������飬Ȼ���滻��
	 * 
	 * ��ʽ���£�
	 *  1)���磺(TABLE1|SYS_USER|SYS_MENU|SP_GET_REPL|SF_VIEW)
	 * 	2)����(XXX|XXXX)  -->ȡ��group(1)
	 * @param sb		��Ҫ������ʽ��ѯ�Ĵ�
	 * @param arr		��Ҫ�滻������
	 * @param changeMap	��Ҫ�滻�ɵ�map
	 * @return
	 */
	public StringBuffer operateXml(StringBuffer sb,String[] arr,HashMap changeMap) {
		StringBuffer sbb = new StringBuffer();
		
		String pattern = StringUtil.joinReg(arr);
		Pattern p = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sb);
		int start = 0;
		int end = 0;
		int count = 0;//�ܹ������˶��ٸ�
		while(m.find()) {
			count++;
			String tableName = "";
			String chuan = m.group(0);
			tableName = m.group(1);
			System.out.println("��ѯ���Ĵ���" + chuan+",������" + tableName);
			
			start = m.start(1);
			end = m.end(1);
			System.out.println("��ʼλ�ã�"+start+",����λ�ã�"+end+",�Ӵ���"+sb.substring(start, end));
			
			m.appendReplacement(sbb, changeMap.get(tableName.toUpperCase())+"");
		}
		m.appendTail(sbb);
		System.out.println("�ܹ��滻�˶��ٸ���"+count+",ƥ��Ĵ���"+pattern);
		
		return sbb;
	}
	
	
	
}
