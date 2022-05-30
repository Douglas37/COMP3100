public class TCPServer {

	public int id;
	public String type;
	public int memory;
	public int cores;
	public int disk;



	TCPServer(int id, String t, int m, int c, int d) {
		this.id = id;
		this.type = t;
		this.memory = m;
		this.cores = c;
		this.disk = d;
	}
}
