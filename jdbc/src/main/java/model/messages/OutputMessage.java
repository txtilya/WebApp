package model.messages;


import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
public class OutputMessage {
    String type;
    String content;
    String conferenceId;
    int messageId;
    int readied;
    String creator;
    Timestamp timestamp;

    public String getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public String getConferenceId() {
        return this.conferenceId;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public int getReadied() {
        return this.readied;
    }

    public String getCreator() {
        return this.creator;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof OutputMessage)) return false;
        final OutputMessage other = (OutputMessage) o;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        final Object this$conferenceId = this.getConferenceId();
        final Object other$conferenceId = other.getConferenceId();
        if (this$conferenceId == null ? other$conferenceId != null : !this$conferenceId.equals(other$conferenceId))
            return false;
        if (this.getMessageId() != other.getMessageId()) return false;
        if (this.getReadied() != other.getReadied()) return false;
        final Object this$creator = this.getCreator();
        final Object other$creator = other.getCreator();
        if (this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) return false;
        final Object this$timestamp = this.getTimestamp();
        final Object other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        final Object $conferenceId = this.getConferenceId();
        result = result * PRIME + ($conferenceId == null ? 43 : $conferenceId.hashCode());
        result = result * PRIME + this.getMessageId();
        result = result * PRIME + this.getReadied();
        final Object $creator = this.getCreator();
        result = result * PRIME + ($creator == null ? 43 : $creator.hashCode());
        final Object $timestamp = this.getTimestamp();
        result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
        return result;
    }

    public String toString() {
        return "model.messages.OutputMessage(type=" + this.getType() + ", content=" + this.getContent() + ", conferenceId=" + this.getConferenceId() + ", messageId=" + this.getMessageId() + ", readied=" + this.getReadied() + ", creator=" + this.getCreator() + ", timestamp=" + this.getTimestamp() + ")";
    }
}