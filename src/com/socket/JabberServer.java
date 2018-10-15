package com.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *************************************************************** 
 * 项目名称：JavaThread 程序名称：JabberServer 日期：2012-8-23 上午11:36:12 作者： 模块： 描述： 备注：
 * ------------------------------------------------------------ 修改历史 序号 日期 修改人
 * 修改原因
 * 
 * 修改备注：
 * 
 * @version
 *************************************************************** 
 */
public class JabberServer {

	public static int PORT = 8080;

	public static void main(String[] agrs) {
		ServerSocket serverSocket = null;
		try {
			// 设定服务端的端口号
			serverSocket = new ServerSocket(PORT);
			//JabberServer.serviceSingle(serverSocket);//单线程的ServerSocket例子测试
			JabberServer.serviceMultiple(serverSocket);//多线程的ServerSocket例子测试
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("关闭Socket服务器.....");
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (Exception e2) {

			}
		}
	}

	//单线程的ServerSocket例子
	public static void serviceSingle(ServerSocket serverSocket) {
		boolean end = false;
		while (true) {
			if (end) 
				break;
			
			Socket socket = null;
			BufferedReader br = null;
			PrintWriter pw = null;
			try {
				System.out.println("Socket服务端监听中..:" + serverSocket);
				// 等待请求,此方法会一直阻塞,直到获得请求才往下走
				socket = serverSocket.accept(); // 从连接请求队列中取出一个连接
				System.out.println("主线程获取客户端请求连接 " + socket.getInetAddress() + ":" + socket.getPort());
				// 用于接收客户端发来的请求
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// 用于发送信息,可以不需要装饰这么多io流，使用缓冲流时发送数据要注意调用.flush()方法
				pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				
				int i = 1;
				while (true) {
					String str = br.readLine();
					if (str.equals("END")) {
						end = true;
						break;
					}
					System.out.println("打印Socket客户端发送的信息:" + str);
					Thread.sleep(1000);
					pw.println("服务端输出的信息" + i++);
					pw.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (socket != null)
						socket.close();
					if (br != null)
						br.close();
					if (pw != null)
						pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * *多线程的ServerSocket例子
	 * 
	 * 多线程的好处不用多说，而且大多数的场景都是多线程的，无论是我们的即时类游戏还是IM，多线程的需求都是必须的。下面说说实现方式：
	 * 主线程会循环执行ServerSocket.accept()；
	 * 当拿到客户端连接请求的时候，就会将Socket对象传递给多线程，让多线程去执行具体的操作（这样就避免了多个请求排队执行）；
	 * 注意：在创建和启动线程的过程中，请求是排队执行的。
	 * 实现多线程的方法要么继承Thread类，要么实现Runnable接口。当然也可以使用线程池，但实现的本质都是差不多的
	 */
	public static void serviceMultiple(ServerSocket serverSocket) {
		int i = 1;
		while (true) {
			Socket socket = null;
			try {
				System.out.println("循环次数" + (i++) + "，Socket服务端监听中..:" + serverSocket);
				// 等待请求,此方法会一直阻塞,直到获得请求才往下走
				socket = serverSocket.accept(); // 从连接请求队列中取出一个连接
				System.out.println("主线程获取客户端请求连接 " + socket.getInetAddress() + ":" + socket.getPort());
				Thread workThread = new Thread(new Handler(socket)); // 创建线程
				workThread.start(); // 启动线程
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}