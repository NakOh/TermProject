import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TCPServer {
	private HashMap<String, DataOutputStream> clients;
	private ServerSocket serverSocket;

	public static void main(String[] args) {
		new TCPServer().start();
	}

	public TCPServer() {
		clients = new HashMap<String, DataOutputStream>();
		Collections.synchronizedMap(clients);
	}

	public void start() {
		try {
			Socket socket;
			serverSocket = new ServerSocket(7777);
			System.out.println("서버가 시작되었습니다.");
			while (true) {
				socket = serverSocket.accept();
				ServerReceiver receiver = new ServerReceiver(socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream input;
		DataOutputStream output;

		public ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		@Override
		public void run() {
			String name = "";
			try {
				name = input.readUTF();
				sendToAll("#" + name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "님이 서버에 접속했습니다.");
				clients.put(name, output);
				System.out.println(
						name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "님이 서버에 접속했습니다.");
				System.out.println("현재 " + clients.size() + "명이 Server에 접속 중입니다");
				// 메세지 전송
				while (input != null) {
					sendToAll(input.readUTF());
				}
			} catch (IOException e) {
			} finally {
				// 접속이 종료되면
				clients.remove(name);
				sendToAll("#" + name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "님이 서버에서 나갔습니다.");
				System.out.println(
						name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "님이 서버에서 나갔습니다.");
				System.out.println("현재 " + clients.size() + "명이 Server에 접속중입니다.");
			}
		}

		public void sendToAll(String message) {
			Iterator<String> it = clients.keySet().iterator();
			while (it.hasNext()) {
				try {
					DataOutputStream dos = clients.get(it.next());
					dos.writeUTF(message);
				} catch (Exception e) {
				}
			}
		}
	}
}