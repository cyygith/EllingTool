package com.elling.tableOperator.testnew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elling.tableOperator.analyze.UpdateXmlUseReg;
import com.elling.tableOperator.utils.FileUtil;
import com.elling.tableOperator.utils.StringBufUtil;

/**
 * ���Ը����滻��Ӧ�ı�
 * @author Administrator
 *
 */
public class UpdateFileTest {
	public static void main(String[] args) {
		String sourcePath = "E:\\WORK\\workspace\\EllingTool\\src";//��ȡ��λ�ã������ڴ������ݶ�ȡ��
		String savePath = "C:\\Users\\Administrator\\Desktop\\operate";//���������µ��ļ�λ��
		String suffix = "_DZG";
		
		//1��������������Ҫ�滻�ı������������ѯ��ʱ��Ҫ�л��У��滻��ʱ�򱣴�ԭ�л��и�ʽ,�����Ϳ��ԱȽ������Ľ����ʽ����
		String[] forReplaceArr = new String[] {"SYS_USER","SYS_DICT"};//��Ҫ�滻���ֶ�
		String[] replaceArr = new String[] {"SYS_USER_DZG","SYS_DICT_N_DZG"};//�滻�ɵ��ֶ�
		
		//2������Ҫ�滻���ֶ�����ŵ�һ��Map��
		HashMap replaceMap = new HashMap();
		for(int i=0,len = replaceArr.length;i<len;i++) {
			replaceMap.put(forReplaceArr[i], replaceArr[i]);
		}
		
		//3��ȡ����Ҫ�滻���ļ�����
		FileUtil fileUtil = new FileUtil();
		fileUtil.find(sourcePath, 1, "sql|xml");
		ArrayList<Map> fileList = fileUtil.getListMap();
		System.out.println("��Ҫ�滻���ļ��ж��ٸ���"+fileList.size());
		
		//4����ʼ�滻
		UpdateXmlUseReg updateXml = new UpdateXmlUseReg();
		StringBuffer sb = null;
		for(int i=0,len=fileList.size();i<len;i++) {
			String path = "";
			String name = "";
			Map pathMap = fileList.get(i);
			Iterator it = pathMap.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				path = entry.getValue() + "";
				name = entry.getKey() + "";
			}
			sb = StringBufUtil.getSbFromDisk(path,StringBufUtil.KEEPFORMAT);
			StringBuffer sbb = updateXml.operateXml(sb, forReplaceArr, replaceMap);
			FileUtil.saveFile(savePath, name+suffix, sbb);//�����걣�浽ָ���ļ���
		}
		
	}
}
