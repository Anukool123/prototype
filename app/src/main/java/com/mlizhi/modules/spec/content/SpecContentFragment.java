package com.mlizhi.modules.spec.content;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.NetWorkManager;
import com.mlizhi.base.SecurityUtil;
import com.mlizhi.base.Session;
import com.mlizhi.modules.spec.ISpecInterface;
import com.mlizhi.modules.spec.dao.ContentDao;
import com.mlizhi.modules.spec.dao.DaoSession;
import com.mlizhi.modules.spec.dao.model.ContentModel;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.mlizhi.widgets.waterfall.MultiColumnPullToRefreshListView;
import com.mlizhi.widgets.waterfall.internal.PLA_AbsListView;
import com.mlizhi.widgets.waterfall.internal.PLA_AbsListView.OnScrollListener;
import com.mlizhi.widgets.waterfall.internal.PLA_AdapterView;
import com.mlizhi.widgets.waterfall.internal.PLA_AdapterView.OnItemClickListener;
import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpecContentFragment extends Fragment {
    private static SpecContentFragment specContentFragment;
    private AdvertiseAdapter advertiseAdapter;
    private TextView advertiseDes;
    private LinearLayout advertisePoints;
    private ContentDao contentDao;
    private List<HashMap<String, String>> contentList;
    private DaoSession daoSession;
    private String[] descriptions;
    private int[] drawables;
    private List<ImageView> imageViews;
    private int index;
    ErrorListener listener4error;
    Listener<String> listener4success;
    private ContentAdapter mAdapter;
    private Context mContext;
    Handler mHandler;
    private LayoutInflater mInflater;
    private MultiColumnPullToRefreshListView mMultiColumnListView;
    private RequestQueue mRequestQueue;
    private Session mSession;
    private MlzApplication mlzApplication;
    private int pageNo;
    private int pageTotal;
    private View rootView;
    private ISpecInterface specCallback;
    private ImageView[] tips;
    private ViewPager viewPager;

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.1 */
    class C01311 extends Handler {
        C01311() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                SpecContentFragment.this.viewPager.setCurrentItem(SpecContentFragment.this.index);
                if (SpecContentFragment.this.index < Integer.MAX_VALUE) {
                    SpecContentFragment specContentFragment = SpecContentFragment.this;
                    specContentFragment.index = specContentFragment.index + 1;
                    return;
                }
                SpecContentFragment.this.index = 0;
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.6 */
    class C01326 extends Thread {
        C01326() {
        }

        public void run() {
            super.run();
            while (true) {
                Message msg = SpecContentFragment.this.mHandler.obtainMessage();
                msg.what = 0;
                SpecContentFragment.this.mHandler.sendMessage(msg);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.2 */
    class C04052 implements ErrorListener {
        C04052() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(SpecContentFragment.this.mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.3 */
    class C04063 implements Listener<String> {
        C04063() {
        }

        public void onResponse(String response) {
            if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                JSONObject bodyJsonObject = JsonUtil.getBodyJsonObject(response);
                if (bodyJsonObject != null) {
                    try {
                        SpecContentFragment.this.pageTotal = bodyJsonObject.getInt(com.mlizhi.utils.Constants.CONTENT_ITEM_PAGE_COUNT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                List<HashMap<String, String>> tempContentList = JsonUtil.getListContentMap(bodyJsonObject);
                SpecContentFragment specContentFragment;
                if (SpecContentFragment.this.pageNo == 1) {
                    SpecContentFragment.this.contentList.clear();
                    SpecContentFragment.this.contentList.addAll(tempContentList);
                    SpecContentFragment.this.mAdapter.notifyDataSetChanged();
                    specContentFragment = SpecContentFragment.this;
                    specContentFragment.pageNo = specContentFragment.pageNo + 1;
                    return;
                } else if (SpecContentFragment.this.contentList != null && SpecContentFragment.this.contentList.size() > 0) {
                    SpecContentFragment.this.contentList.addAll(tempContentList);
                    SpecContentFragment.this.mAdapter.notifyDataSetChanged();
                    specContentFragment = SpecContentFragment.this;
                    specContentFragment.pageNo = specContentFragment.pageNo + 1;
                    return;
                } else if (SpecContentFragment.this.pageNo > SpecContentFragment.this.pageTotal) {
                    Toast.makeText(SpecContentFragment.this.mContext, "\u6570\u636e\u52a0\u8f7d\u5b8c\u6bd5\uff01\uff01", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    return;
                }
            }
            Toast.makeText(SpecContentFragment.this.mContext, JsonUtil.getHeaderErrorInfo(response), Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.4 */
    class C04074 implements OnScrollListener {
        C04074() {
        }

        public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
            switch (scrollState) {
                case 2 /*2*/:
                    if (SpecContentFragment.this.pageNo <= SpecContentFragment.this.pageTotal) {
                        SpecContentFragment.this.getContentList(SpecContentFragment.this.pageNo);
                    }
                default:
            }
        }

        public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.5 */
    class C04085 implements OnItemClickListener {
        C04085() {
        }

        public void onItemClick(PLA_AdapterView<?> pLA_AdapterView, View view, int position, long id) {
            // TODO-ANUKOOL
            /*Intent intent = new Intent(SpecContentFragment.this.mContext, SpecContentViewActivity.class);
            if (id >= ((long) SpecContentFragment.this.contentList.size())) {
                id = (long) (SpecContentFragment.this.contentList.size() - 1);
            }
            HashMap<String, String> contentMap = (HashMap) SpecContentFragment.this.contentList.get((int) id);
            intent.putExtra(com.mlizhi.utils.Constants.CONTENT_ITEM_ID, (String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_ID));
            intent.putExtra(com.mlizhi.utils.Constants.CONTENT_ITEM_TITLE, (String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_TITLE));
            intent.putExtra(com.mlizhi.utils.Constants.CONTENT_ITEM_IMAGE, (String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_IMAGE));
            SpecContentFragment.this.mContext.startActivity(intent);*/
        }
    }

    private class AdvertiseAdapter extends PagerAdapter {
        String[] tempDesc;
        TextView tempDescTextView;
        int[] tempDrawables;
        List<ImageView> tempImageViews;

        private AdvertiseAdapter() {
            this.tempImageViews = SpecContentFragment.this.imageViews;
            this.tempDrawables = SpecContentFragment.this.drawables;
            this.tempDesc = SpecContentFragment.this.descriptions;
            this.tempDescTextView = SpecContentFragment.this.advertiseDes;
        }

        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = (ImageView) this.tempImageViews.get(position % this.tempDrawables.length);
            try {
                imageView.setImageResource(this.tempDrawables[position % this.tempDrawables.length]);
                imageView.setScaleType(ScaleType.CENTER_CROP);
                imageView.setTag(Integer.valueOf(this.tempDrawables[position % this.tempDrawables.length]));
                SpecContentFragment.this.setImageBackground(position % this.tempDrawables.length);
                this.tempDescTextView.setText(this.tempDesc[position % this.tempDrawables.length]);
                ((ViewPager) container).addView(imageView);
            } catch (Exception e) {
            }
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /* renamed from: com.mlizhi.modules.spec.content.SpecContentFragment.7 */
    class C05437 extends StringRequest {
        private final /* synthetic */ int val$pageNo;

        C05437(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3, int i) {

            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
            this.val$pageNo = i;
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            params.put("companyId", Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put("pageNo", String.valueOf(this.val$pageNo));
            params.put("pageSize", String.valueOf(20));
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    public SpecContentFragment() {
        this.mMultiColumnListView = null;
        this.mAdapter = null;
        this.pageNo = 1;
        this.pageTotal = 1;
        this.index = 0;
        this.mHandler = new C01311();
        this.listener4error = new C04052();
        this.listener4success = new C04063();
        this.drawables = new int[]{R.drawable.ic_advertise_01, R.drawable.ic_advertise_02, R.drawable.ic_advertise_03};
        this.descriptions = new String[]{"\u8336\u854a\u5ae9\u767d\u7cfb\u5217", "\u808c\u80a4\u7ba1\u5bb6", "\u7537\u58eb\u65b0\u54c1"};
        this.imageViews = new ArrayList();
    }

    public static SpecContentFragment getNewInstance() {
        if (specContentFragment == null) {
            specContentFragment = new SpecContentFragment();
        }
        return specContentFragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mlzApplication = (MlzApplication) getActivity().getApplication();
        this.mSession = Session.get(getActivity().getApplicationContext());
        this.daoSession = this.mlzApplication.getDaoSession();
        this.contentDao = this.daoSession.getContentDao();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.rootView == null) {
            this.rootView = inflater.inflate(R.layout.fragment_spec_content, container, false);
            this.mInflater = LayoutInflater.from(getActivity());
            this.mMultiColumnListView = (MultiColumnPullToRefreshListView) this.rootView.findViewById(R.id.content_list);
            View headerView = this.mInflater.inflate(R.layout.content_view_header, null);
            for (int i = 0; i < this.drawables.length; i++) {
                this.imageViews.add(new ImageView(getActivity()));
            }
            this.advertisePoints = (LinearLayout) headerView.findViewById(R.id.advertise_points);
            this.advertiseDes = (TextView) headerView.findViewById(R.id.advertise_slogan);
            this.advertiseDes.setText(this.descriptions[0]);
            this.viewPager = (ViewPager) headerView.findViewById(R.id.advertise_view_pager);
            initPointGroup();
            this.advertiseAdapter = new AdvertiseAdapter();
            this.viewPager.setAdapter(this.advertiseAdapter);
            this.viewPager.setCurrentItem(0);
            this.mMultiColumnListView.addHeaderView(headerView);
        }
        ViewGroup parent = (ViewGroup) this.rootView.getParent();
        if (parent != null) {
            parent.removeView(this.rootView);
        }
        return this.rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.specCallback = (ISpecInterface) getActivity();
        this.contentList = new ArrayList();
        List<HashMap<String, String>> tempContentList = getStateInstance();
        this.contentList.clear();
        this.contentList.addAll(tempContentList);
        this.mAdapter = new ContentAdapter(getActivity());
        this.mAdapter.setContentList(this.contentList);
        this.mMultiColumnListView.setAdapter(this.mAdapter);
        this.mMultiColumnListView.setOnScrollListener(new C04074());
        this.mMultiColumnListView.setOnItemClickListener(new C04085());
        new C01326().start();
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
        saveStateInstance();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    private void saveStateInstance() {
        if (this.contentList != null && this.contentList.size() > 0) {
            int contentSize = this.contentList.size();
            this.contentDao.deleteAll();
            int i;
            HashMap<String, String> contentMap;
            ContentModel entity;
            if (this.contentList.size() > 10) {
                for (i = 0; i < 10; i++) {
                    contentMap = (HashMap) this.contentList.get(i);
                    entity = new ContentModel();
                    entity.setContentId((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_ID));
                    entity.setContentImageUrl((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_IMAGE));
                    entity.setContentPraise((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_PRAISE_NUM));
                    entity.setContentTitle((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_TITLE));
                    entity.setContentView((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_VIEW_NUM));
                    this.contentDao.insert(entity);
                }
                return;
            }
            for (i = 0; i < contentSize; i++) {
                contentMap = (HashMap) this.contentList.get(i);
                entity = new ContentModel();
                entity.setContentId((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_ID));
                entity.setContentImageUrl((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_IMAGE));
                entity.setContentPraise((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_PRAISE_NUM));
                entity.setContentTitle((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_TITLE));
                entity.setContentView((String) contentMap.get(com.mlizhi.utils.Constants.CONTENT_ITEM_VIEW_NUM));
                this.contentDao.insert(entity);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private List<HashMap<String, String>> getStateInstance() {
        /*
        r10 = this;
        r9 = 1;
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1 = 0;
        r6 = r10.contentDao;	 Catch:{ Exception -> 0x0060 }
        r0 = r6.loadAll();	 Catch:{ Exception -> 0x0060 }
        r6 = r0.iterator();	 Catch:{ Exception -> 0x0060 }
        r2 = r1;
    L_0x0012:
        r7 = r6.hasNext();	 Catch:{ Exception -> 0x0065 }
        if (r7 != 0) goto L_0x001f;
    L_0x0018:
        r1 = r2;
    L_0x0019:
        r10.getContentList(r9);
        r10.pageNo = r9;
        return r3;
    L_0x001f:
        r4 = r6.next();	 Catch:{ Exception -> 0x0065 }
        r4 = (com.mlizhi.modules.spec.dao.model.ContentModel) r4;	 Catch:{ Exception -> 0x0065 }
        r1 = new java.util.HashMap;	 Catch:{ Exception -> 0x0065 }
        r1.<init>();	 Catch:{ Exception -> 0x0065 }
        r7 = "content_item_id";
        r8 = r4.getContentId();	 Catch:{ Exception -> 0x0060 }
        r8 = java.lang.String.valueOf(r8);	 Catch:{ Exception -> 0x0060 }
        r1.put(r7, r8);	 Catch:{ Exception -> 0x0060 }
        r7 = "content_item_image";
        r8 = r4.getContentImageUrl();	 Catch:{ Exception -> 0x0060 }
        r1.put(r7, r8);	 Catch:{ Exception -> 0x0060 }
        r7 = "content_item_praise_num";
        r8 = r4.getContentPraise();	 Catch:{ Exception -> 0x0060 }
        r1.put(r7, r8);	 Catch:{ Exception -> 0x0060 }
        r7 = "content_item_title";
        r8 = r4.getContentTitle();	 Catch:{ Exception -> 0x0060 }
        r1.put(r7, r8);	 Catch:{ Exception -> 0x0060 }
        r7 = "content_item_view_num";
        r8 = r4.getContentView();	 Catch:{ Exception -> 0x0060 }
        r1.put(r7, r8);	 Catch:{ Exception -> 0x0060 }
        r3.add(r1);	 Catch:{ Exception -> 0x0060 }
        r2 = r1;
        goto L_0x0012;
    L_0x0060:
        r5 = move-exception;
    L_0x0061:
        r5.printStackTrace();
        goto L_0x0019;
    L_0x0065:
        r5 = move-exception;
        r1 = r2;
        goto L_0x0061;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.modules.spec.content.SpecContentFragment.getStateInstance():java.util.List<java.util.HashMap<java.lang.String, java.lang.String>>");
    }

    private void getContentList(int pageNo) {
        if (NetWorkManager.getNewInstance().isNetworkConnected(this.mContext)) {
            this.mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            this.mRequestQueue.add(new C05437(1, com.mlizhi.utils.Constants.URL_CONTENT_LIST_URL, this.listener4success, this.listener4error, pageNo));
            this.mRequestQueue.start();
        }
    }

    private void initPointGroup() {
        this.tips = new ImageView[this.drawables.length];
        for (int i = 0; i < this.tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            this.tips[i] = imageView;
            if (i == 0) {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator_focused);
            } else {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator);
            }
            LayoutParams layoutParams = new LayoutParams(new ViewGroup.LayoutParams(-2, -1));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            this.advertisePoints.setGravity(16);
            this.advertisePoints.addView(imageView, layoutParams);
        }
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < this.tips.length; i++) {
            if (i == selectItems) {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator_focused);
            } else {
                this.tips[i].setBackgroundResource(R.drawable.ic_splash_indicator);
            }
        }
    }
}
