package dk.itu.percomp17.jumanji.services.conversationDetection;

import java.util.Date;

/**
 * A class repreenting a conversation the user have had.
 * This class stores a date for the conversation and a length.
 */
public class ConversationEvent {

    Date date;
    double length;

    ConversationEvent(Date date, double length) {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
