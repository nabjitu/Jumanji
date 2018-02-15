package dk.itu.percomp17.jumanji;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import dk.itu.percomp17.jumanji.services.conversationDetection.ConversationEvent;

public class UserProfile implements Serializable {
    String userName;
    String AzProfileID;
    String password;

    HashMap<Date, ArrayList<ConversationEvent>> conversations;

    public UserProfile(String username, String AzProfileID, String passsword){
        this.userName = username;
        this.AzProfileID = AzProfileID;
        this.password = passsword;
    }

    public String getUSername(){
        return userName;
    }

    public String getAzProfileID(){
        return AzProfileID;
    }

    public String getPassword(){
        return password;
    }

    /**
     * TODO: make sure that the Dates that are added follows the format DD-MM-YY
     * @param date
     * @param conversation
     */
    public void addConversation(Date date, ConversationEvent conversation) {
        if (conversations.containsKey(date)) {
            conversations.get(date).add(conversation);
        } else {
            ArrayList<ConversationEvent> convo = new ArrayList<>();
            convo.add(conversation);
            conversations.put(date, convo);
        }
    }

    public ArrayList<ConversationEvent> getConversation(Date date) {
        return conversations.get(date);
    }

    public HashMap<Date, ArrayList<ConversationEvent>> getConversations() {
        return this.conversations;
    }

    public void setUserName(String name){
        this.userName = name;
    }

    public void setAzProfileID(String Az){
        this.AzProfileID = Az;
    }

    public void setPassword(String pass){
        this.password = pass;
    }

}
