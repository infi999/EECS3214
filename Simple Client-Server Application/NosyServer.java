import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NosyServer
{

	public static void main(String[] args) throws Exception
	{
		// Get command line argument.
		if (args.length != 1)
		{
			System.out.println("Required arguments: port");
			return;
		}

		int port = Integer.parseInt(args[0]);
		String menu = "= = = = = = = = = = = = = = = = Menu = = = = = = = = = = = = = = = =\n"
				+ "date -- print the date and time of server's system\n"
				+ "timezone -- print the time zone of server's system\n"
				+ "OSname -- print the name of server's operating system (OS)\n"
				+ "OSversion -- print the of version number of server's OS\n"
				+ "user -- print the name of the user logged onto (i.e. running) the server\n"
				+ "exit -- exit the program\n"
				+ "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =\n"
				+ "Enter command >\n";

		// create server socket for listening state
		ServerSocket socket = new ServerSocket(port);

		while (true)
		{
			Socket connection = socket.accept();
			// once a request is received send out the menu
			DataOutputStream output = new DataOutputStream(
					connection.getOutputStream());
			output.writeBytes(menu);
			// read the command from user
			BufferedReader input = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String command = input.readLine();
//			System.out.println(command);
			while (!command.equalsIgnoreCase("exit"))
			{
//				System.out.println(command);
				// send message according to the command received
				if (command.equalsIgnoreCase("date"))
				{
					output.writeBytes("Server Date: "
							+ new SimpleDateFormat("EEE MMM dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n");
					output.writeBytes("Enter command >\n");
				}
				else if (command.equalsIgnoreCase("timezone"))
				{
					output.writeBytes("Server Timezone : "	+ TimeZone.getDefault().getDisplayName() + "\n");
					output.writeBytes("Enter command >\n");
				}
				else if (command.equalsIgnoreCase("osname"))
				{
					output.writeBytes("Server OS name: " + System.getProperty("os.name") + "\n");
					output.writeBytes("Enter command >\n");
				}
				else if (command.equalsIgnoreCase("osversion"))
				{
					output.writeBytes("Server OS Version: "	+ System.getProperty("os.version") + "\n");
					output.writeBytes("Enter command >\n");
				}
				else if (command.equalsIgnoreCase("user"))
				{
					output.writeBytes("Server User: " + System.getProperty("user.name")+ "\n");
					output.writeBytes("Enter command >\n");
				}
				else
				{
					output.writeBytes(menu);
				}
				command = input.readLine();
			}
			output.close();
			input.close();
			connection.close();

		}
	}

}