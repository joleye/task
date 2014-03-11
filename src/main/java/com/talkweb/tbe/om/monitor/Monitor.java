package com.talkweb.tbe.om.monitor;

import java.io.IOException;


public class Monitor {
	
	public static void main(String[] args) throws IOException
	{
		//启动监听程序
		ServerMonitor server = new ServerMonitor("localhost",5676,"c5a88807fc06a93b");
		server.start();
	}
}
