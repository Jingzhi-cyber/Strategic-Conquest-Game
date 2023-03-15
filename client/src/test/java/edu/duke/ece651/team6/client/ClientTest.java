package edu.duke.ece651.team6.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.Territory;

public class ClientTest {
  private Socket mockSocket;
  private Client client;

  @BeforeEach
  public void setUp() throws IOException {
    mockSocket = mock(Socket.class);
    client = new Client(mockSocket);

    InetAddress mockAddress = mock(InetAddress.class);
    when(mockAddress.isReachable(anyInt())).thenReturn(true);

    Socket mockSocket = mock(Socket.class);
    // when(mockSocket.getOutputStream()).thenReturn(mock(OutputStream.class));
    when(mockSocket.getInputStream()).thenReturn(mock(InputStream.class));

  }

  @Test
  public void testConstructors() throws IOException {

  }

  @Test
  public void testRecvObject() throws IOException, ClassNotFoundException {
    HashSet<Territory> territories = new HashSet<Territory>() {
      {
        add(new Territory());
      }
    };

    // Create a mock Socket object and InputStream object
    Socket mockSocket = mock(Socket.class);
    InputStream mockInputStream = mock(InputStream.class);

  }

}
