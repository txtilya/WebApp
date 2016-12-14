/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package websocket.chat;

import dao.UserDao;
import dao.UserInConferenceDao;
import lombok.extern.java.Log;
import model.User;
import util.HTMLFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static listeners.Initer.USER_DAO;
import static listeners.Initer.USER_IN_CONFERENCE_DAO;

@Log
@ServerEndpoint(value = "/websocket/chat/{room}", configurator = ServletAwareConfig.class)
public class ChatAnnotation {
    private EndpointConfig config;


    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<ChatAnnotation> connections =
            new CopyOnWriteArraySet<>();

    private final String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session, EndpointConfig config) {
        this.config = config;
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        ServletContext servletContext = httpSession.getServletContext();

        UserInConferenceDao userInConferenceDao =
                (UserInConferenceDao) servletContext.getAttribute(USER_IN_CONFERENCE_DAO);
        UserDao userDao =
                (UserDao) servletContext.getAttribute(USER_DAO);

        String username = (String) httpSession.getAttribute("username");
        Optional<User> user = userDao.getByEmail(username);
        int userId = user.get().getId();
        log.info(String.valueOf(userId));

        int room = Integer.parseInt((String) httpSession.getAttribute("room"));
        log.info(String.valueOf(room));

        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);

        if (userInConferenceDao.isPresent(userId, room)) {
            log.info("User in conference");
            broadcast("User in conference");
        } else {
            log.info("User not in conference");
            broadcast("User not in conference");
        }
    }

    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }


    @OnMessage
    public void incoming(String message) {
        // Never trust the client
        String filteredMessage = String.format("%s: %s",
                nickname, HTMLFilter.filter(message.toString()));
        broadcast(filteredMessage);
    }


    @OnError
    public void onError(Throwable t) throws Throwable {
        log.info("Chat Error: " + t.toString());
    }


    private static void broadcast(String msg) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.info("Chat Error: Failed to send message to client");
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}
