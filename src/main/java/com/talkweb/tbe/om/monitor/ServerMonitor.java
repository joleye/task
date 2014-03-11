package com.talkweb.tbe.om.monitor;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 监控程序入口
 * @author joleye
 */
public class ServerMonitor {
	private final int portnum;
	private final String sign;
	private final InetAddress bindip;
	
	public ServerMonitor(String ipstr,int portnum){
		this(ipstr,portnum,"");
	}
	
	/**
	 * 初始化配置信息
	 * @param ipstr
	 * @param portnum
	 * @param sign
	 */
	public ServerMonitor(String ipstr,int portnum,String sign){
		try{
			 this.portnum = portnum;
	         this.sign = sign;
	         this.bindip = InetAddress.getByName(ipstr);
		}catch (Exception ex) {
	        throw new RuntimeException(ex);
	    }
	}
	
	/**
	 * 开始监听
	 */
	public void start() {
		//统计连接数
        int connum = 0;
        try {
        	@SuppressWarnings("resource")
			ServerSocket srvsock = new ServerSocket(portnum, 0,bindip);
        	System.out.println("Listening on " + bindip.getHostAddress() + ":" + portnum);
        	 
            while (true) {
                // 监听一端口，等待客户接入            	
                Socket socket = srvsock.accept();
                
                connum++;
                InetAddress ipaddr = socket.getInetAddress();
                System.out.println("Connection on #" + connum + " from: " + ipaddr.getHostAddress());
                
                // 将会话交给新线程处理
                new WorkThread(socket,sign,connum);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
	}
}
