package model.messages;


import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ConferenceMessage {
    String type;
    String content;
    String conferenceId;
}