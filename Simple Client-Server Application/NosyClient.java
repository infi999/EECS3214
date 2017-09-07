import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NosyClient
{

	public static void main(String[] args) throws Exception
	{
		// Get command line argument.
		if (args.length != 2) 
		{
			System.out.println("Required arguments: host and port");
			return;
		}
		int port = Integer.parseInt(args[1]);
		
		//connect to server
		InetAddress IP = InetAddress.getByName(args[0]);
		Socket socket = new Socket(IP, port);
		//buffer for message from server
		BufferedReader inFromServer = new BufferedReader (new InputStreamReader(socket.getInputStream()));
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		//check if server replied
		boolean reply = false;
		//read user input
		Scanner input = new Scanner(System.in);
		String command = "";
		
		while (true)
		{
			while(inFromServer.ready())
			{
				System.out.println(inFromServer.readLine());
				reply = true;
			}
			if(reply == true)
			{
				command = input.nextLine();
				output.writeBytes(command+"\n");
				reply = false;
			}
			//exit the program
			if(command.equalsIgnoreCase("exit"))
			{
				//release resource
				inFromServer.close();
				output.close();
				socket.close();
				System.exit(0);
			}

		}
		
	}

}