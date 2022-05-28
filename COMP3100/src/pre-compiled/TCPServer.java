import java.net.*;
import java.io.*;

import java.util.concurrent.TimeUnit;

public class TCPServer {
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(50000);
		while (true) {
		try{
		Socket s = ss.accept();
		DataInputStream din = new DataInputStream(s.getInputStream());
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());

		String str = (String)din.readUTF();
		System.out.println("RCVD: "+str);

		dout.writeUTF("G'DAY");
		System.out.println("SENT: G'DAY");

		str = (String)din.readUTF();
		System.out.println("RCVD: "+str);

		dout.writeUTF("BYE");
		System.out.println("SENT: BYE");

		s.close();
		}
		catch(Exception e){System.out.println(e);}
		}
	}
}

