import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        try{
            Socket s = new Socket("localhost",50000);
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            
            //Socket number and data input and outputs
            dout.write(("HELO\n").getBytes()); 
            String str = din.readLine();
            System.out.println(str);

            //Send "HELO" and reads/prints incoming data
            dout.write(("AUTH 45961301\n").getBytes());
            str = din.readLine();
            System.out.println(str);

            //Send "HELO" and reads/prints incoming data
            dout.write(("REDY\n").getBytes());
            str = din.readLine();
            System.out.println(str);
            

            int jobID;
            int core;
            int memory;
            int disk;

            String[] jobInfo = str.split(" ");
            if (jobInfo[0].equals("JOBN")){
                jobID = Integer.parseInt(jobInfo[2]);
                core = Integer.parseInt(jobInfo[4]);
                memory = Integer.parseInt(jobInfo[5]);
                disk = Integer.parseInt(jobInfo[6]);
                System.out.println(" Job ID: " + jobID + ", Core: " + core + ", Memory: " + memory + ", Disk: " + disk + "\n");
            }

            while(str.contains("RVSP")){
                dout.write(("REDY\n").getBytes());
                dout.flush();
                System.out.println("server says: "+str);
            }
            //Send "HELO" and reads/prints incoming data
            dout.write(("QUIT\n").getBytes());
            str = din.readLine();
            System.out.println(str);

            //Send "BYE" and reads/prints incoming data
            s.close();
            }
        catch(Exception e) {
        System.out.println(e);
        }
    }
} 

