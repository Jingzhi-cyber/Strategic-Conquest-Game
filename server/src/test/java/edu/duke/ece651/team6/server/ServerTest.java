package edu.duke.ece651.team6.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

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
    // public boolean ready;
    // public Exception failure;
    public String hostname;
    public int port;
    public Object toSend;
    public int close = 0;

    public ConnectThread(String hostname, int port, Object toSend) {
      this.port = port;
      this.hostname = hostname;
      this.toSend = toSend;

    }

    @Override
    public void run() {
      // try {
      // synchronized (this) {
      // while (!ready) {
      // wait();
      // }
      // }
      try {
        Socket clientSocket = new Socket(hostname, port);
        String message = (String) recvObject(clientSocket);
        assertEquals(message, "Connected to server successfully!");
        if (close == 1) {
          clientSocket.close();
        }
        sendObject(clientSocket, toSend);
        String message1 = (String) recvObject(clientSocket);
        assertEquals(message1, "Your commit has been received!");
        String message2 = (String) recvObject(clientSocket);
        assertEquals(message2, "Time to submit your commit!");
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

    }

  }

  @Test
  public void test_accept() throws IOException, InterruptedException, ClassNotFoundException {
    Server server = new Server(4444);
    ConnectThread connectThread = new ConnectThread("localhost", 4444, "Hi, I'm a player!");

    ConnectThread connectThread1 = new ConnectThread("  ", 4444, "Hi, I'm a player!");
    Thread connect1 = new Thread(connectThread1);
    connect1.start();

    Thread connect = new Thread(connectThread);
    // connectThread.ready = false;
    connect.setPriority(5);
    connect.start();

    // synchronized (connectThread) {
    // connectThread.ready = true;
    // connectThread.notify();
    // }

    Socket connectedSocket = server.accept();

    // assertNull(connectThread.failure);
    assertNotNull(connectedSocket);

    assertEquals("Hi, I'm a player!", (String) server.recvObjectByPlayerID(0));

    server.sendObject(connectedSocket, "Your commit has been received!");
    server.sendObjectByPlayerID(0, "Time to submit your commit!");
    connect.join();

    ArrayList<Socket> single = new ArrayList<>();
    single.add(connectedSocket);

    assertEquals(single, server.getClientSockets());

    assertThrows(IllegalArgumentException.class, () -> server.closeClientSocket(new Socket()));

    assertDoesNotThrow(() -> server.closeClientSocket(connectedSocket));
    assertDoesNotThrow(() -> server.closeServerSocket());

  }

  @Test
  public void test_multi_accept() throws IOException, ClassNotFoundException, InterruptedException {
    Server server = new Server(5555);
    int playerNum = 3;
    ArrayList<Thread> multiThreads = new ArrayList<>();
    for (int i = 0; i < playerNum; i++) {
      ConnectThread connectThread = new ConnectThread("localhost", 5555, "Hi, I'm a player!");
      Thread connect = new Thread(connectThread);
      // connectThread.ready = false;
      connect.setPriority(5);
      connect.start();
      multiThreads.add(connect);
    }

    // synchronized (connectThread) {
    // connectThread.ready = true;
    // connectThread.notify();
    // }

    assertThrows(IllegalArgumentException.class, () -> server.acceptMultiPlayers(-1));
    server.acceptMultiPlayers(3);

    ArrayList<Object> playerInfos = server.recvObjectFromALL();
    assertEquals(playerNum, playerInfos.size());

    for (int i = 0; i < playerNum; i++) {
      assertEquals("Hi, I'm a player!", (String) playerInfos.get(i));
    }

    server.sendObjectToAll("Time to submit your commit!");

    for (Thread thread : multiThreads) {
      thread.join();
    }

    ArrayList<Socket> clients = server.getClientSockets();
    Socket client1 = clients.get(0);
    Socket client2 = clients.get(1);
    Socket client3 = clients.get(2);

    assertDoesNotThrow(() -> server.closeClientSocket(client1));
    assertEquals(2, server.getClientSockets().size());
    assertDoesNotThrow(() -> server.closeClientSocket(client2));
    assertEquals(1, server.getClientSockets().size());
    assertDoesNotThrow(() -> server.closeClientSocket(client3));
    assertEquals(0, server.getClientSockets().size());
    assertDoesNotThrow(() -> server.closeServerSocket());

  }

  @Test
  public void test_recv_from_all() throws IOException, ClassNotFoundException {
    Server server = new Server(6666);
    ConnectThread connectThread = new ConnectThread("localhost", 6666, "hi");
    connectThread.close = 1;
    Thread connect = new Thread(connectThread);
    // connectThread.ready = false;
    connect.setPriority(5);
    connect.start();

    // synchronized (connectThread) {
    // connectThread.ready = true;
    // connectThread.notify();
    // }

    server.acceptMultiPlayers(1);
    server.recvObjectFromALL();

  }

  @Test
  public void test_recv_from_all2() throws IOException, ClassNotFoundException {
    Server server = new Server(7777);
    ConnectThread connectThread = new ConnectThread("localhost", 7777, "hi");
    Thread connect = new Thread(connectThread);
    // connectThread.ready = false;
    connect.setPriority(5);
    connect.start();

    // synchronized (connectThread) {
    // connectThread.ready = true;
    // connectThread.notify();
    // }

    server.acceptMultiPlayers(1);

    server.recvObjectFromALL();

  }

}
