package edu.duke.ece651.team6.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.junit.jupiter.api.Test;

public class GameReactorTest {

    @Test
    public void testMain() throws IOException {
        GameReactor gr = new GameReactor(6667, 10);
        Thread t = new Thread(new RunTheGame(gr));
        t.start();
        Socket s1 = new Socket("localhost", 6667);
        OutputStream out1 = s1.getOutputStream();
        ObjectOutputStream objectOut1 = new ObjectOutputStream(out1);
        objectOut1.writeObject("a 2");
        Socket s2 = new Socket("localhost", 6667);
        OutputStream out2 = s2.getOutputStream();
        ObjectOutputStream objectOut2 = new ObjectOutputStream(out2);
        objectOut2.writeObject("b 2");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Socket s3 = new Socket("localhost", 6667);
        OutputStream out3 = s3.getOutputStream();
        ObjectOutputStream objectOut3 = new ObjectOutputStream(out3);
        objectOut3.writeObject("a");
        s1.close();
        Socket s4 = new Socket("localhost", 6667);
        OutputStream out4 = s4.getOutputStream();
        ObjectOutputStream objectOut4 = new ObjectOutputStream(out4);
        objectOut4.writeObject("a");
        s2.close();
        s3.close();
        s4.close();
        gr.close();
    }

    @Test
    public void testMain2() throws IOException {
        GameReactor gr = new GameReactor(6666, 10);
        Thread t = new Thread(new RunTheGame(gr));
        t.start();
        Socket s1 = new Socket("localhost", 6666);
        OutputStream out1 = s1.getOutputStream();
        ObjectOutputStream objectOut1 = new ObjectOutputStream(out1);
        objectOut1.writeObject("2");
        s1.close();
        Socket s2 = new Socket("localhost", 6666);
        OutputStream out2 = s2.getOutputStream();
        ObjectOutputStream objectOut2 = new ObjectOutputStream(out2);
        objectOut2.writeObject("2");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s2.close();
        gr.close();
    }

    private class RunTheGame implements Runnable {

        GameReactor gr;

        public RunTheGame(GameReactor gr) {
            this.gr = gr;
        }

        @Override
        public void run() {
            try {
                gr.run();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
}
