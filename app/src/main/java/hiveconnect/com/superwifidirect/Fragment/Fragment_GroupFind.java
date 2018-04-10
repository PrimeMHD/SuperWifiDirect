package hiveconnect.com.superwifidirect.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hiveconnect.com.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import hiveconnect.com.superwifidirect.R;

public class Fragment_GroupFind extends MySupportFragment {
    private Context mContext;


    public static Fragment_GroupFind newInstance() {
        Bundle args = new Bundle();
        Fragment_GroupFind fragment = new Fragment_GroupFind();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_formgroup_slave, container, false);

        return view;
    }


}
