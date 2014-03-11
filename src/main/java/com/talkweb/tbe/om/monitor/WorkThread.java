package com.talkweb.tbe.om.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WorkThread {

	private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String sign;
    private int tid;
    
	public WorkThread(Socket s,String sign,int tid) throws UnsupportedEncodingException, IOException {
        this.socket = s;  
        this.sign = sign;
        this.tid = tid;
        try{
        	//设置每个请求最多等待时间
	        this.socket.setSoTimeout(100000);
	        
	        //构造该会话中的输入输出流
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GB2312"));
	        out = new PrintWriter(socket.getOutputStream(), true);
	        
	        run();
        }catch (Exception ex) {
        	//关闭消息
        	System.out.println(ex.getMessage());
        	try {
        		in.close();
        		out.close();
				socket.close();
				System.out.println("连接强制关闭 #" + tid);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	//执行请求处理
    public void run() throws IOException {
		//通过输入流接收客户端信息
    	Map<String,String> map = new HashMap<String,String>();
    	int receivelength = 0;
        while (true) {
            String line = in.readLine();
            if (line == null || "".equals(line.trim()) || receivelength>2000) { // 是否终止会话
                break;
            }
            receivelength += line.length();
            
            System.out.println(receivelength+"-<"+line+">");
            //第一行处理验证信息
            if(line.indexOf(":")>-1){
            	int index = line.indexOf(":");
            	String key = line.substring(0,index);
            	String val = line.substring(index+1);
            	map.put(key, val);
            }
        }
        
        //#消息验证
        if(map.containsKey("sign") && map.get("sign").equals(this.sign))
        {
        	if(map.containsKey("action"))
        	{
        		String action = map.get("action");
        		
        		try {
					out.write(new MonitorAction(action).getValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        	
	        	out.flush();
        	}
        }
        else
        {
        	out.write("check sign error");
        	out.flush();
        }
        //标识结束
        out.print("\r\n");
        out.flush();
        
        in.close();
        out.close();
        socket.close();
        System.out.println("通信结束，连接已关闭 #" + tid);
    }

}
