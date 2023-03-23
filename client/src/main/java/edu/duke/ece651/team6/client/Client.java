package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.duke.ece651.team6.shared.Commit;
import edu.duke.ece651.team6.shared.GameBasicSetting;
import edu.duke.ece651.team6.shared.GlobalMapInfo;
import edu.duke.ece651.team6.shared.Result;

/**
 * This class is a wrapper class for client side socket that has a Socket object
 * It builds the Socket object for one player of the game and provides methods
 * like "send/receive stream to/from connected server", "send/receive
 * serializable obeject to/from connect server"
 */
public class Client {

  final Socket socket;

  public Client(Socket socket) {
    this.socket = socket;
  }

  /**
   * Constructor one. Takes a string for hostname and an int for port number.
   * Creates a stream socket and connects it to the specified port number on the
   * named host.
   * 
   * @param hostname is a String of the server's hostname or null for the loopback
   *                 address.
   * @param port     is a Int for the port to connect to. Range: 0 - 65535.
   * @throws UnknownHostException     if the IP address of the host could not be
   *                                  determined from the given hostname
   * @throws IOException              if an I/O error occurs when creating the
   *                                  socket
   * @throws SecurityException        if a security manager exists and its
   *                                  checkConnect method doesn't allow the
   *                                  operation.
   * @throws IllegalArgumentException if the port parameter is outside the
   *                                  specified range of valid port values, which
   *                                  is between 0 and 65535, inclusive.
   */
  public Client(String hostname, int port) throws UnknownHostException, IOException {
    this.socket = new Socket(hostname, port);
  }

  /**
   * Constructor two. Takes an InetAddress for the specified IP address and an int
   * for the port number. Creates a stream socket and connects it to the specified
   * port number at the specified IP address.
   * 
   * @param address is an InetAddress object for the IP address.
   * @param port    is an int for the port number. Range: 0 - 65535.
   * @throws IOException              if an I/O error occurs when creating the
   *                                  socket.
   * @throws SecurityException        if a security manager exists and its
   *                                  checkConnect
   *                                  method doesn't allow the operation.
   * @throws IllegalArgumentException if the port parameter is outside the
   *                                  specified range of valid port values, which
   *                                  is between 0 and 65535, inclusive.
   * @throws NullPointerException     if address is null.
   * @throws ClassNotFoundException   if the class of a serialized object cannot
   *                                  be
   *                                  found.
   * 
   */
  // public Client(InetAddress address, int port) throws IOException,
  // ClassNotFoundException {
  // this.socket = new Socket(address, port);
  // System.out.println((String) recvObject());
  // }

  /**
   * Receive data from the connected server. The received data is stored in the
   * InputStream of the current client socket.
   * Returns the InputStream of the current client socket.
   * 
   * @return the InputStream for the given socket.
   * @throws IOException if an I/O error occurs when creating the input stream,
   *                     the socket is closed, the socket is not connected, or the
   *                     socket input has been shutdown using shutdownInput()
   */
  // public InputStream recv() throws IOException {
  // return this.socket.getInputStream();
  // }

  /**
   * Takes an InputStream object for the data to send, send all bytes in the given
   * InputStream to the connected server
   * 
   * @param tosend is an InputStream object that has the bytes as the data
   *               to send
   * @throws IOException if an I/O error occurs when creating the output stream or
   *                     if the socket is not connected.
   */
  // public void send(InputStream tosend) throws IOException {
  // OutputStream out = this.socket.getOutputStream();
  // out.write(tosend.readAllBytes());
  // OutputStream outToServer = this.socket.getOutputStream();
  // DataOutputStream out = new DataOutputStream(outToServer);

  // out.writeUTF("Hi! I want to play the game!");
  // }

  /**
   * Receive an object from the connected server. The object to be received must
   * be Serializable. (e.g., Class Map implements
   * Serializable, then receive a Map object using this method.)
   *
   * @return a serializable object that is receive from the client socket.
   * @throws IOException            if an I/O error occurs when creating the input
   *                                stream, the socket is closed, the socket is
   *                                not connected, or the socket input has been
   *                                shutdown using shutdownInput()
   * @throws ClassNotFoundException if the class of a serialized object cannot be
   *                                found.
   */
  public Object recvObject() throws IOException, ClassNotFoundException {
    InputStream in = this.socket.getInputStream();
    ObjectInputStream objectIn = new ObjectInputStream(in);
    return objectIn.readObject();
  }

