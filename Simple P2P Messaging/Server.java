/**********************************************************
 * EECS3214 Winter 2017
 * Assignment 2
 * Student Name:  Yang Wang
 * Student cse account:  infi999	
 * Student ID number:  213894167 
 **********************************************************
 *
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The purpose of this class is to build the multi - threaded server which
 * receives message from client and send it back to client after handle it. This
 * server can accept multiple connection simultaneously.
 * 
 * @author Yang Wang
 * 
 */

public class Server {
	static ArrayList<Socket> list = new ArrayList<Socket>();

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int port = 24167;

		// create server socket for listening state
		ServerSocket socket = new ServerSocket(port);
		System.out.println("Server is started");
		System.out.println("Waiting for connection");

		while (true) {
			// accept connection request from clients
			Socket connection = socket.accept();
			ServerRunnable S = new ServerRunnable(connection);
			// start a service thread to service client
			new Thread(S).start();
			System.out.println("connection set up");
		}

	}
}

/**
 * The purpose of this class is when socket accept request from client, divide
 * each connection to here to deal with
 * 
 * @author Yang Wang
 *
 */
class ServerRunnable implements Runnable {

	Socket connection;
	String message;

	/**
	 * constructor
	 * 
	 * @param connection
	 */
	public ServerRunnable(Socket connection) {
		this.connection = connection;

	}

	/**
	 * the body of the thread
	 */
	public void run() {

		try {
			// get output streams
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			// get input streams
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			// receive the massages from clients and handle it
			while ((message = in.readLine()) != null) {

				if (message.equalsIgnoreCase("JOIN")) {

					out.println("You have joined the player list");

					if (!Server.list.contains(connection)) {

						Server.list.add(connection);
					}
				} else if (message.equalsIgnoreCase("LEAVE")) {

					out.println("You have left the player list");
					out.println("Please input QUIT to close the socket!");
					System.out.println("User" + connection.getPort() + " leave the player list");

					if (Server.list.contains(connection)) {

						Server.list.remove(connection);
					}
				} else if (message.equalsIgnoreCase("CHAT")) {

					out.println("If you want invite peer to talk, type SENDIVT:IP:PORT");
					
				} else if (message.equalsIgnoreCase("LIST")) {

					if (Server.list.isEmpty()) {

						System.out.println("player list is empty");
						out.println("player list is empty");

					} else {

						StringBuilder sb = new StringBuilder();
						for (Socket user : Server.list) {

							sb.append(user.getInetAddress().getHostAddress() + ":" + user.getPort() + "\t");
						}

						out.println(sb);

					}

				} else if (message.equalsIgnoreCase("QUIT")) {

					if (Server.list.contains(connection)) {

						Server.list.remove(connection);
						out.println("You quit");
						System.out.println("User" + connection.getPort() + " quit");
						break;

					} else {
						out.println("You quit");
						System.out.println("User" + connection.getPort() + " quit");
						break;
					}
					// type SENDINV:IP:PORT to send invitation to destination client
				} else if (message.startsWith("SENDIVT")) {

					message = message.substring(8);
					System.out.println(message);
					String[] str = message.split(":");
					String client1_ip = str[0];
					int client1_port = Integer.parseInt(str[1]);
					String client2_ip = this.connection.getInetAddress().toString().replaceAll("/", "");
					int client2_port = this.connection.getPort();
					System.out.println("invitation send");

					for (int i = 0; i < Server.list.size(); i++) {
						Socket socket = Server.list.get(i);
						if (socket.getInetAddress().toString().endsWith(client1_ip) && socket.getPort() == client1_port) {
							try {
								PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
								output.println("SENDIVT TO" + "," + client2_ip + "," + client2_port);
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}
						}
					}
					out.println("invitation accepted");

				}

			}

			// clean up, close stream and socket
			out.close();
			in.close();
			connection.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

}
