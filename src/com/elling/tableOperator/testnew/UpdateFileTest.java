package com.elling.tableOperator.testnew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elling.tableOperator.analyze.UpdateXmlUseReg;
import com.elling.tableOperator.utils.FileUtil;
import com.elling.tableOperator.utils.StringBufUtil;

/**
 * 测试更新替换对应的表
 * @author Administrator
 *
 */
public class UpdateFileTest {
	public static void main(String[] args) {
		String sourcePath = "E:\\WORK\\workspace\\EllingTool\\src";//读取的位置（适用于磁盘内容读取）
		String savePath = "C:\\Users\\Administrator\\Desktop\\operate";//保存最后更新的文件位置
		String suffix = "_DZG";
		
		//1、分析出来的需要替换的表整理出来，查询的时候不要有换行，替换的时候保存原有换行格式,这样就可以比较完整的解决格式问题
		String[] forReplaceArr = new String[] {"SYS_USER","SYS_DICT"};//需要替换的字段
		String[] replaceArr = new String[] {"SYS_USER_DZG","SYS_DICT_N_DZG"};//替换成的字段
		
		//2、把需要替换的字段数组放到一个Map中
		HashMap replaceMap = new HashMap();
		for(int i=0,len = replaceArr.length;i<len;i++) {
			replaceMap.put(forReplaceArr[i], replaceArr[i]);
		}
		
		//3、取出需要替换的文件（）
		FileUtil fileUtil = new FileUtil();
		fileUtil.find(sourcePath, 1, "sql|xml");
		ArrayList<Map> fileList = fileUtil.getListMap();
		System.out.println("需要替换的文件有多少个："+fileList.size());
		
		//4、开始替换
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
			FileUtil.saveFile(savePath, name+suffix, sbb);//更新完保存到指定文件夹
		}
		
	}
}
