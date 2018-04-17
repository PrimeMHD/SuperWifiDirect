package hiveconnect.com.superwifidirect.Bean;

public class Event_ServiceToFragment {
    public enum TransEvent{START,DOING,FINISH};
    private TransEvent transEvent;

    public Event_ServiceToFragment(TransEvent transEvent) {
        this.transEvent = transEvent;
    }

    public TransEvent getTransEvent() {
        return transEvent;
    }
}
