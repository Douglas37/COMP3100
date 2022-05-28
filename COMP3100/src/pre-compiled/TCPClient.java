import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPClient {

    private static boolean completeLoop = true;
    private static int largestServer = 0;

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            
            //sends "HELO" and prints data
            dout.write(("HELO\n").getBytes());
            dout.flush();
            String str = (String) din.readLine();
            System.out.println(str);
            
            //Authenticate the User
            dout.write(("AUTH Douglas\n").getBytes());
            dout.flush();
            str = (String) din.readLine();
            System.out.println(str);
            
            //Finding the largest Server type
            ArrayList<String> serverList = new ArrayList<String>();
            String serverType = null;
            
            //Signal server to make next move
            dout.write(("REDY\n").getBytes());
            str = (String) din.readLine();
            System.out.println(str);
            String memory = str.substring(0, 4);

            //Find the IP address of largest server
            ArrayList<Integer> largestServerIP = new ArrayList<Integer>();
            int disk = 0;
            
            //Check nessage is not NONE
            while (!memory.contains("NONE")) {
            
                //Get the job information 
            	String[] info = str.split(" ");
                int jobRequest = Integer.parseInt(info[2]);
                if ((info[0].toUpperCase()).contains("JOBN")) {
                    dout.write(("GETS Capable " + info[4] + " " + info[5] + " " + info[6] + "\n").getBytes());
                    str = (String) din.readLine();
                    System.out.println(str);

                    String[] getJobs = str.split(" ");
                    int arr = Integer.parseInt(getJobs[1]);
                    dout.write(("OK\n").getBytes());

                    //Finding the largest server
                    for (int i = 0; i < arr; i++) {


                        str = (String) din.readLine();
                        System.out.println(str);

                        if(i == 0){

                            String[] spt = str.split(" ");
                            serverType = spt[0];

                        } 

                        
                        //Find the IP address of the largest server to send job request to
                        /*if (completeLoop) {
                            serverList.add(str);
                            String[] spt = str.split(" ");
                            int core = Integer.parseInt(spt[4]);
                            int IP = Integer.parseInt(spt[1]);
                            if (disk < core) {
                                serverType = spt[0];
                                disk = core;
                                largestServerIP.clear();
                                largestServerIP.add(IP);
                            }
                            else if (serverType.contains(spt[0])) {
                                largestServerIP.add(IP);
                            }
                        } */
                    }

                    dout.write(("OK\n").getBytes());
                    str = (String) din.readLine();
                    System.out.println(str);

                    // if (largestServer< largestServerIP.size()) {
                    //     dout.write(("SCHD " + jobRequest + " " + serverType + " " + largestServerIP.get(largestServer) + "\n").getBytes());
                    //     str = (String) din.readLine();
                    //     System.out.println(str);
                    //     largestServer++;
                    // }

                    // if (largestServer > (largestServerIP.size()) - 1) {
                    //     largestServer = 0;
                    // }

                    dout.write(("SCHD " + jobRequest + " " + serverType + " " + "0" + "\n").getBytes());
                    str = (String) din.readLine();
                    System.out.println(str);
                    
                    while(!(str.contains("OK"))){
                        str = (String) din.readLine();
                    }
                }
                
                //Once largest server is found, send the job to the server and close loop
                
                memory = str.substring(0, 4);
                dout.write(("REDY\n").getBytes());
                str = (String) din.readLine();
                completeLoop = false;
                
                
            }

            public static void jobSchedule(DataOutputStream dout, String job, String serverType, String serverID) throw IOException {
                String schedule = "SCHD " + job + " " + serverType + " " + serverID;
                writeData(dout, schd);
            }

            public static void jobMigrate(DataOutputStream dout, String job, String serverType, String serverID, String type, String IP) throw IOException {
                String migrate = "MIGJ " + job + " " + serverType + " " + serverID + " " + type + " " + IP;
                writeData(dout, schd);
            }

            //sends QUIT to exit 
            dout.write(("QUIT\n").getBytes());
            str = (String) din.readLine();
            System.out.println(str);
            dout.flush();
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
