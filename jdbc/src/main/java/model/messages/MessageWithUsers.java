package model.messages;

import lombok.AllArgsConstructor;
import lombok.Value;
import model.User;

import java.util.Collection;

@Value
@AllArgsConstructor
public class MessageWithUsers {
    String roleForRequester;
    Collection<User> users;
}
