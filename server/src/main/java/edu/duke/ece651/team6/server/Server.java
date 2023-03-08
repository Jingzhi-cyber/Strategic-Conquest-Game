package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * This class is a wrapper class for server side socket that has a ServerSocket
 * and an ArrayList of Socket thatare connected to the ServerSocket. It builds a
 * server socket for the game's server and provides methods like "accept
 * connection from client socket", "send/receive stream to/from connected
 * client", "send/receive serializable obeject to/from connect client"
 */
public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Socket> clientSockets;

  /**
   * constructs a wrapper server object. Takes an int as the local port number and
   * bind the ServerSocket to listen to that port.
   * 
   * @param port is the local port number to bind. Range: 0 - 65535. If the given
   *             value is 0, it means that the port number is automatically
   *             allocated, typically from an ephemeral port range. This port
   *             number can then be retrieved by calling getLocalPort().
   * @throws IOException              if an I/O error occurs when opening the
   *                                  socket.
   * @throws SecurityException        if a security manager exists and its
   *                                  checkListen method doesn't allow the
   *                                  operation.
   * @throws IllegalArgumentException if the port parameter is outside the
   *                                  specified range of valid port values, which
   *                                  is between 0 and 65535, inclusive
   */
  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    clientSockets = new ArrayList<>();
  }

  /**
   * Listens for a connection to be made to wrapper server socket and accepts it.
   * Then add the connected client socket into clientSockets ArrayList.
   * The method blocks until a connection is made.
   * 
   * @return a socket object for the client connection
   * @throws IOException                  if an I/O error occurs when waiting for
   *                                      a
   *                                      connection.
   * @throws SecurityException            if a security manager exists and its
   *                                      checkAccept
   *                                      method doesn't allow the operation
   * @throws TimeoutException             if a timeout was previously set with
   *                                      setSoTimeout
   *                                      and the timeout has been reached.
   * @throws IllegalBlockingModeException if this socket has an associated
   *                                      channel, the channel is in non-blocking
   *                                      mode, and there is no connection ready
   *                                      to be accepted
   */
  public Socket accept() throws IOException {
    Socket clientSocket = serverSocket.accept();
    clientSockets.add(clientSocket);
    return clientSocket;
  }

  /**
   * takes a Socket object for the client to receive from, returns an
   * InputStream of the given socket
   * 
   * @param clientSocket is a Socket object to receive from
   * @return an InputStream for the given socket.
   * @throws IOException if an I/O error occurs when creating the input stream,
   *                     the socket is closed, the socket is not connected, or the
   *                     socket input has been shutdown using shutdownInput()
   */
  // public InputStream recv(Socket clientSocket) throws IOException {
  // return clientSocket.getInputStream();
  // }

  /**
   * takes a Socket object for the client to send to and an InputStream object for
   * the data to send, send all bytes in the given InputStream to the given Socket
   *
   * @param clientSocket is a Socket object for the client to send data to
   * @param tosend       is an InputStream object that has the bytes as the data
   *                     to send
   * @throws IOException if an I/O error occurs when creating the output stream or
   *                     if the socket is not connected.
   */
  // public void send(Socket clientSocket, InputStream tosend) throws IOException
  // {
  // OutputStream out = clientSocket.getOutputStream();
  // out.write(tosend.readAllBytes());
  // }

  /**
   * takes a Socket object for the client to receive an object from. The object to
   * be received must be Serializable. (e.g., Class Map implements Serializable,
   * then receive a Map object using this method.)
   *
   * @param clientSocket is a Socket object for the client to receive one
   *                     serializable object from
   * @return a serializable object that is receive from the client socket.
   * @throws IOException            if an I/O error occurs when creating the input
   *                                stream, the socket is closed, the socket is
   *                                not connected, or the socket input has been
   *                                shutdown using shutdownInput()
   * @throws ClassNotFoundException if the class of a serialized object cannot be
   *                                found.
   */
  public Object recvObject(Socket clientSocket) throws IOException, ClassNotFoundException {
    InputStream in = clientSocket.getInputStream();
    ObjectInputStream objectIn = new ObjectInputStream(in);
    return objectIn.readObject();
  }

  /**
   * takes a socket object for the client to send one serializabel object to and
   * the serializable object to be sent. (e.g., Class Map implements Serializable,
   * then send a Map object using this method.)
   * 
   * @param clientSocket is a socket object for the client to send one
   *                     serializable object to
   * @param object       is the serializable object to be sent
   * @throws IOException              if an I/O error occurs when creating the
   *                                  output stream or if the socket is not
   *                                  connected.
   * @throws InvalidClassException    if something is wrong with a class used by
   *                                  serialization.
   * @throws NotSerializableException if some object to be serialized does not
   *                                  implement the java.io.Serializable
   *                                  interface.
   */
  public void sendObject(Socket clientSocket, Object object) throws IOException {
    OutputStream out = clientSocket.getOutputStream();
    ObjectOutputStream objectOut = new ObjectOutputStream(out);
    objectOut.writeObject(object);
  }

  /**
   * Closes this server socket. Any thread currently blocked in accept() will
   * throw a SocketException.
   * If this socket has an associated channel then the channel is closed as well.
   * 
   * @throws IOException if an I/O error occurs when closing the serverSocket
   */
  public void closeServerSocket() throws IOException {
    this.serverSocket.close();
  }

  /**
   * Closes one socket for client.
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
   * @param clientSocket is the socket for one client that is to be closed.
   * @throws IOException if an I/O error occurs when closing this socket.
   */
  public void closeClientSocket(Socket clientSocket) throws IOException {
    clientSocket.close();
  }

  /**
   * Returns the local address of this server socket as an InetAddress object.
   * 
   * @return an InetAddress object that is the local address of this server
   *         socket.
   */
  public InetAddress getInetAddress() {
    return this.serverSocket.getInetAddress();
  }
}
