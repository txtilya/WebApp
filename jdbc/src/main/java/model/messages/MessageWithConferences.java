package model.messages;

import lombok.AllArgsConstructor;
import lombok.Value;
import model.Conference;

import java.util.Collection;

@Value
@AllArgsConstructor
public class MessageWithConferences {
    String type;
    Collection<Conference> conferences;
}
