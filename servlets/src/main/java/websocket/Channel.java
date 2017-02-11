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
package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.UserDao;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import model.User;
import model.messages.*;
import util.HTMLFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static listeners.Initer.USER_DAO;

@Log
@ServerEndpoint(value = "/websocket/{path}", configurator = ServletAwareConfig.class)
public class Channel {
    private Session session;

    private static final Set<Channel> connections =
            new CopyOnWriteArraySet<>();

    private UserDao userDao;

    @Getter
    private User connectionOwner;

    @OnOpen
    public void start(Session session, EndpointConfig config) {
        this.session = session;
//        session.getUserProperties().put("clientId", clientId);
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        ServletContext servletContext = httpSession.getServletContext();

        userDao = (UserDao) servletContext.getAttribute(USER_DAO);

        String username = (String) httpSession.getAttribute("username");
        connectionOwner = userDao.getByEmail(username).get();
        connections.add(this);

    }

    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                connectionOwner.getLogin(), "has disconnected.");
        log.info(message);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.info("Error: " + t.toString());
    }

    @OnMessage
    public void incoming(String message) {
        messageDispatcher(message);
    }


    @SneakyThrows
    private void messageDispatcher(String message) {

        ObjectMapper mapper = new ObjectMapper();
        Message m = mapper.readValue(message, Message.class);
        if (m.getType().equals("INFO")) message = "WORKS!!!";
        if (m.getType().equals("ChangeContent")) changeContent(m.getContent());
        if (m.getType().equals("LoadUserPage")) loadUserPage(m.getContent());
        if (m.getType().equals("getConferenceName")) loadConferenceName(m.getContent());
        if (m.getType().equals("ConferenceMessage")) conferenceMessage(message);
        if (m.getType().equals("SearchFriends")) searchFriends(m.getContent());
        if (m.getType().equals("getConferences")) getConferences();
        if (m.getType().equals("getMessages")) getMessages(m.getContent());

        String filteredMessage = String.format("%s: %s",
                connectionOwner.getLogin(), HTMLFilter.filter(message));
        log.info(message);
//        broadcast(filteredMessage);
//        sendToThis(filteredMessage);
    }

    private void getMessages(String conferenceId) {
        ArrayList<OutputMessage> m = new ArrayList<OutputMessage>
                (userDao.getConferenceMessagesById(Integer.parseInt(conferenceId)));
        m.sort(Comparator.comparing(OutputMessage::getMessageId));
        for (OutputMessage om : m) {
            String u = toJson(om);
            sendToThis(u);
            log.info(u);
        }

//        getMessageId()
    }

    private void getConferences() {
        MessageWithConferences m = new MessageWithConferences("getConferences",
                userDao.getConferences(connectionOwner));
        String u = toJson(m);
        sendToThis(u);
        log.info(u);

    }

    private void searchFriends(String idOrLogin) {
        MessageWithUsers m = new MessageWithUsers("notFriends",
                userDao.getUsersByIdOrLogin(connectionOwner, idOrLogin));
        String u = toJson(m);
        sendToThis(u);
        log.info(u);

    }


    @SneakyThrows
    private void conferenceMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ConferenceMessage m = mapper.readValue(message, ConferenceMessage.class);
        int messageId = userDao.createMessageAndAddToConference(connectionOwner, m);
        if (messageId != 0) {
            OutputMessage o = new OutputMessage(m.getType(), m.getContent(), m.getConferenceId(),
                    messageId, 0, connectionOwner.getLogin(), new Timestamp(new Date().getTime()));
            Collection<Integer> usersIds = userDao.getUsersIdsFromConference(Integer.parseInt(o.getConferenceId()));
            sendMessageToUsers(usersIds, o);
//            createNotification();
        }
    }

    private void sendMessageToUsers(Collection<Integer> usersIds, OutputMessage o) {
        for (int userId : usersIds) {
            for (Channel client : connections) {
                if (userId == client.getConnectionOwner().getId()) {
                    sendToClient(toJson(o), client);
                }
            }
        }
    }

    @SneakyThrows
    private String toJson(Object o) {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(o);
    }


    @SneakyThrows
    private void loadUserPage(String content) {
//        Collection<User> users = userDao.getAll();
        User user = userDao.getById(Long.parseLong(content)).get();
        int requestedUserId = user.getId();
        int ownersId = connectionOwner.getId();
        String type;
        if (requestedUserId == ownersId) type = "LoadMyPage";
        else {
            if (userDao.isUsersFriends(requestedUserId, ownersId)) type = "LoadUserPageFriends";
            else type = "LoadUserPageNotFriends";
        }

        MessageWithUser outMessage = new MessageWithUser(type, user);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String arrayToJson = objectMapper.writeValueAsString(outMessage);
        sendToThis(arrayToJson);
        log.info(arrayToJson);
    }


    private void loadConferenceName(String content) {
        int ownersId = connectionOwner.getId();
        int conferenceId = Integer.parseInt(content);
        if (userDao.isUserInConference(ownersId, conferenceId)) {
            sendToThis(toJson(new Message("getConferenceName", userDao.getConferenceNameById(conferenceId))));
        }
    }

    private void changeContent(String content) {
        if (content.equals("friends")) getFriends();
        if (content.equals("requests")) getRequests();

    }

    private void getRequests() {
        MessageWithUsers m = new MessageWithUsers("requests",
                userDao.getFriendsRequests(connectionOwner));
        String u = toJson(m);
        sendToThis(u);
        log.info(u);
    }

    private void getFriends() {
        MessageWithUsers m = new MessageWithUsers("friends",
                userDao.getFriends(connectionOwner));
        String u = toJson(m);
        sendToThis(u);
        log.info(u);
    }


    private void sendToClient(String msg, Channel channel) {
        synchronized (channel) {
            try {
                channel.session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                log.info("Chat Error: Failed to send message to client");
                connections.remove(channel);
                try {
                    channel.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        channel.connectionOwner.getLogin(), "has been disconnected.");
                log.info(message);
            }
        }
    }

    private void sendToThis(String msg) {
        sendToClient(msg, this);
    }

    private void broadcast(String msg) {
        for (Channel client : connections) {
            sendToClient(msg, client);
        }
    }
}
