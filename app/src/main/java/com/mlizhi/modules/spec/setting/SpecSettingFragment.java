package com.mlizhi.modules.spec.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlizhi.base.Session;
import com.mlizhi.base.imageloader.core.DisplayImageOptions;
import com.mlizhi.base.imageloader.core.DisplayImageOptions.Builder;
import com.mlizhi.base.imageloader.core.ImageLoader;
import com.mlizhi.modules.login.LoginActivity;
import com.mlizhi.modules.spec.ISpecInterface;
import com.mlizhi.utils.Constants;
import com.philips.skincare.skincareprototype.R;
import com.umeng.analytics.MobclickAgent;

import p016u.aly.bq;

public class SpecSettingFragment extends Fragment {
    private static SpecSettingFragment specSettingFragment;
    private DisplayImageOptions displayImageOptions;
    private Context mContext;
    private Session mSession;
    private View rootView;
    private TextView settingTv;
    private ISpecInterface specCallback;
    private View userInfoLy;
    private ImageView userInfoPhoto;
    private TextView userNickName;

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingFragment.1 */
    class C01531 implements OnClickListener {
        C01531() {
        }

        public void onClick(View v) {
            MobclickAgent.onEvent(SpecSettingFragment.this.mContext, "startSettingActivity");
            SpecSettingFragment.this.mContext.startActivity(new Intent(SpecSettingFragment.this.mContext, SpecSettingListActivity.class));
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingFragment.2 */
    class C01542 implements OnClickListener {
        C01542() {
        }

        public void onClick(View v) {
            MobclickAgent.onEvent(SpecSettingFragment.this.mContext, "startMineActivity");
            String uid = SpecSettingFragment.this.mSession.getUid();
            if (uid == null || bq.f888b.equals(uid)) {
                Intent intent = new Intent(SpecSettingFragment.this.getActivity(), LoginActivity.class);
                intent.putExtra(Constants.LOGIN_FROM_FLAG, SpecSettingFragment.class.getSimpleName());
                SpecSettingFragment.this.getActivity().startActivityForResult(intent, 16);
                return;
            }
            SpecSettingFragment.this.getActivity().startActivityForResult(new Intent(SpecSettingFragment.this.mContext, SpecSettingMineActivity.class), 49);
        }
    }

    public SpecSettingFragment() {
        this.userInfoLy = null;
        this.displayImageOptions = null;
    }

    public static SpecSettingFragment getNewInstance() {
        if (specSettingFragment == null) {
            specSettingFragment = new SpecSettingFragment();
        }
        return specSettingFragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSession = Session.get(getActivity());
        this.displayImageOptions = new Builder().showImageOnLoading((int) R.drawable.ic_tourist).showImageForEmptyUri((int) R.drawable.ic_tourist).showImageOnFail((int) R.drawable.ic_tourist).cacheInMemory(true).cacheOnDisk(true).build();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.rootView == null) {
            this.rootView = inflater.inflate(R.layout.fragment_spec_setting, container, false);
        }
        ViewGroup parent = (ViewGroup) this.rootView.getParent();
        if (parent != null) {
            parent.removeView(this.rootView);
        }
        this.settingTv = (TextView) this.rootView.findViewById(R.id.id_user_info_setting_label);
        this.userInfoLy = this.rootView.findViewById(R.id.id_user_info_ly);
        this.userInfoPhoto = (ImageView) this.rootView.findViewById(R.id.id_cover_user_photo);
        this.userNickName = (TextView) this.rootView.findViewById(R.id.id_user_info_name);
        return this.rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.specCallback = (ISpecInterface) getActivity();
        this.settingTv.setOnClickListener(new C01531());
        this.userInfoLy.setOnClickListener(new C01542());
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        String userPhoto = this.mSession.getUserIcon();
        if (!(userPhoto == null || bq.f888b.equals(userPhoto))) {
            ImageLoader.getInstance().displayImage(this.mSession.getUserIcon(), this.userInfoPhoto, this.displayImageOptions);
        }
        String userName = this.mSession.getUserName();
        if (userName == null || bq.f888b.equals(userName)) {
            userName = "\u60a8\u8fd8\u672a\u767b\u5f55";
        }
        this.userNickName.setText(userName);
        MobclickAgent.onPageStart("MainScreen");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 16 && resultCode == 17) {
            this.mContext.startActivity(new Intent(this.mContext, SpecSettingMineActivity.class));
        } else if (requestCode == 49 && resultCode == -1) {
            String userPhoto = this.mSession.getUserIcon();
            if (!(userPhoto == null || bq.f888b.equals(userPhoto))) {
                ImageLoader.getInstance().displayImage(this.mSession.getUserIcon(), this.userInfoPhoto, this.displayImageOptions);
            }
            String userName = this.mSession.getUserName();
            if (userName == null || bq.f888b.equals(userName) || "null".equals(userName)) {
                userName = "\u60a8\u8fd8\u672a\u767b\u5f55";
            }
            this.userNickName.setText(userName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
