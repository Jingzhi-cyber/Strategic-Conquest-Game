package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.team6.shared.SocketKey;

/**
 * This class is a wrapper class for server side socket that has a ServerSocket
 * and an ArrayList of Socket thatare connected to the ServerSocket. It builds a
 * server socket for the game's server and provides methods like "accept
 * connection from client socket", "send/receive stream to/from connected
 * client", "send/receive serializable obeject to/from connect client"
 */
public class Server {
  private List<SocketKey> clientSockets;

  private class RecvObjectThread implements Runnable {

    private int playerID;
    private SocketKey key;

    private List<Object> recvedObjects;

    public RecvObjectThread(int playerID, SocketKey key, List<Object> recvedObjects) {
      this.playerID = playerID;
      this.key = key;
      this.recvedObjects = recvedObjects;
    }

    @Override
    public void run() {
      try {
        Object object = recvObject(key);
        this.recvedObjects.set(playerID, object);
        sendObject(key, "Your commit has been received!");
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
        // maybe request the client to send again?
      }
    }
  }

  public Server(List<SocketKey> clientSockets) {
    this.clientSockets = clientSockets;
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
   * @param key is a Socket object for the client to receive one
   *                     serializable object from
   * @return a serializable object that is receive from the client socket.
   * @throws IOException            if an I/O error occurs when creating the input
   *                                stream, the socket is closed, the socket is
   *                                not connected, or the socket input has been
   *                                shutdown using shutdownInput()
   * @throws ClassNotFoundException if the class of a serialized object cannot be
   *                                found.
   */
  public Object recvObject(SocketKey key) throws IOException, ClassNotFoundException {
    AccountManager m = AccountManager.getInstance();
    Socket clientSocket = m.getSocket(key);
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
  public List<Object> recvObjectFromALL() {
    int playerNum = clientSockets.size();
    List<Object> recvedObjects = new ArrayList<>(playerNum);
    for (int i = 0; i < playerNum; i++) {
      recvedObjects.add(null);
    }
    List<Thread> recvThreads = new ArrayList<>(playerNum);
    for (int i = 0; i < playerNum; i++) {
      Thread recvThread = new Thread(new RecvObjectThread(i, clientSockets.get(i), recvedObjects));
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
   * @param key is a socket object for the client to send one
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
  public void sendObject(SocketKey key, Object object) throws IOException {
    AccountManager m = AccountManager.getInstance();
    Socket clientSocket = m.getSocket(key);
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
    for (SocketKey clientSocket : clientSockets) {
      sendObject(clientSocket, object);
    }
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
  public void closeClientSocket(SocketKey key) throws IOException {
    AccountManager m = AccountManager.getInstance();
    Socket clientSocket = m.getSocket(key);
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
  public List<SocketKey> getClientSockets() {
    return this.clientSockets;
  }
}
