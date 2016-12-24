package websocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@ServerEndpoint("/echo")
public class WebSocketDemo {

    private static Set<Session> userSessions = Collections.newSetFromMap(
            new ConcurrentHashMap<Session, Boolean>());

    @OnOpen
    public void onOpen(Session userSession) {
        userSessions.add(userSession);
        userSession.getAsyncRemote().sendText("Hi!");
    }

    @OnClose
    public void onClose(Session userSession) {
        userSessions.remove(userSession);
    }

    @OnMessage
    public void onMessage(String message) {
        broadcast("Все говорят \"" + message + "\", а ты купи слона!");
    }

    @SuppressWarnings("WeakerAccess")
    public static void broadcast(String msg) {
        userSessions.stream()
                .map(Session::getAsyncRemote)
                .forEach(async -> async.sendText(msg));
    }

//    @OnMessage
//    public String echoTextMessage(String msg) {
//        return msg + " from WebSocket!";
//    }
}
