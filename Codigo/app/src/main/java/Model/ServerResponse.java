package Model;

import java.io.Serializable;


public class ServerResponse implements Serializable {

    private String token;
    private boolean success;
    private String env;
    private String token_refresh;
    private Event event;


    public ServerResponse(String token, boolean success, String env,String msg, String t, String s, String d) {
        this.token = token;
        this.success = success;
        this.env = env;
        this.event = new Event(t, s, d);
    }

    public  String getToken_refresh() { return token_refresh; }

    public String getToken() {
        return token;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getEventType() { return event.getType_events(); }


}

