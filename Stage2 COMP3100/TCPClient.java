import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//write ./stage2-test-x86 "java TCPClient 127.0.0.1 50000 lrr" -o tt -n

public class TCPClient {

	// Global variables established for the socket, input and output
	// Write and read messages between client and server
	private Socket s = null;
	private BufferedReader din = null;
	private DataOutputStream dout = null;
	private TCPServer[] servers = new TCPServer[1];
	private String string;
	private Boolean flag = false;
	private String[] string_arr;


	public TCPClient() {
		try {
			s = new Socket("localhost", 50000);
			din = new BufferedReader(new InputStreamReader(s.getInputStream()));
			dout = new DataOutputStream(s.getOutputStream());

		} catch (UnknownHostException e) {
			System.out.println("Error: " + e);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

	
	public static void main(String args[]) {
		TCPClient client = new TCPClient();
		client.run();
	}

	public void run() {
		
		//sends HELO and prints data
		write("HELO");
		string = read();
		
		//Authenticate the user
		write("AUTH Douglas");
		string = read();

		//File that will be read
		File file = new File("ds-system.xml");
		readFile(file);
		
		write("REDY");
		string = read();

		//call first algorithm
		firstAlg();

		quit();
	}

	//First algorithm finds the available servers
	public void firstAlg() {
		
		if (string.contains("NONE")) {
			quit();

		} else {
			
			while (!flag) {
				
				if (string.contains("OK") || string.contains(".") || string.contains(".OK")) {
					write("REDY");
					string = read();
				}

				String[] msg = string.split("\\s+");
				String first = msg[0];

				while (first.contains("JCPL") || first.contains("RESR") || first.contains("RESF")) {
					write("REDY");
					string = read();

					msg = string.split("\\s+");
					first = msg[0];
				}
				
				if (first.contains("NONE")) {
					flag = true;
					break;
				}

				String[] avail = string.split("\\s+");
				findServer(avail);
				string = read();

				String num = avail[2];
				String schd = "SCHD " + num + " " + string_arr[0] + " " + string_arr[1];
				write(schd);
				string = read();
			}
		}
	}
	
	//finds the available servers for the job to be dispatched to 
	public void findServer(String[] info){
				write("GETS Available " + info[4] + " " + info[5] + " " + info[6]);
				String job = read();
				String[] id = job.split("\\s+");
				int memory = Integer.parseInt(id[1]);
				write("OK");
				job = read();
				if (job.contains(".")) {

					write("GETS Capable " + info[4] + " " + info[5] + " " + info[6]);
					job = read();
					id = job.split("\\s+");

					memory = Integer.parseInt(id[1]);

					write("OK");
					job = read();
					String[] disk = job.split("\\r?\\n");

					string_arr = disk[0].split("\\s+");
					checkServer(job, memory, disk);
					
				} else {
					String[] core = job.split("\\r?\\n");
					string_arr = core[0].split("\\s+");
					checkServer(job, memory, core);
				}
	}

	//second algorithm compares disk, memory and corecount
	public void checkServer(String disk, int memory, String[] core){
		for (int i = 0; i < memory; i++){

			core = disk.split("\\r?\\n");
			String[] second = core[0].split("\\s+");
			System.out.println(second);
			if (Integer.parseInt(second[4]) > Integer.parseInt(string_arr[4]) && Integer.parseInt(second[5]) > Integer.parseInt(string_arr[5]) && Integer.parseInt(second[6]) > Integer.parseInt(string_arr[6]) ){
				string_arr = second;
			}
			
			if (memory - 1 == i){
				write("OK");
				break;
			}
			else {
				disk = read();
			}
		}
	}

	// Parse through the XML file
	public void readFile(File file) {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document systemDocument = builder.parse(file);
			systemDocument.getDocumentElement().normalize();

			NodeList serverNodeList = systemDocument.getElementsByTagName("server");
			servers = new TCPServer[serverNodeList.getLength()];
			for (int j = 0; j < serverNodeList.getLength(); j++) {
				Element element = (Element) serverNodeList.item(j);
				String read = element.getAttribute("type");

				int dis = Integer.parseInt(element.getAttribute("disk"));
				int mem = Integer.parseInt(element.getAttribute("memory"));
				int cor = Integer.parseInt(element.getAttribute("cores"));

				TCPServer temp = new TCPServer(j, read, mem, cor, dis);
				servers[j] = temp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String read() {
		String text = "";
		try {
            text = din.readLine();
			string = text;

		} catch (IOException i) {
			System.out.println("Error: " + i);
		}
		return text;
	}


	public void write(String text) {
		try {
			dout.write((text + "\n").getBytes());
			dout.flush();
		} catch (IOException i) {
			System.out.println("Error: " + i);
		}
	}

	//sends QUIT to exit
	public void quit() {
		try {
			write("QUIT");
			string = read();
			if (string.contains("QUIT")) {
				din.close();
				dout.close();
				s.close();
			}
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

}