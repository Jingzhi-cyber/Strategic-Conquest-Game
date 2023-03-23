package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.Result;

public class ClientTest {
  private static final int PORT = 8888;
  private ServerSocket server;
  private Socket clientSocket;
  OutputStream outputStream = mock(OutputStream.class);

  @BeforeEach
  void setup() throws IOException {
    // create a server socket
    server = mock(ServerSocket.class);
    // accept a client socket
    // clientSocket = new Socket(InetAddress.getLocalHost(), PORT);
    clientSocket = mock(Socket.class);
    // inputStream = mock(ObjectInputStream.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    // close the client socket
    clientSocket.close();
    // close the server socket
    server.close();
  }

  // class ClientTestClass {
  // Socket getSocket(String localhost, int port) throws UnknownHostException,
  // IOException {
  // return new Socket(localhost, port);
  // }

  // Socket getSocket(InetAddress addr, int port) throws UnknownHostException,
  // IOException {
  // return new Socket(addr, port);
  // }

  // ObjectOutputStream getServerOutputStream(OutputStream out) throws IOException
  // {
  // return new ObjectOutputStream(out);
  // }
  // }

  // @Test
  // void testConstructors() throws UnknownHostException, IOException {
  // ClientTestClass clientTest = new ClientTestClass();
  // // create a mock object for the Socket class
  // // throw a ConnectException when connect() method is called

  // when(clientTest.getSocket("localhost", PORT)).thenReturn(clientSocket);

  // Socket client = mock(Socket.class);
  // when(server.accept()).thenReturn(client);

  // Commit commit = new Commit(1); // replace with your own object
  // ByteArrayOutputStream baos = new ByteArrayOutputStream();
  // ObjectOutputStream oos = new ObjectOutputStream(baos);
  // oos.writeObject(commit);
  // oos.flush();
  // byte[] serializedObject = baos.toByteArray();

  // OutputStream serverOutputStream = mock(OutputStream.class);
  // when(client.getOutputStream()).thenReturn(serverOutputStream);
  // when(clientTest.getServerOutputStream(serverOutputStream)).thenReturn(oos);
  // //when(oos.writeObject(serializedObject);)
  // // send the serialized object to the server
  // // client.sendObject(serializedObject);

  // // OutputStream out = clientSocket.getOutputStream();
  // // ObjectOutputStream objectOut = new ObjectOutputStream(out);
  // // objectOut.writeObject(object);

  // // create a client with the mock socket
  // Client newClient = new Client("localhost", PORT);
  // assertNotNull(newClient.socket);
  // }

  // @Test
  // void testConstructorWithAddressAndPort() throws IOException,
  // ClassNotFoundException {
  // // create a client with address and port
  // Client client = new Client(InetAddress.getLocalHost(), PORT);
  // // assert that the client socket is not null
  // assertNotNull(client.socket);
  // }

  void setOutputStreamAndAssumption_recvSpecifiedObject(Object object) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object); // replace MyObject with your own object

    oos.flush();

    byte[] serializedObject = baos.toByteArray();

    when(clientSocket.getInputStream())
        .thenReturn(new ByteArrayInputStream(serializedObject));
  }

  <T> void assertExpectedRecvObject(Object object, Class<T> c) {
    // assert that the received object is not null and of the expected class
    assertNotNull(object);
    assertEquals(object.getClass(), c);
  }

  @Test
  void testRecvObject() throws IOException, ClassNotFoundException {
    /* Create a client with the client socket */
    Client client = new Client(clientSocket);
    setOutputStreamAndAssumption_recvSpecifiedObject(new Commit(1));

    /* Test recv general object */
    // receive the object from the server
    Object receivedObject = client.recvObject();
    assertExpectedRecvObject(receivedObject, Commit.class);

    /* Test recv GameBasicSetting */
    setOutputStreamAndAssumption_recvSpecifiedObject(new GameBasicSetting(1, 2, null, 3));
    GameBasicSetting receivedObject2 = client.recvGameBasicSetting();
    assertExpectedRecvObject(receivedObject2, GameBasicSetting.class);

    /* Test recv GlobalMapInfo */
    setOutputStreamAndAssumption_recvSpecifiedObject(new GlobalMapInfo());
    GlobalMapInfo receivedObject3 = client.recvGlobalMapInfo();
    assertExpectedRecvObject(receivedObject3, GlobalMapInfo.class);

    /* Test recv Result */
    setOutputStreamAndAssumption_recvSpecifiedObject(new Result());
    Result receivedObject4 = client.recvGameResult();
    assertExpectedRecvObject(receivedObject4, Result.class);

    /* Close the socket */
    client.closeSocket();
  }

  @Test
  void test_recvObject_invalidCase() throws IOException, ClassNotFoundException {
    Client client = new Client(clientSocket);
    setOutputStreamAndAssumption_recvSpecifiedObject(new Commit(1));
    assertThrows(InvalidObjectException.class, () -> client.recvGameResult());
  }

  @Test
  void testSendObject() throws IOException, ClassNotFoundException {
    // create a client with the client socket
    Client client = new Client(clientSocket);
    Commit commit = new Commit(1); // replace with your own object
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(commit);
    oos.flush();
    byte[] serializedObject = baos.toByteArray();

    when(clientSocket.getOutputStream()).thenReturn(this.outputStream);

    // send the serialized object to the server
    client.sendObject(serializedObject);

    // verify that the object was sent correctly
    // verify(outputStream, times(1)).write(serializedObject);

    /* Send Commit */
    client.sendCommit(commit);

    /* Send Exit Info */
    client.sendExitInfo(true);

    /* Send Game Basic Info */
    client.sendUpdatedGameBasicSetting(new GameBasicSetting(1, 2, null, 3));

    client.closeSocket();
  }

  private class ClientThread implements Runnable {
    public String hostname;
    public int port;

    public ClientThread(String hostname, int port) {
      this.port = port;
      this.hostname = hostname;
    }

    @Override
    public void run() {
      try {
        Client client = new Client(hostname, port);
        String message = (String) client.recvObject();
        assertEquals(message, "Connected to server successfully!");
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void test_client_constructor() throws IOException, InterruptedException, ClassNotFoundException {
    ServerSocket serverSocket = new ServerSocket(4444);
    ClientThread clientThread = new ClientThread("localhost", 4444);
    Thread connect = new Thread(clientThread);
    connect.start();

    ClientThread errorHostname = new ClientThread("  ", 4444);
    Thread errorConnect = new Thread(errorHostname);
    errorConnect.start();

    Socket connected = serverSocket.accept();
    OutputStream out = connected.getOutputStream();
    ObjectOutputStream objectOut = new ObjectOutputStream(out);
    objectOut.writeObject("Connected to server successfully!");

    connect.join();
    serverSocket.close();
  }

}
