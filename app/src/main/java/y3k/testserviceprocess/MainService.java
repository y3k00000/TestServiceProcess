package y3k.testserviceprocess;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MainService extends Service {
    Handler incomingMessageHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj instanceof Bundle && ((Bundle) msg.obj).containsKey("message")) {
                String message = ((Bundle) msg.obj).getString("message");
                Toast.makeText(MainService.this, message, Toast.LENGTH_SHORT).show();
                switch (message) {
                    case "Boom": {
                        MainService.this.boomSelf();
                    }
                }
            }
            return true;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    void boomSelf() {
        final Handler boomHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boomHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        boomHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Object bomb = null;
                                bomb.toString();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(incomingMessageHandler).getBinder();
    }
}
