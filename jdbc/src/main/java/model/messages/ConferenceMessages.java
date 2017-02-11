package model.messages;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Collection;

@Value
@AllArgsConstructor
public class ConferenceMessages {
    String type;
    Collection<OutputMessage> messages;
}
