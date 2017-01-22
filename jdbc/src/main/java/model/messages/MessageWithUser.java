package model.messages;


import lombok.AllArgsConstructor;
import lombok.Value;
import model.User;

@Value
@AllArgsConstructor
public class MessageWithUser {
    String type;
    User user;
}
