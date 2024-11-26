package Client;


import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ImageWebSocketClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
        String initSessionMessage =
                "{\n" +
                "    \"senderId\":2,\n" +
                "    \"contentType\": \"INIT_SESSION\"\n" +
                "}"
        sendMessage("Hello, WebSocket!");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnMessage
    public void onBinaryMessage(byte[] message) {
        System.out.println("Received binary message of length: " + message.length);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Session closed: " + closeReason);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://127.0.0.1:61562/user";
        try {
            container.connectToServer(ImageWebSocketClient.class, URI.create(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
