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
                if(message!=null) {
                    Toast.makeText(MainService.this, message, Toast.LENGTH_SHORT).show();
                    switch (message) {
                        case "Boom": {
                            MainService.this.boomSelf();
                        }
                    }
                }
            }
            return true;
        }
    });

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
                    @Override @SuppressWarnings("ConstantConditions")
                    public void run() {
                        // 只是要炸而已。
                        ((String)null).notify();
                    }
                });
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 因為兩邊不同Process，要用這個做法才能實現溝通。
        // 部分人習慣的在Binder內加入.getService()直接跟Service物件溝通的作法不能使用，可自行實驗看看。>.^
        return new Messenger(incomingMessageHandler).getBinder();
    }
}
