import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPClient {
    private static boolean flag = true;
    private static int largestServer = 0;

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            BufferedReader din = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            
            //sends HELO and prints data
            dout.write(("HELO\n").getBytes());
            dout.flush();
            String str = (String) din.readLine();
           // System.out.println(str);
            
            //Authenticate the user
            dout.write(("AUTH Douglas\n").getBytes());
            dout.flush();
            String str2 = (String) din.readLine();
            //System.out.println(str2);

            //Array of strings to find the best suitable server
            ArrayList<String> serverList = new ArrayList<String>(); 
            String serverType = null;

            dout.write(("REDY\n").getBytes());
            str2 = (String) din.readLine();
            //System.out.println(str2);
            String memory = str2.substring(0, 4);

            //Find ID of first available server
            ArrayList<Integer> largestServerID = new ArrayList<Integer>();
            int disk = 0;
            
            while (!memory.contains("NONE")) {
                
                //get job informaton
                String[] info = str2.split(" ");
                if ((info[0].toUpperCase()).contains("JOBN")) {
                    dout.write(("GETS Capable " + info[4] + " " + info[5] + " " + info[6] + "\n").getBytes());
                    str2 = (String) din.readLine();
                  //  System.out.println(str2);
                    
                    String[] getJobs = str2.split(" ");
                    int arr = Integer.parseInt(getJobs[1]);
                    dout.write(("OK\n").getBytes());

                    
                    for (int i = 0; i < arr; i++) {
                        str2 = (String) din.readLine();
                      //  System.out.println(str2);
                        
                        //find id of the first available server
                        if (i == 0) {
                            serverList.add(str2);
                            String[] spt = str2.split(" ");
                            int core = Integer.parseInt(spt[4]);
                            int id = Integer.parseInt(spt[1]);
                            // if core is greater than the disk of the data, disk will have the same data as the core
                            if (disk < core) {
                                serverType = spt[0]; 
                                disk = core;
                                largestServerID.clear();
                                largestServerID.add(id);
                            }

                            else if (serverType.contains(spt[0])) {
                                largestServerID.add(id);
                            }

                        }

                    }

                    dout.write(("OK\n").getBytes());
                    str2 = (String) din.readLine();
                  //  System.out.println(str2);

                    if (largestServer < largestServerID.size()) {
                        dout.write(("SCHD " + info[2] + " " + serverType + " " + largestServerID.get(largestServer) + "\n").getBytes());
                        str2 = (String) din.readLine();
                     //   System.out.println(str2);
                        largestServer++;
                    }
                    if (largestServer > (largestServerID.size()) - 1) {
                        largestServer = 0;
                    }
                }

                dout.write(("REDY\n").getBytes());
                str2 = (String) din.readLine();
                memory = str2.substring(0, 4);
                flag = false;
            }

            //sends QUIT to exit
            dout.write(("QUIT\n").getBytes());
            str2 = (String) din.readLine();
          //  System.out.println(str2);
            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
