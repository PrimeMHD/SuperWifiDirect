package hiveconnect.com.superwifidirect.Bean;

public class Event_TaskToFragment {
    public enum TransEvent{START,DOING,FINISH};
    private TransEvent transEvent;

    public Event_TaskToFragment(TransEvent transEvent) {
        this.transEvent = transEvent;
    }

    public TransEvent getTransEvent() {
        return transEvent;
    }
}
