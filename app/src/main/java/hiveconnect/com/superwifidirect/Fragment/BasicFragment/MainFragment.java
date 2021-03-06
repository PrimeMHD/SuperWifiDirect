package hiveconnect.com.superwifidirect.Fragment.BasicFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hiveconnect.com.superwifidirect.Activity.MainActivity;
import hiveconnect.com.superwifidirect.Fragment.Fragment_GroupCreate;
import hiveconnect.com.superwifidirect.Fragment.Fragment_GroupFind;
import hiveconnect.com.superwifidirect.R;
import hiveconnect.com.superwifidirect.Service.HandshakeServerService;
import hiveconnect.com.superwifidirect.Util.EnumPack;

public class MainFragment extends MySupportFragment {


    private Button button_GroupMaster;
    private Button button_GroupSlave;


    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_groupwork, container, false);
        button_GroupMaster=(Button) view.findViewById(R.id.button_GroupMaster);
        button_GroupSlave=(Button)view.findViewById(R.id.button_GroupSlave);
        setOnClick();



        return view;
    }

    private void setOnClick() {
        button_GroupMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.setDBRState(EnumPack.DBRState.GROUP_CREATE);
                mainActivity.setHandshakeServiceCharactor(HandshakeServerService.GroupCharactor.MASTER);
                mainActivity.startService(new Intent(mainActivity, HandshakeServerService.class));
                start(Fragment_GroupCreate.newInstance());
                mainActivity.showToast("GroupMaster");
            }
        });

        button_GroupSlave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.setDBRState(EnumPack.DBRState.GROUP_FIND);
                mainActivity.setHandshakeServiceCharactor(HandshakeServerService.GroupCharactor.SLAVE);
                mainActivity.startService(new Intent(mainActivity, HandshakeServerService.class));
                start(Fragment_GroupFind.newInstance());
                ((MainActivity)mContext).showToast("GroupSlave");
            }
        });
    }






}
