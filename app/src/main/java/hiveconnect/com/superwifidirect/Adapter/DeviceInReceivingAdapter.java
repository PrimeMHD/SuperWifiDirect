package hiveconnect.com.superwifidirect.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Bean.WifiP2pDeviceInReceiving;
import hiveconnect.com.superwifidirect.R;


public class DeviceInReceivingAdapter extends RecyclerView.Adapter<DeviceInReceivingAdapter.ViewHolder> {

    private List<WifiP2pDeviceInReceiving> wifiP2pDeviceInReceivingList;

    private OnClickListener clickListener;

    public interface OnClickListener {

        void onItemClick(int position);

    }

    public DeviceInReceivingAdapter(List<WifiP2pDeviceInReceiving> wifiP2pDeviceInReceivingList) {
        this.wifiP2pDeviceInReceivingList = wifiP2pDeviceInReceivingList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tv_deviceName.setText(wifiP2pDeviceInReceivingList.get(position).getWifiP2pDevice().deviceName);
        holder.tv_deviceAddress.setText(wifiP2pDeviceInReceivingList.get(position).getWifiP2pDevice().deviceAddress);
        holder.tv_deviceDetails.setText(MainActivity.getDeviceStatus(wifiP2pDeviceInReceivingList.get(position).getWifiP2pDevice().status));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return wifiP2pDeviceInReceivingList.size();
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_deviceName;

        private TextView tv_deviceAddress;

        private TextView tv_deviceDetails;

        ViewHolder(View itemView) {
            super(itemView);
            tv_deviceName = (TextView) itemView.findViewById(R.id.tv_deviceName);
            tv_deviceAddress = (TextView) itemView.findViewById(R.id.tv_deviceAddress);
            tv_deviceDetails = (TextView) itemView.findViewById(R.id.tv_deviceDetails);
        }

    }

}
