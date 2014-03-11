package com.talkweb.tbe.om.monitor;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

/**
 * 根据请求参数处理，读取出系统信息
 * @author joleye
 */
public class MonitorAction {

	private String key,value;
	private static Gson gson = new Gson();//谷歌json转换工具
	
	public MonitorAction(String data) throws IOException, InterruptedException
	{
		Map map = (Map)gson.fromJson(data, Map.class);
		
		//Map<String, Object> map = UtilJson.toMap(data);
		
		//这里需要处理###################
		String pathfile = "/home/joleye/monitor.sh";
		
		String method =  map.get("method").toString();
		String thread = map.get("thread").toString();
		
		CommandResult result = CommandHelper.exec("bash "+pathfile+" "+method+" "+thread);
		
		key = method;
		value = result.getOutput();
	}

	/**
	 * 返回数据以json格式返回
	 * @return
	 */
	public String getValue() {
		return "{"+key+":'"+value+"'}";
	}
	
	
}
