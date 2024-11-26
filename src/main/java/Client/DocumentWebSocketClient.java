package Client;

import javax.websocket.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ClientEndpoint
public class DocumentWebSocketClient {

    private Session session;

    private URI uri = URI.create(ApiClass.DOCUMENT_WEB_SOCKET);

    public DocumentWebSocketClient() throws IOException, DeploymentException {
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

    public void sendDocumentMessage(ByteBuffer imageData) {
        try {
            session.getBasicRemote().sendBinary(imageData);
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            DocumentWebSocketClient client = new DocumentWebSocketClient();
            client.sendDocumentMessage(getImageByteBuffer("/Users/quannguyen/Desktop/scheduledReport.pptx"));
    }

    public static ByteBuffer getImageByteBuffer(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        Path path = imageFile.toPath();

        byte[] fileNameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        byte[] fileContent = Files.readAllBytes(path);

        //2 additional space for header
        ByteBuffer buffer = ByteBuffer.allocate(2 + fileNameBytes.length + fileContent.length);
        // Set the operation code for binary message (0x82)
        buffer.put((byte) 0x82);

        // Set the payload length for file name
        if (fileNameBytes.length <= 125) {
            buffer.put((byte) fileNameBytes.length);
        } else if (fileNameBytes.length <= 65535) {
            buffer.put((byte) 126);
            buffer.putShort((short) fileNameBytes.length);
        } else {
            buffer.put((byte) 127);
            buffer.putLong(fileNameBytes.length);
        }

        buffer.put(fileNameBytes);
        buffer.put(fileContent);
        buffer.flip();// Flip the buffer for reading

        return buffer;
    }
}
