package Client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

@ClientEndpoint
public class TextWebSocketClient {

    private Session session;

    private String uri = ApiClass.TEXT_WEB_SOCKET;

    private Long clientUserId;

    public TextWebSocketClient(Long clientUserId) throws IOException, DeploymentException {
        this.clientUserId = clientUserId;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, URI.create(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        initSession();
        System.out.println("Connected to server");
    }

    public void initSession() {

        try {
            if (Objects.isNull(session)) {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, URI.create(uri));
            }
            // khai báo session này thì tương ứng với user id nào để be lưu lại
            MessageDto messageDto = new MessageDto(clientUserId, null, null, ContentType.INIT_SESSION);
            String initSessionMessage = (new ObjectMapper()).writeValueAsString(messageDto);
            sendMessage(initSessionMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Session closed: " + closeReason);
    }

    public void sendMessage(String message, Long conversationId, ContentType contentType) {
        try {
            MessageDto messageDto = new MessageDto(this.clientUserId, message, conversationId, contentType);
            String strMessageDto = (new ObjectMapper()).writeValueAsString(messageDto);
            System.out.println("strMessageDto: "+ strMessageDto);
            this.session.getBasicRemote().sendText(strMessageDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, DeploymentException {
        TextWebSocketClient textClient = new TextWebSocketClient(1L);
        if (Objects.isNull(textClient.session))
            textClient.initSession();
        textClient.sendMessage("text client message1", 1L, ContentType.TEXT);
    }

    public static class MessageDto implements Serializable {

        public MessageDto(Long senderId, String content, Long conversationId, ContentType contentType) {
            this.senderId = senderId;
            this.content = content;
            this.conversationId = conversationId;
            this.contentType = contentType;
        }

        public MessageDto() {
        }

        Long senderId;

        String content;

        Long conversationId;

        ContentType contentType;

        public void setSenderId(Long senderId){ this.senderId = senderId; }
        public Long getSenderId() { return this.senderId; }

        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public Long getConversationId() { return this.conversationId; }

        public void setContent(String content) { this.content = content; }
        public String getContent() { return this.content; }

        public void setContentType(ContentType contentType) { this.contentType = contentType; }
        public ContentType getContentType() { return this.contentType; }
    }

    public enum ContentType {
        INIT_SESSION,
        ADD_PARTICIPANT,
        REMOVE_PARTICIPANT,
        TEXT,
        DOCUMENT,
        IMAGE,
        VIDEO
    }

}
