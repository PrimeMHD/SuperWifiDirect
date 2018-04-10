package hiveconnect.com.superwifidirect.Fragment.BasicFragment;

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

public class MainFragment extends MySupportFragment {


    private Button button_CreateGroup;
    private Button button_FindGroup;


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
        button_CreateGroup=(Button) view.findViewById(R.id.button_CreateGroup);
        button_FindGroup=(Button)view.findViewById(R.id.button_FindGroup);
        setOnClick();



        return view;
    }

    private void setOnClick() {
        button_CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start(Fragment_GroupCreate.newInstance());
                ((MainActivity)mContext).showToast("createGroup");
            }
        });

        button_FindGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(Fragment_GroupFind.newInstance());
                ((MainActivity)mContext).showToast("findGroup");
            }
        });
    }






}
