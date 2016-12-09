package websocket.chat;

import lombok.extern.java.Log;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@Log
public class ServletAwareConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        log.info(request.getUserPrincipal().getName());
        httpSession.setAttribute("username", request.getUserPrincipal().getName());
        config.getUserProperties().put("httpSession", httpSession);
    }

}