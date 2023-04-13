package edu.duke.ece651.team6.client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

class AppTest {

  // class InnerAppTest {
  // Client getClient() throws IOException, UnknownHostException {
  // return new Client("localhost", 12345);
  // }

  // InputStreamReader getInputStreamReader() {
  // return new InputStreamReader(System.in);
  // }

  // BufferedReader getBufferedReader() {
  // return new BufferedReader(new InputStreamReader(System.in));
  // }

  // TextPlayer getTextPlayer() throws IOException, ClassNotFoundException {
  // return new TextPlayer(client, input, System.out, setting);
  // }
  // }

  // Client client = mock(Client.class);
  // TextPlayer textPlayer = mock(TextPlayer.class);
  // GameBasicSetting setting = mock(GameBasicSetting.class);
  // GlobalMapInfo global = mock(GlobalMapInfo.class);
  // Result result = mock(Result.class);
  // BufferedReader input = mock(BufferedReader.class);

  // @BeforeEach
  // public void setUp() throws IOException, ClassNotFoundException {
  // when(this.client.recvObject()).thenReturn("hello");
  // when(this.client.recvGlobalMapInfo()).thenReturn(global);
  // when(this.client.recvGameResult()).thenReturn(result);
  // when(this.client.recvGameBasicSetting()).thenReturn(setting);
  // when(this.textPlayer.playerId).thenReturn(1);
  // when(setting.getNumPlayers()).thenReturn(3);
  // when(setting.getRemainingNumUnits()).thenReturn(10);
  // when(setting.getAssignedTerritories()).thenReturn(new HashSet<Territory>(){
  // {
  // add(new Territory("t1", 1, 10));
  // }
  // });
  // // String inputString = "1\n2\n3\n";
  // // InputStream inputStream = new
  // ByteArrayInputStream(inputString.getBytes());
  // // input = new BufferedReader(new InputStreamReader(inputStream));
  // }

  @Test
  public void test_app() throws IOException, UnknownHostException, ClassNotFoundException {
  }
}
