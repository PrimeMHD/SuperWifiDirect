package hiveconnect.com.superwifidirect.Fragment.BasicFragment;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import hiveconnect.com.superwifidirect.Activity.MainActivity;

public class BaseMainFragment extends MySupportFragment {
    protected OnBackToFirstListener _mBackToFirstListener;
    protected Context mContext;
    protected MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mainActivity=(MainActivity)context;
        if (context instanceof OnBackToFirstListener) {
            _mBackToFirstListener = (OnBackToFirstListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBackToFirstListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _mBackToFirstListener = null;
    }

    /**
     * 处理回退事件
     *
     * @return
     */
    @Override
    public boolean onBackPressedSupport() {
        Log.d("now", "on back press");
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            popChild();
            Log.d("NOW_pop", getChildFragmentManager().getBackStackEntryCount() + "");
        } else {
            Log.d("NOW", getChildFragmentManager().getBackStackEntryCount() + "");
            if (this instanceof ContainerFragment) {   // 如果是 第一个Fragment 则退出app
                _mActivity.finish();
            } else {                                    // 如果不是,则回到第一个Fragment
                Log.d("现在","back to first frag");
                _mBackToFirstListener.onBackToFirstFragment();
            }
        }
        return true;
    }

    public interface OnBackToFirstListener {
        void onBackToFirstFragment();
    }
    protected void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
