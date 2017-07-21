package y3k.testserviceprocess

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    // Test
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById(R.id.button_bind).setOnClickListener(object : View.OnClickListener {
            internal var firstOnclickListener: View.OnClickListener = this
            override fun onClick(v: View) {
                val bindingDialog = ProgressDialog.show(v.context, "Bind", "Binding...", true, false)
                // 開始Bind Service動作。
                this@MainActivity.bindService(Intent(this@MainActivity, MainService::class.java), object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName, service: IBinder) {
                        // Bind完成後，關閉ProgressDialog，改變Button的行為。
                        bindingDialog.dismiss()
                        (findViewById(R.id.text_message) as TextView).text = String.format(java.util.Locale.US, "%s %s %s", "Service", name.toShortString(), "bound!!")
                        findViewById(R.id.button_bind).setOnClickListener { v ->
                            // 送Message去叫Service在他的MainThread自殺。
                            val message = Message()
                            message.what = 0
                            message.obj = Bundle()
                            message.replyTo = Messenger(Handler(android.os.Handler.Callback { msg ->
                                Log.d(MainActivity::class.java.name, msg.toString())
                                if (msg.obj != null && msg.obj is Bundle && (msg.obj as Bundle).containsKey("message")) {
                                    val receiveMessage = (msg.obj as Bundle).getString("message")
                                    Log.d(MainActivity::class.java.name, "receiveMessage = " + receiveMessage!!)
                                    (findViewById(R.id.text_message) as TextView).text = receiveMessage
                                }
                                true
                            }))
                            (message.obj as Bundle).putSerializable("message", MainCommand(MainCommand.ACTION.BOOM, 7, true))
                            try {
                                Messenger(service).send(message)
                            } catch (e: RemoteException) {
                                e.printStackTrace()
                                Toast.makeText(v.context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onServiceDisconnected(name: ComponentName) {
                        // ServiceDisconnect時，把button行為設回原本的。
                        Log.d(MainActivity::class.java.name, "onServiceDisconnected(" + name.toString() + ")")
                        findViewById(R.id.button_bind).setOnClickListener(firstOnclickListener)
                    }
                }, Context.BIND_AUTO_CREATE)
            }
        })
    }
}
