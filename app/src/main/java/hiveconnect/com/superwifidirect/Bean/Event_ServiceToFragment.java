package hiveconnect.com.superwifidirect.Bean;

public class Event_ServiceToFragment {
    public enum TransEvent{START,DOING,FINISH};
    private TransEvent transEvent;
    private String receivedString;
    public Event_ServiceToFragment(TransEvent transEvent) {
        this.transEvent = transEvent;
    }

    public Event_ServiceToFragment(TransEvent transEvent, String receivedString) {
        this.transEvent = transEvent;
        this.receivedString = receivedString;
    }

    public String getReceivedString() {
        return receivedString;
    }

    public TransEvent getTransEvent() {
        return transEvent;
    }
}
