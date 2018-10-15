package com.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

class Handler implements Runnable {
	private Socket socket;

	public Handler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedReader br = null;
		PrintWriter pw = null;
		try {
			// 用于接收客户端发来的请求
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 用于发送信息,可以不需要装饰这么多io流，使用缓冲流时发送数据要注意调用.flush()方法
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			int i = 1;
			while (true) {
				String str = br.readLine();
				if (str.equals("END")) {
					break;
				}
				System.out.println("打印Socket客户端发送的信息:" + str);
				Thread.sleep(1000);
				pw.println("服务端输出的信息" + i++);
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("关闭连接:" + socket.getInetAddress() + ":" + socket.getPort());
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