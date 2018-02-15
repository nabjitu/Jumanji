package dk.itu.percomp17.jumanji.services.conversationDetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(ServiceRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oh no!");

        context.startService(new Intent(context, ConversationDetectionService.class));
    }
}