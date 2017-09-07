/**********************************************************
 * EECS3214 Winter 2017
 * Assignment 2
 * Student Name:  Yang Wang
 * Student cse account:  infi999	
 * Student ID number:  213894167 
 **********************************************************
 *
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The purpose of this class is to build the client part which can connect to server and connect to other client and exchange message
 * 
 * @author Yang Wang
 * 
 */


public class Client extends Thread {


	private PrintWriter out;
	
	private Socket socket;

	private BufferedReader in;

	private Socket serverSocket;

	
	private BlockingQueue<Socket> socketQueue = new LinkedBlockingQueue<Socket>();

	private int client_Port;
	
 /**
  * 
  * @param ip
  * @param port_str
  */
	public Client(String ip, String port_str) {

		int port = Integer.parseInt(port_str);

		try {
			this.serverSocket = new Socket(ip, port);
			System.out.println( "Welcome user " + serverSocket.getLocalPort());
			System.out.println("MENU: LIST JOIN LEAVE CHAT");

			socket = serverSocket;
			out = new PrintWriter(this.socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			
			this.start();
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				String message = input.readLine();
				exchange(message);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 *  build up a listener and read and write information through different situations
	 */
	@Override
	public void run() {
		String message = "";
		while (true) {
			try {
				message = in.readLine();
               // second situation of client (client A accept other clients invitation
				if (message.startsWith("invitation accepted")) {
					// Create a new socket server
					// set up real port number for client ( random + 50)
					client_Port = serverSocket.getLocalPort() + 50;

					ServerSocket chatServerSocket = new ServerSocket(client_Port);
					System.out.println("Waiting destination client's answer");
					new ClientAsServerThread(chatServerSocket, socketQueue).start();

					exchange(message);

					socket = socketQueue.take();
					out = new PrintWriter(this.socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
               // third situation of client (client A send invitation to other clients)
				} else if (message.startsWith("SENDIVT")) {

					String[] arr = message.split(",");
					String ip = arr[1];
					int port = Integer.parseInt(arr[2]);

					System.out.println("connecting:" + ip + ":" + (port + 50));
					socket = new Socket(ip, port + 50);
					System.out.println("connected:" + ip + ":" + (port + 50));
			        out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
              
				} else if (message.equals("quit")) {
					System.out.println("The other people quit the chat, MENU: LIST JOIN LEAVE CHAT");
					Socket tmp = this.socket;
					socket = this.serverSocket;

					out = new PrintWriter(this.socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					tmp.close();

				} else {
					System.out.println(message);
				}
			} catch (Exception e) {
				System.out.println("Oops, no connection");
				message = "";
				continue;
			}
		}
	}
/**
 * exchange message
 * @param message
 */
	public void exchange(String message) {
		try {
			out.println(message);
			if (message.equals("QUIT")) {
				this.serverSocket.close();
				System.exit(0);
			}
			// QUIT p2p
			if (message.equals("quit")) {
				Socket tmp = socket;
				tmp.close();
				socket = this.serverSocket;
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("The input format is : java Client ip port ");
			return;
		}try {
			new Client(args[0], args[1]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

/**
 * server part of client
 * @author yangwang
 *
 */
class ClientAsServerThread extends Thread {

	private BlockingQueue<Socket> socketQueue;
	private ServerSocket serverSocket;

	public ClientAsServerThread(ServerSocket serverSocket, BlockingQueue<Socket> socketQueue) {
		this.serverSocket = serverSocket;
		this.socketQueue = socketQueue;

	}

	@Override
	public void run() {
		try {
			Socket clientSocket = serverSocket.accept();
			System.out.println(clientSocket.toString() + " connection set up");

			this.socketQueue.add(clientSocket);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
