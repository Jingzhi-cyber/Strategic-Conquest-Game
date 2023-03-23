package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

  private class RecvObjectThead implements Runnable {

    private int playerID;
    private Socket clientSocket;

    private ArrayList<Object> recvedObjects;

    public RecvObjectThead(int playerID, Socket clientSocket, ArrayList<Object> recvedObjects) {
      this.playerID = playerID;
      this.clientSocket = clientSocket;
      this.recvedObjects = recvedObjects;
    }

    @Override
    public void run() {
      try {
        Object object = recvObject(clientSocket);
        this.recvedObjects.set(playerID, object);
        sendObject(clientSocket, "Your commit has been received!");
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        // maybe request the client to send again?
      }
    }
  }

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
    sendObject(clientSocket, "Connected to server successfully!");
    return clientSocket;
  }

  /**
   * This method takes an int as the number of players to connect to the server.
   * Then accepts these players one by one and adds their corresponding client
   * sockets to the ArrayList "ClientSockets". The client socket of the first
   * accepted player will be the first to be added to the ArrayList, so this
   * player will have playerID 0. And the next accepted player will have playerID
   * 1 and so on. This method will block until the required number (aka,
   * playerNum) of connections are made.
   * 
   * @param playerNum is an int of the number of players trying to connect to this
   *                  server. This playerNum must not be less than 0. If it is 0,
   *                  then this method does nothing.
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
   * @throws IllegalArgumentException     if the given playerNum is less than 0
   */
  public void acceptMultiPlayers(int playerNum) throws IOException {
    if (playerNum < 0) {
      throw new IllegalArgumentException("The given playerNum must not less than 0, but is : " + playerNum);
    }
    for (int i = 0; i < playerNum; i++) {
      this.accept();
    }
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
   * This method takes an int that is the playerID to receive the object from and
   * return the received object from that player. The object to
   * be received must be Serializable. (e.g., Class Map implements Serializable,
   * then receive a Map object using this method.)
   * 
   * @param playerID is an int that is the playerID to receive the object from.
   *                 Range: 0 - (clientSockets.size() - 1), otherwise throws
   *                 IndexOutOfBoundsException
   * @return a serializable object that is receive from the player specified by
   *         the playerID
   * @throws IOException               if an I/O error occurs when creating the
   *                                   input
   *                                   stream, the socket is closed, the socket is
   *                                   not connected, or the socket input has been
   *                                   shutdown using shutdownInput()
   * @throws ClassNotFoundException    if the class of a serialized object cannot
   *                                   be
   *                                   found.
   * @throws IndexOutOfBoundsException if the given playerID is out of range
   *                                   (playerID < 0 || index >=
   *                                   clientSockets.size())
   */
  public Object recvObjectByPlayerID(int playerID) throws IOException, ClassNotFoundException {
    return recvObject(this.clientSockets.get(playerID));
  }

  /**
   * This method tries to receive an object from all players by creating
   * playNum(e.g. 4) threads, each thread will receive one object, send a string
   * of confirmation to the specific player and add the object into recvedObjecs
   * ArrayList. After all threads are finished, this method will return the
   * recvedObject ArrayList.
   *
   * @return an ArrayList that has all the received objects.
   */
  public ArrayList<Object> recvObjectFromALL() {
    int playerNum = clientSockets.size();
    ArrayList<Object> recvedObjects = new ArrayList<>(playerNum);
    for (int i = 0; i < playerNum; i++) {
      recvedObjects.add(null);
    }
    ArrayList<Thread> recvThreads = new ArrayList<>(playerNum);
    for (int i = 0; i < playerNum; i++) {
      Thread recvThread = new Thread(new RecvObjectThead(i, clientSockets.get(i), recvedObjects));
      recvThread.setPriority(5);
      recvThread.start();
      recvThreads.add(recvThread);
    }
    for (Thread recvObjectThread : recvThreads) {
      try {
        recvObjectThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return recvedObjects;

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
   * This method takes an int that is the playerID to send the object to and
   * the serializable object to be sent. (e.g., Class Map implements Serializable,
   * then send a Map object using this method.)
   * 
   * @param playerID is an int that is the playerID to receive the object from.
   *                 Range: 0 - (clientSockets.size() - 1), otherwise throws
   *                 IndexOutOfBoundsException
   * @param object
   * @param object   is the serializable object to be sent
   * @throws IOException               if an I/O error occurs when creating the
   *                                   output stream or if the socket is not
   *                                   connected.
   * @throws InvalidClassException     if something is wrong with a class used by
   *                                   serialization.
   * @throws NotSerializableException  if some object to be serialized does not
   *                                   implement the java.io.Serializable
   *                                   interface.
   * @throws IndexOutOfBoundsException if the given playerID is out of range
   *                                   (playerID < 0 || index >=
   *                                   clientSockets.size())
   */
  public void sendObjectByPlayerID(int playerID, Object object) throws IOException {
    sendObject(this.clientSockets.get(playerID), object);
  }

  /**
   * This method takes the object to send, and send this object to all the players
   * in the ArrayList clientSockets.
   * 
   * @param object is the object to send. This object must be serializable.
   * @throws IOException              if an I/O error occurs when creating the
   *                                  output stream or if the socket is not
   *                                  connected.
   * @throws InvalidClassException    if something is wrong with a class used by
   *                                  serialization.
   * @throws NotSerializableException if some object to be serialized does not
   *                                  implement the java.io.Serializable
   *                                  interface.
   */
  public void sendObjectToAll(Object object) throws IOException {
    for (Socket clientSocket : clientSockets) {
      sendObject(clientSocket, object);
    }
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
   * Closes one socket for client. And close the corresponding client socket form
   * ArrayList clientSockets.
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
   * @param playerID is an int ID for one client that is to be closed.
   * @throws IOException               if an I/O error occurs when closing this
   *                                   socket.
   * @throws IndexOutOfBoundsException if the given playerID is out of range
   *                                   (playerID < 0 || index >=
   *                                   clientSockets.size())
   */
  // public void closeClientSocketByID(int playerID) throws IOException {
  // clientSockets.remove(playerID);
  // closeClientSocket(this.clientSockets.get(playerID));
  // }

  /**
   * Closes one socket for client. And remove the clientSocket from the ArrayList
   * clientSockets.
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
    if (!clientSockets.remove(clientSocket)) {
      throw new IllegalArgumentException(
          "The given clientSocket to remove is not in the clientSockets arraylist in server!");
    }
    clientSocket.close();
  }

  // /**
  // * Returns the local address of this server socket as an InetAddress object.
  // *
  // * @return an InetAddress object that is the local address of this server
  // * socket.
  // */
  // public InetAddress getInetAddress() {
  // return this.serverSocket.getInetAddress();
  // }

  /**
   * Get clientSockets that contains all client sockets
   * 
   * @return this.clientSockets
   */
  public ArrayList<Socket> getClientSockets() {
    return this.clientSockets;
  }
}
