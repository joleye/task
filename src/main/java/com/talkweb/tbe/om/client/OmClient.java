package com.talkweb.tbe.om.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.google.gson.Gson;

/**
 * 远程连接客户端，用于和tbe-om-task交互
 * @author joleye
 */
public class OmClient {

	private StringBuilder sb;
	private static Gson gson = new Gson();//谷歌json转换工具
	private String method;
	
	/**
	 * 初始化Om监控器客户端
	 * @param host 主机名或者IP地址
	 * @param method 监控项目 cpu／memory／io
	 * @param thread 程序ID
	 * @throws IOException
	 */
	public OmClient(String host,String method,String thread) throws IOException
	{
		int port = 5676;
		String sign = "c5a88807fc06a93b";
		this.method = method;
		
		InetAddress addr = InetAddress.getByName(host);  
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(addr,port),5000);
		
        //向服务端程序发送数据
        OutputStream ops  = socket.getOutputStream();        
        OutputStreamWriter opsw = new OutputStreamWriter(ops);
        BufferedWriter bw = new BufferedWriter(opsw);
        
        bw.write("sign:"+sign+"\n");
        bw.write("action:{'method':'"+method+"','thread':'"+thread+"'}\n");
        bw.write("\n");
        bw.flush();
        
        // 从服务端程序接收数据
        InputStream ips = socket.getInputStream();
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);

        String s = null;
        sb = new StringBuilder();
        while(true)
		{
		    s = br.readLine();
		    if (s == null || "".equals(s.trim())) { //是否终止会话
		    	break;
	        }
		    sb.append(s);
	 	}

        bw.close();
        opsw.close();
        ops.close();
        
        socket.close();
	}
	
	/**
	 * 读取返回接结果数据
	 * @return
	 */
	public float toValue()
	{
		Map map = (Map)gson.fromJson(sb.toString(), Map.class);
		//System.out.println(map.get(method));
		
		return Float.valueOf(map.get(method).toString());
	}
}
