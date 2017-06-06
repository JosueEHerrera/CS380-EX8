import java.util.*;
import java.io.*;
import java.net.*;

public class WebServer {

		public static void main(String[] args) throws Exception{
			try (ServerSocket sSocket = new ServerSocket(8080)) {
				Boolean listening = true;

				System.out.println("Listening for connection on port 8080: ");

				while(listening){
					Socket socket = sSocket.accept();
					Runnable run = new Client(socket);
					Thread client = new Thread(run);
					client.start();

				}    

			}
		}
}

class Client implements Runnable{
	public Socket socket;

	public Client(Socket socket){
		this.socket = socket;
	}

	public void run(){
		String address = socket.getInetAddress().getHostAddress();	
		System.out.println("Client Connected: " + address);

		try{
			BufferedReader in = new BufferedReader(
			new InputStreamReader(socket.getInputStream(),"UTF-8"));

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			String request = in.readLine();
			String[] requestParam = request.split(" ");
			String filepath = "www" + requestParam[1];
			
			System.out.println("[Filepath]: " + filepath);
			File file = new File(filepath);
			
			File missing = new File("www/404.html"); 
			BufferedReader not = new BufferedReader(new FileReader(missing));
			
			if (file.exists()) {
				try {
					BufferedReader fbr = new BufferedReader(new FileReader(file));
					print200(out, file);
					send(fbr, out);
					
				} catch (FileNotFoundException e) {
						print404(out,missing);
						send(not,out);
			 			out.close();
				}
			} else {
				print404(out, missing);
				send(not, out);
			 	out.close();
			}
				out.close();
				socket.close();
		} catch (Exception e) {
				e.printStackTrace();
		}


	}

	public void print200(PrintWriter out, File file) {
		out.println("HTTP/1.1 200 OK\n" +
					"Content-type: text/html\n" +
					"Content-length: " + file.length() + "\n" +
					"\r\n");
	}

	public void print404(PrintWriter out, File file) {
		out.println("HTTP/1.1 404 Not Found\n" +
					"Content-type: text/html\n" +
					"Content-length: " + file.length() + "\n" +
					"\r\n");
	}

	public void send(BufferedReader br, PrintWriter out) throws Exception {
		
		for(String line; (line = br.readLine())!= null;){
			out.println(line);
		}
		out.flush();

	}




}