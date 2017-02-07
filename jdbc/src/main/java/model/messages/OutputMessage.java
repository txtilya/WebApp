package model.messages;


import lombok.AllArgsConstructor;
import lombok.Value;

import java.sql.Timestamp;

@Value
@AllArgsConstructor
public class OutputMessage {
    String type;
    String content;
    String conferenceId;
    int messageId;
    int readied;
    String creator;
    Timestamp timestamp;
}