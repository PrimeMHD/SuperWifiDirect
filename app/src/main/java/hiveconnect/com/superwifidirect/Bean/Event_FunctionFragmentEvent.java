package hiveconnect.com.superwifidirect.Bean;

public class Event_FunctionFragmentEvent {
    public enum ConcreteEvent{wifiP2pEnabled,onConnectionInfoAvailable,onDisconnection,onSelfDeviceAvailable,onPeersAvailable};
    private ConcreteEvent mConcreteEvent;


    public Event_FunctionFragmentEvent(ConcreteEvent mConcreteEvent) {
        this.mConcreteEvent = mConcreteEvent;
    }

    public ConcreteEvent getmConcreteEvent() {
        return mConcreteEvent;
    }

    public void setmConcreteEvent(ConcreteEvent mConcreteEvent) {
        this.mConcreteEvent = mConcreteEvent;
    }
}
