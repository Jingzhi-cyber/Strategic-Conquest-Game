package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.SocketKey;

public class ServerTest {

  private void sendObject(Socket clientSocket, Object object) throws IOException {
    OutputStream out = clientSocket.getOutputStream();
    ObjectOutputStream objectOut = new ObjectOutputStream(out);
    objectOut.writeObject(object);
  }

  private Object recvObject(Socket clieSocket) throws IOException, ClassNotFoundException {
    InputStream in = clieSocket.getInputStream();
    ObjectInputStream objectIn = new ObjectInputStream(in);
    return objectIn.readObject();
  }

  private class ConnectThread implements Runnable {
    public String hostname;
    public int port;
    public Object toSend;

    public ConnectThread(String hostname, int port, Object toSend) {
      this.port = port;
      this.hostname = hostname;
      this.toSend = toSend;

    }

    @Override
    public void run() {
      try {
        Socket clientSocket;
        while (true) {
          try {
            clientSocket = new Socket(hostname, port);
          } catch (Exception e) {
            continue;
          }
          break;
        }
        sendObject(clientSocket, toSend);
        recvObject(clientSocket);
        sendObject(clientSocket, toSend);
        sendObject(clientSocket, toSend);
        recvObject(clientSocket);
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

    }

  }

  @Test
  public void test_recv_from_all() throws IOException, ClassNotFoundException {
    ConnectThread connectThread = new ConnectThread("localhost", 6666, "hi");
    ServerSocket ss = new ServerSocket(6666);
    connectThread = new ConnectThread("localhost", 6666, "hi");
    Thread connect = new Thread(connectThread);
    connect.setPriority(5);
    connect.start();
    List<SocketKey> clientSockets = new ArrayList<>();
    Socket socket = ss.accept();
    AccountManager m = AccountManager.getInstance();
    SocketKey key = m.add("hi", socket);
    clientSockets.add(key);
    Server server = new Server(clientSockets);
    server.recvObjectFromALL();
    server.recvObject(key);
    server.recvObjectByPlayerID(0);
    server.sendObject(key, "hi");
    server.sendObjectByPlayerID(0, "hello");
    server.sendObjectToAll("hello");
    server.closeClientSocket(clientSockets.get(0));
    assertDoesNotThrow(() -> server.closeClientSocket(key));
    assertEquals(1, server.getClientSockets().size());
    ss.close();
  }

}
