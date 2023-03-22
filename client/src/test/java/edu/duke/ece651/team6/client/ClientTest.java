package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;

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

  private void runAssumptions(InputStream input, OutputStream output) throws IOException {
    when(this.clientSocket.getInputStream()).thenReturn(input);
    when(this.clientSocket.getOutputStream()).thenReturn(output);
  }

  @Disabled
  @Test
  void testConstructorWithHostnameAndPort() throws UnknownHostException, IOException {
    // create a client with hostname and port
    Client client = new Client("localhost", PORT);
    // assert that the client socket is not null
    assertNotNull(client.socket);
  }

  @Disabled
  @Test
  void testConstructorWithAddressAndPort() throws IOException, ClassNotFoundException {
    // create a client with address and port
    Client client = new Client(InetAddress.getLocalHost(), PORT);
    // assert that the client socket is not null
    assertNotNull(client.socket);
  }

  @Test
  void testRecvObject() throws IOException, ClassNotFoundException {
    // create a client with the client socket
    Client client = new Client(clientSocket);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(new Commit(1)); // replace MyObject with your own object

    oos.flush();

    byte[] serializedObject = baos.toByteArray();

    when(clientSocket.getInputStream())
        .thenReturn(new ByteArrayInputStream(serializedObject));

    // receive the object from the server
    Object receivedObject = client.recvObject();
    // assert that the received object is not null and of the expected class
    assertNotNull(receivedObject);
    assertEquals(receivedObject.getClass(), Commit.class);

    
    client.closeSocket();
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

}
