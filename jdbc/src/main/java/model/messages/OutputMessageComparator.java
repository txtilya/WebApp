package model.messages;

import java.util.Comparator;

public class OutputMessageComparator implements Comparator<OutputMessage> {

    @Override
    public int compare(OutputMessage o1, OutputMessage o2) {
        return o1.getMessageId() < o2.getMessageId() ? -1 : o1.getMessageId() == o2.getMessageId() ? 0 : 1;
    }
}
