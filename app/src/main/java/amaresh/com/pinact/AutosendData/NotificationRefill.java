package amaresh.com.pinact.AutosendData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jagat on 2/22/2017.
 */

public class NotificationRefill extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, AutoStartUpdate.class);
            context.startService(serviceIntent);
        }
    }
}




