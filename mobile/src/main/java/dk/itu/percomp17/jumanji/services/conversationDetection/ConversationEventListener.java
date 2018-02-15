package dk.itu.percomp17.jumanji.services.conversationDetection;

public interface ConversationEventListener {

    /**
     * Is called when an ConversationEvent has taken place.
     * @see ConversationDetectionService
     * @param event
     */
    void onConversationEvent(ConversationEvent event);
}
