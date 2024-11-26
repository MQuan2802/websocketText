package Client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@ClientEndpoint
public class ImageWebSocketClient {

    private Session session;

    private URI uri = URI.create(ApiClass.DOCUMENT_WEB_SOCKET);

    public ImageWebSocketClient() throws IOException, DeploymentException {
        ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");

    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnMessage
    public void onBinaryMessage(byte[] message) {
        System.out.println("Received binary message of length: " + message.length);
    }

    public void initSession() {

        try {
            if (Objects.isNull(session)) {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, uri);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Session closed: " + closeReason);
    }

    public void sendImageMessage(ByteBuffer imageData) {
        try {
            session.getBasicRemote().sendBinary(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            ImageWebSocketClient client = new ImageWebSocketClient();
//            client.sendMessage("from send Message");
            client.sendImageMessage(getImageByteBuffer("/Users/quannguyen/Desktop/softwareArchitecture/webSocketImageHandler12.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ByteBuffer getImageByteBuffer(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        Path path = imageFile.toPath();

        // Read the image file into a byte array
        byte[] imageData = Files.readAllBytes(path);

        // Create a ByteBuffer from the byte array
        ByteBuffer byteBuffer = ByteBuffer.wrap(imageData);

        return byteBuffer;
    }

}
