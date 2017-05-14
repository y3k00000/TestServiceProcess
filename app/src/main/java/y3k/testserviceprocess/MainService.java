package y3k.testserviceprocess;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {
    Handler incomingMessageHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj instanceof Bundle && ((Bundle) msg.obj).containsKey("message")) {
                String message = ((Bundle) msg.obj).getString("message");
                if (message != null) {
                    Toast.makeText(MainService.this, message, Toast.LENGTH_SHORT).show();
                    switch (message) {
                        case "Boom": {
                            if (msg.replyTo != null) {
                                Bundle replyBundle = new Bundle();
                                replyBundle.putString("message", "Going to booooom!!");
                                try {
                                    msg.replyTo.send(Message.obtain(incomingMessageHandler, 0, replyBundle));
                                } catch (RemoteException e) {
                                    Log.d(MainService.class.getName(), "incomingMessageHandler.Callback.handleMessage()", e);
                                }
                            } else {
                                Log.d(MainService.class.getName(), "msg.replyTo==null");
                            }
                            MainService.this.boomSelf(msg.replyTo);
                        }
                    }
                }
            }
            return true;
        }
    });

    void boomSelf(final Messenger replyToMessenger) {
        final Handler boomHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(replyToMessenger!=null) {
                        Message notifyMessage = new Message();
                        notifyMessage.what = 0;
                        Bundle notifyBundle = new Bundle();
                        notifyBundle.putString("message", "Countdown " + (5 - i));
                        notifyMessage.obj = notifyBundle;
                        try {
                            replyToMessenger.send(notifyMessage);
                        } catch (RemoteException e) {
                            Log.d(MainService.class.getName(), "MainService.boomSelf", e);
                        }
                    }
                }
                boomHandler.post(new Runnable() {
                    @Override
                    @SuppressWarnings("ConstantConditions")
                    public void run() {
                        // 只是要炸而已。
                        ((String) null).notify();
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
