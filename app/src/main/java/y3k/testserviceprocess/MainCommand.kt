package y3k.testserviceprocess

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class MainCommand : Serializable {
    enum class ACTION {
        BOOM
    }

    internal val action: ACTION
    internal val countdown: Int
    internal val shouldCallback: Boolean

    internal constructor(action: ACTION, countdown: Int, shouldCallback: Boolean) {
        this.action = action
        this.countdown = countdown
        this.shouldCallback = shouldCallback
    }

    @Throws(JSONException::class)
    internal constructor(jsonObject: JSONObject) {
        this.action = ACTION.valueOf(jsonObject.getString(JSON_TAG_ACTION))
        this.countdown = jsonObject.optInt(JSON_TAG_COUNTDOWN, DEFAULT_COUNTDOWN)
        this.shouldCallback = jsonObject.optBoolean(JSON_TAG_SHOULD_SEND_MESSAGE, true)
    }

    fun toJSONObject(): JSONObject {
        try {
            return JSONObject()
                    .put(JSON_TAG_ACTION, this.action)
                    .put(JSON_TAG_COUNTDOWN, this.countdown)
                    .put(JSON_TAG_SHOULD_SEND_MESSAGE, this.shouldCallback)
        } catch (e: JSONException) {
            e.printStackTrace()
            return JSONObject()
        }

    }

    companion object {
        private val JSON_TAG_ACTION = "action"
        private val JSON_TAG_COUNTDOWN = "countdown"
        private val JSON_TAG_SHOULD_SEND_MESSAGE = "should_send_message"
        private val DEFAULT_COUNTDOWN = 5
    }
}