  /**
   * takes the serializable object to be sent and send the serializable object to
   * the connected server (e.g., Class Map implements Serializable,
   * then send a Map object using this method.)
   * 
   * @param object is the serializable object to be sent
   * @throws IOException              if an I/O error occurs when creating the
   *                                  output stream or if the socket is not
   *                                  connected.
   * @throws InvalidClassException    if something is wrong with a class used by
   *                                  serialization.
   * @throws NotSerializableException if some object to be serialized does not
   *                                  implement the java.io.Serializable
   *                                  interface.
   */
  public void sendObject(Object object) throws IOException {
    OutputStream out = this.socket.getOutputStream();
    ObjectOutputStream objectOut = new ObjectOutputStream(out);
    objectOut.writeObject(object);
  }

  /**
   * Closes the current client socket.
   * Any thread currently blocked in an I/O operation upon this socket will throw
   * a SocketException.
   * 
   * Once a socket has been closed, it is not available for further networking use
   * (i.e. can't be reconnected or rebound). A new socket needs to be created.
   * 
   * Closing this socket will also close the socket's InputStream and
   * OutputStream.
   * 
   * If this socket has an associated channel then the channel is closed as well.
   * 
   * @throws IOException if an I/O error occurs when closing this socket.
   */
  public void closeSocket() throws IOException {
    this.socket.close();
  }

  /**
   * Send initial messages
   */
  // public String sendInitialMessage(String message) throws IOException,
  // ClassNotFoundException {
  // sendObject(message);
  // return (String) recvObject();
  // }

  /**
   * Send settings (how many units to put on each territory)
   */
  public void sendUpdatedGameBasicSetting(GameBasicSetting setting)
      throws IOException, ClassNotFoundException {
    sendObject(setting);
  }

  /**
   * Send commit to server
   * 
   * @param commit
   * @throws IOException, {@link ClassNotFoundException}
   */
  public void sendCommit(Commit commit) throws IOException, ClassNotFoundException {
    sendObject(commit);
  }

  /**
   * Send exit info to server
   * - True: ask for leave
   * - False: ask for keeping watching
   * 
   * @param exit
   * @throws IOException, {@link ClassNotFoundException}
   */
  public void sendExitInfo(Boolean exit) throws IOException, ClassNotFoundException {
    sendObject(exit);
  }

  /**
   * A generic method to receive a specified object from server through socket
   * 
   * @param expectedType
   * @return T
   * @throws InvalidObjectException if the received object is of a wrong type
   */
  public <T> T receiveSpecifiedObject(Class<T> expectedType) throws IOException, ClassNotFoundException {
    Object obj = recvObject();
    if (expectedType.isInstance(obj)) {
      return expectedType.cast(obj);
    } else {
      throw new InvalidObjectException("Incorrect object type. It should be a " + expectedType.getSimpleName() + ".");
    }
  }

  /**
   * Receive game basic setting for initialization phase
   * 
   * @param message
   * @return {@link GameBasicSetting}
   * @throws IOException, {@link ClassNotFoundException}
   */
  public GameBasicSetting recvGameBasicSetting() throws IOException, ClassNotFoundException {
    // sendObject(message); // TODO: remove?
    return receiveSpecifiedObject(GameBasicSetting.class);
  }

  /**
   * Receive a {@link GlobalMapInfo} object from server side
   * 
   * @return {@link GlobalMapInfo}
   * @throws IOException, {@link ClassNotFoundException}
   */
  public GlobalMapInfo recvGlobalMapInfo() throws IOException, ClassNotFoundException {
    return receiveSpecifiedObject(GlobalMapInfo.class);
  }

  /**
   * Receive a {@link Result} object from server side
   * 
   * @return {@link Result}
   * @throws IOException, {@link ClassNotFoundException}
   */
  public Result recvGameResult() throws IOException, ClassNotFoundException {
    return receiveSpecifiedObject(Result.class);
  }

}
