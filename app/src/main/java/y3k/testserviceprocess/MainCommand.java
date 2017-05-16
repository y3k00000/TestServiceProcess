package y3k.testserviceprocess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MainCommand implements Serializable{
    private final static String JSON_TAG_ACTION = "action";
    private final static String JSON_TAG_COUNTDOWN = "countdown";
    private final static String JSON_TAG_SHOULD_SEND_MESSAGE = "should_send_message";
    private final static int DEFAULT_COUNTDOWN = 5;
    public enum ACTION{
        BOOM
    }
    final ACTION action;
    final int countdown;
    final boolean shouldCallback;
    MainCommand(ACTION action, int countdown, boolean shouldCallback){
        this.action = action;
        this.countdown = countdown;
        this.shouldCallback = shouldCallback;
    }
    MainCommand(JSONObject jsonObject) throws JSONException{
        this.action = ACTION.valueOf(jsonObject.getString(JSON_TAG_ACTION));
        this.countdown = jsonObject.optInt(JSON_TAG_COUNTDOWN,DEFAULT_COUNTDOWN);
        this.shouldCallback = jsonObject.optBoolean(JSON_TAG_SHOULD_SEND_MESSAGE,true);
    }
    public JSONObject toJSONObject(){
        try {
            return new JSONObject()
                    .put(JSON_TAG_ACTION,this.action)
                    .put(JSON_TAG_COUNTDOWN,this.countdown)
                    .put(JSON_TAG_SHOULD_SEND_MESSAGE,this.shouldCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
