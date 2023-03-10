package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;
import edu.duke.ece651.team6.shared.PlayerMapInfo;

public class HumanPlayer implements Player {
    private final Client client;

    public HumanPlayer() throws UnknownHostException, IOException {
        this.client = new Client("127.0.0.1", 12345);
    }
    @Override
    public boolean init() throws IOException, ClassNotFoundException {
        Object o = client.recvObject();
        PlayerMapInfo playerMapInfo = (PlayerMapInfo)o;
        MapTextView mtv = new MapTextView(playerMapInfo);
        System.out.println(mtv.display());
        return true;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void placeUnit() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'placeUnit'");
    }

    @Override
    public void playOneTurn() {
        // PlayerMapInfo playerMapInfo = client.recv();
        // MapView mapView = new MapTextView(playerMapInfo);
        // mapView.display();
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'playOneTurn'");
    }
    
    public static void main(String[] args) throws IOException, UnknownHostException, ClassNotFoundException {
        HumanPlayer hp = new HumanPlayer();
        hp.init();
    }
}
