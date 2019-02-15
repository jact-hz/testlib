package common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class PushNotificationReceiver extends BroadcastReceiver {  //广播！！！！！！！！！

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            receivePushNotification(null);
            return;
        }
        PushData datas =
                (PushData) intent.getSerializableExtra(GlobalConstants.PUSH_CONTENT_KEY);  //接收到刷新广播时获得刷新的数据
        receivePushNotification(datas);
    }

    public abstract void receivePushNotification(PushData datas);  //抽象方法~

}
