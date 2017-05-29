package y3k.testserviceprocess

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import kotlin.concurrent.thread

class MainService : Service() {
    var incomingMessageHandlerCallback = Handler.Callback { msg: Message ->
        if (msg.obj is Bundle && (msg.obj as Bundle).containsKey("message")) {
            val message: MainCommand = (msg.obj as Bundle).getSerializable("message") as MainCommand
            when (message.action) {
                MainCommand.ACTION.BOOM -> {
                    if (msg.replyTo != null && message.shouldCallback) {
                        val replyBundle: Bundle = Bundle()
                        replyBundle.putString("message", "Going to boom!!")
                        try {
                            msg.replyTo.send(Message.obtain(incomingMessageHandler, 0, replyBundle))
                        } catch(e: RemoteException) {
                            Log.d(MainService::class.java.simpleName, "incomingMessageHandler.Callback.handleMessage()", e)
                        }
                    }
                    boomSelf(msg.replyTo, message)
                }
            }
        }
        true
    }

    var incomingMessageHandler: Handler = Handler(incomingMessageHandlerCallback)

    override fun onBind(intent: Intent?): IBinder {
        return Messenger(incomingMessageHandler).binder
    }

    fun boomSelf(replyToMessenger: Messenger?, message: MainCommand) {
        val boomHandler: Handler = Handler(Looper.getMainLooper())
        thread {
            for (i in 0..message.countdown) {
                Thread.sleep(1000)
                val notifyMessage: Message = Message()
                notifyMessage.what = 0
                val notifyBundle: Bundle = Bundle()
                notifyBundle.putString("message", "Countdown " + (message.countdown - i))
                notifyMessage.obj = notifyBundle
                replyToMessenger?.send(notifyMessage)
            }
            boomHandler.post {
                (null as String).toString()
            }
        }
    }
}