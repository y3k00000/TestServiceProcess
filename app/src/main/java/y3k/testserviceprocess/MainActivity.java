package y3k.testserviceprocess;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_bind).setOnClickListener(new View.OnClickListener() {
            View.OnClickListener firstOnclickListener = this;
            @Override
            public void onClick(View v) {
                final ProgressDialog bindingDialog = ProgressDialog.show(v.getContext(), "Bind", "Binding...",true,false);
                MainActivity.this.bindService(new Intent(MainActivity.this, MainService.class), new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, final IBinder service) {
                        bindingDialog.dismiss();
                        findViewById(R.id.button_bind).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Message message = new Message();
                                message.what = 0;
                                message.obj = new Bundle();
                                ((Bundle)message.obj).putString("message","Boom");
                                try {
                                    new Messenger(service).send(message);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.d(MainActivity.class.getName(), "onServiceDisconnected("+name.toString()+")");
                        findViewById(R.id.button_bind).setOnClickListener(firstOnclickListener);
                    }
                }, Context.BIND_AUTO_CREATE);
            }
        });
    }
}
