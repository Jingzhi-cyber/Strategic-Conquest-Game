package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;
import edu.duke.ece651.team6.shared.GlobalMapInfo;

public class HumanPlayer implements Player {
    private final Client client;

    public HumanPlayer() throws UnknownHostException, IOException {
        this.client = new Client("127.0.0.1", 12345);
    }
    @Override
    public boolean init() throws IOException, ClassNotFoundException {
        Object o = client.recvObject();
        GlobalMapInfo globalMapInfo = (GlobalMapInfo)o;
        MapTextView mtv = new MapTextView(globalMapInfo);
        System.out.println(mtv.display());
        return true;
    }

    @Override
    public void placeUnit() {
    }

    @Override
    public void playOneTurn() {
    }
    
    public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
        HumanPlayer hp = new HumanPlayer();
        hp.init();
    }
}
