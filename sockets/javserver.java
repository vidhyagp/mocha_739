import java.io.*;
import java.net.*;
 
class JavaServer {
    public static void main(String args[]) throws Exception {
        String fromClient;
        String toClient;
 
        ServerSocket server = new ServerSocket(8080);
        System.out.println("wait for connection on port 8080");
 
        boolean run = true;
	try {
		while(run) {
		    Socket client = server.accept();
		    try {
			    System.out.println("got connection on port 8080");
			    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			    PrintWriter out = new PrintWriter(client.getOutputStream(),true); 
			    fromClient = in.readLine();
			    toClient = "sending back" + fromClient;
			    out.println(toClient);
			    System.out.println("received: " + fromClient);
		     } finally {
			    client.close();
		     }
		}
	} finally {
		server.close();
	}
    }
}
