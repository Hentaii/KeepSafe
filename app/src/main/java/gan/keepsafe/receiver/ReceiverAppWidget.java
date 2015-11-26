package gan.keepsafe.receiver;


import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import gan.keepsafe.srv.SrvKillProcesWidget;

public class ReceiverAppWidget extends AppWidgetProvider {


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, SrvKillProcesWidget.class);
        context.startService(intent);
    }


    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, SrvKillProcesWidget.class);
        context.stopService(intent);
    }


}
