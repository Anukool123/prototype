package com.mlizhi.modules.spec.record;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.mlizhi.C0111R;
import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.Session;
import com.mlizhi.modules.spec.ISpecInterface;
import com.mlizhi.modules.spec.dao.DaoSession;
import com.mlizhi.modules.spec.dao.DetectDao;
import com.mlizhi.modules.spec.dao.model.DetectModel;
import com.mlizhi.modules.spec.record.adapter.RecordPartAdapter;
import com.mlizhi.modules.spec.util.DateFormatUtil;
import com.mlizhi.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import p016u.aly.bq;

public class SpecRecordFragment extends Fragment {
    private static SpecRecordFragment specRecordFragment;
    private Date currentDate;
    private DaoSession daoSession;
    private DetectDao detectDao;
    private Context mContext;
    private Session mSession;
    private MlzApplication mlzApplication;
    private ListView recordListView;
    private RecordPartAdapter recordPartAdapter;
    private RadioGroup recordPartGroup;
    private List<HashMap<String, String>> recordPartList;
    private View rootView;
    private ISpecInterface specCallback;
    private int timeType;
    private String userId;

    /* renamed from: com.mlizhi.modules.spec.record.SpecRecordFragment.1 */
    class C01481 implements OnCheckedChangeListener {
        C01481() {
        }

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case C0111R.id.id_btn4day:
                    SpecRecordFragment.this.initDayView();
                    SpecRecordFragment.this.timeType = 24;
                    MobclickAgent.onEvent(SpecRecordFragment.this.mContext, "selectHistoryDataByDay");
                    SpecRecordFragment.this.recordPartGroup.check(C0111R.id.id_btn4day);
                case C0111R.id.id_btn4week:
                    SpecRecordFragment.this.initWeekView();
                    SpecRecordFragment.this.timeType = 25;
                    MobclickAgent.onEvent(SpecRecordFragment.this.mContext, "selectHistoryDataByWeek");
                    SpecRecordFragment.this.recordPartGroup.check(C0111R.id.id_btn4week);
                case C0111R.id.id_btn4month:
                    SpecRecordFragment.this.initMonthView();
                    SpecRecordFragment.this.timeType = 32;
                    MobclickAgent.onEvent(SpecRecordFragment.this.mContext, "selectHistoryDataByMonth");
                    SpecRecordFragment.this.recordPartGroup.check(C0111R.id.id_btn4month);
                default:
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.record.SpecRecordFragment.2 */
    class C01492 implements OnItemClickListener {
        C01492() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            float waterFloat;
            HashMap<String, String> typeMap = (HashMap) SpecRecordFragment.this.recordPartList.get((int) id);
            try {
                waterFloat = Float.parseFloat((String) typeMap.get(Constants.BODY_PART_WATER_VALUE));
            } catch (Exception e) {
                waterFloat = 0.0f;
            }
            if (SpecRecordFragment.this.timeType == 24) {
                if (waterFloat != 0.0f) {
                    SpecRecordFragment.this.startActivity(typeMap, SpecRecordFragment.this.timeType);
                } else {
                    SpecRecordFragment.this.dialog(SpecRecordFragment.this.mContext.getResources().getString(C0111R.string.detect_report_day), typeMap, SpecRecordFragment.this.timeType);
                }
            } else if (SpecRecordFragment.this.timeType == 25) {
                if (waterFloat != 0.0f) {
                    SpecRecordFragment.this.startActivity(typeMap, SpecRecordFragment.this.timeType);
                } else {
                    SpecRecordFragment.this.dialog(SpecRecordFragment.this.mContext.getResources().getString(C0111R.string.detect_report_week), typeMap, SpecRecordFragment.this.timeType);
                }
            } else if (SpecRecordFragment.this.timeType == 32) {
                if (waterFloat != 0.0f) {
                    SpecRecordFragment.this.startActivity(typeMap, SpecRecordFragment.this.timeType);
                } else {
                    SpecRecordFragment.this.dialog(SpecRecordFragment.this.mContext.getResources().getString(C0111R.string.detect_report_month), typeMap, SpecRecordFragment.this.timeType);
                }
            }
            MobclickAgent.onEvent(SpecRecordFragment.this.mContext, "go2historyView");
        }
    }

    /* renamed from: com.mlizhi.modules.spec.record.SpecRecordFragment.3 */
    class C01503 implements OnClickListener {
        C01503() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            SpecRecordFragment.this.specCallback.switchSpecViewById(2);
        }
    }

    /* renamed from: com.mlizhi.modules.spec.record.SpecRecordFragment.4 */
    class C01514 implements OnClickListener {
        private final /* synthetic */ int val$timeType;
        private final /* synthetic */ HashMap val$typeMap;

        C01514(HashMap hashMap, int i) {
            this.val$typeMap = hashMap;
            this.val$timeType = i;
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            SpecRecordFragment.this.startActivity(this.val$typeMap, this.val$timeType);
        }
    }

    private SpecRecordFragment() {
        this.timeType = 24;
        this.currentDate = null;
    }

    public static SpecRecordFragment getNewInstance() {
        if (specRecordFragment == null) {
            specRecordFragment = new SpecRecordFragment();
        }
        return specRecordFragment;
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
        this.detectDao = this.daoSession.getDetectDao();
        this.userId = this.mSession.getUid();
        if (this.userId == null || bq.f888b.equals(this.userId)) {
            this.userId = this.mSession.getMac();
        }
        this.currentDate = DateFormatUtil.formatDate(new Date());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.rootView == null) {
            this.rootView = inflater.inflate(C0111R.layout.fragment_spec_record, container, false);
        }
        ViewGroup parent = (ViewGroup) this.rootView.getParent();
        if (parent != null) {
            parent.removeView(this.rootView);
        }
        this.recordListView = (ListView) this.rootView.findViewById(C0111R.id.id_part_category_list);
        this.recordPartGroup = (RadioGroup) this.rootView.findViewById(C0111R.id.id_id_btn4time_group);
        this.recordPartGroup.check(C0111R.id.id_btn4month);
        return this.rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.specCallback = (ISpecInterface) getActivity();
        initView();
        this.recordPartGroup.setOnCheckedChangeListener(new C01481());
        this.recordListView.setOnItemClickListener(new C01492());
    }

    private void initView() {
        initDayView();
    }

    private void initDayView() {
        this.recordPartList = new ArrayList();
        this.recordPartAdapter = new RecordPartAdapter(getActivity());
        Date mediumTime = DateFormatUtil.getBeforeDay(this.currentDate);
        Date preCurrentTime = DateFormatUtil.getBeforeDay(mediumTime);
        Date endTime = DateFormatUtil.getNextDay(this.currentDate);
        HashMap<String, String> faceMap = new HashMap();
        float preDayAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(preCurrentTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentDayAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        faceMap.put(Constants.BODY_PART_TYPE, String.valueOf(17));
        faceMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_day));
        if (preDayAvgFaceValue == 0.0f) {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((((double) (currentDayAvgFaceValue - preDayAvgFaceValue)) / 0.01d) * 10.0d) / 10.0d)).append("%").toString()));
        } else {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentDayAvgFaceValue - preDayAvgFaceValue) / preDayAvgFaceValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        faceMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentDayAvgFaceValue)) / 10.0d));
        this.recordPartList.add(faceMap);
        HashMap<String, String> eyeMap = new HashMap();
        float preDayAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(preCurrentTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentDayAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        eyeMap.put(Constants.BODY_PART_TYPE, String.valueOf(18));
        eyeMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_day));
        if (preDayAvgEyeValue == 0.0f) {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentDayAvgEyeValue - preDayAvgEyeValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentDayAvgEyeValue - preDayAvgEyeValue) / preDayAvgEyeValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        eyeMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentDayAvgEyeValue)) / 10.0d));
        this.recordPartList.add(eyeMap);
        HashMap<String, String> handMap = new HashMap();
        float preDayAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(preCurrentTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentDayAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        handMap.put(Constants.BODY_PART_TYPE, String.valueOf(19));
        handMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_day));
        if (preDayAvgHandValue == 0.0f) {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentDayAvgHandValue - preDayAvgHandValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentDayAvgHandValue - preDayAvgHandValue) / preDayAvgHandValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        handMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentDayAvgHandValue)) / 10.0d));
        this.recordPartList.add(handMap);
        this.recordPartAdapter.setRecordPartList(this.recordPartList);
        this.recordListView.setAdapter(this.recordPartAdapter);
    }

    private void initMonthView() {
        Date preWeekStartTime = DateFormatUtil.getFirstDayOfMonth(DateFormatUtil.getBeforeMonthCurrentDate(this.currentDate));
        Date mediumTime = DateFormatUtil.getFirstDayOfMonth(this.currentDate);
        Date endTime = DateFormatUtil.getNextDay(DateFormatUtil.getLastDayOfMonth(this.currentDate));
        this.recordPartList = new ArrayList();
        this.recordPartAdapter = new RecordPartAdapter(getActivity());
        endTime = DateFormatUtil.getNextDay(endTime);
        HashMap<String, String> faceMap = new HashMap();
        float preMonthAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(preWeekStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentMonthAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        faceMap.put(Constants.BODY_PART_TYPE, String.valueOf(17));
        faceMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_month));
        if (preMonthAvgFaceValue == 0.0f) {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgFaceValue - preMonthAvgFaceValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgFaceValue - preMonthAvgFaceValue) / preMonthAvgFaceValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        faceMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentMonthAvgFaceValue)) / 10.0d));
        this.recordPartList.add(faceMap);
        HashMap<String, String> eyeMap = new HashMap();
        float preMonthAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(preWeekStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentMonthAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        eyeMap.put(Constants.BODY_PART_TYPE, String.valueOf(18));
        eyeMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_month));
        if (preMonthAvgEyeValue == 0.0f) {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgEyeValue - preMonthAvgEyeValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgEyeValue - preMonthAvgEyeValue) / preMonthAvgEyeValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        eyeMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentMonthAvgEyeValue)) / 10.0d));
        this.recordPartList.add(eyeMap);
        HashMap<String, String> handMap = new HashMap();
        float preMonthAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(preWeekStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentMonthAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        handMap.put(Constants.BODY_PART_TYPE, String.valueOf(19));
        handMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_month));
        if (preMonthAvgHandValue == 0.0f) {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgHandValue - preMonthAvgHandValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentMonthAvgHandValue - preMonthAvgHandValue) / preMonthAvgHandValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        handMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentMonthAvgHandValue)) / 10.0d));
        this.recordPartList.add(handMap);
        this.recordPartAdapter.setRecordPartList(this.recordPartList);
        this.recordListView.setAdapter(this.recordPartAdapter);
    }

    private void initWeekView() {
        Date preMonthStartTime = DateFormatUtil.getFirstDayOfWeek(DateFormatUtil.getBeforeWeekCurrentDate(this.currentDate));
        Date mediumTime = DateFormatUtil.getFirstDayOfWeek(this.currentDate);
        Date endTime = DateFormatUtil.getNextDay(DateFormatUtil.getLastDayOfWeek(this.currentDate));
        this.recordPartList = new ArrayList();
        this.recordPartAdapter = new RecordPartAdapter(getActivity());
        HashMap<String, String> faceMap = new HashMap();
        float preWeekAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(preMonthStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentWeekAvgFaceValue = getAverageByTimeCategory(String.valueOf(17), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        faceMap.put(Constants.BODY_PART_TYPE, String.valueOf(17));
        faceMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_week));
        if (preWeekAvgFaceValue == 0.0f) {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgFaceValue - preWeekAvgFaceValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            faceMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgFaceValue - preWeekAvgFaceValue) / preWeekAvgFaceValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        faceMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentWeekAvgFaceValue)) / 10.0d));
        this.recordPartList.add(faceMap);
        HashMap<String, String> eyeMap = new HashMap();
        float preWeekAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(preMonthStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentWeekAvgEyeValue = getAverageByTimeCategory(String.valueOf(18), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        eyeMap.put(Constants.BODY_PART_TYPE, String.valueOf(18));
        eyeMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_week));
        if (preWeekAvgEyeValue == 0.0f) {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgEyeValue - preWeekAvgEyeValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            eyeMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgEyeValue - preWeekAvgEyeValue) / preWeekAvgEyeValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        eyeMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentWeekAvgEyeValue)) / 10.0d));
        this.recordPartList.add(eyeMap);
        HashMap<String, String> handMap = new HashMap();
        float preWeekAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(preMonthStartTime.getTime()), String.valueOf(mediumTime.getTime()));
        float currentWeekAvgHandValue = getAverageByTimeCategory(String.valueOf(19), this.userId, String.valueOf(mediumTime.getTime()), String.valueOf(endTime.getTime()));
        handMap.put(Constants.BODY_PART_TYPE, String.valueOf(19));
        handMap.put(Constants.BODY_PART_COMPARE_LABEL, getString(C0111R.string.skin_healthLabel4comparsion_week));
        if (preWeekAvgHandValue == 0.0f) {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgHandValue - preWeekAvgHandValue) / 0.01f) * 10.0f)) / 10.0d)).append("%").toString()));
        } else {
            handMap.put(Constants.BODY_PART_COMPARE_VALUE, String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (((currentWeekAvgHandValue - preWeekAvgHandValue) / preWeekAvgHandValue) * 10.0f)) / 10.0d)).append("%").toString()));
        }
        handMap.put(Constants.BODY_PART_WATER_VALUE, String.valueOf(Math.ceil((double) (1000.0f * currentWeekAvgHandValue)) / 10.0d));
        this.recordPartList.add(handMap);
        this.recordPartAdapter.setRecordPartList(this.recordPartList);
        this.recordListView.setAdapter(this.recordPartAdapter);
    }

    private float getAverageByTimeCategory(String partType, String userId, String startTime, String endTime) {
        List<DetectModel> detectModelList = this.detectDao.queryRaw("WHERE PART_TYPE = ? AND USER_ID = ? AND INSERT_TIME > ? AND INSERT_TIME < ? AND DETECT_TYPE = ?", partType, userId, startTime, endTime, String.valueOf(23));
        if (detectModelList == null || detectModelList.size() <= 0) {
            return 0.0f;
        }
        float sumValue = 0.0f;
        for (DetectModel detectModel : detectModelList) {
            sumValue += (float) (detectModel.getDetectValue() * 0.10000000149011612d);
        }
        return sumValue / ((float) detectModelList.size());
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
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

    protected void dialog(String msg, HashMap<String, String> typeMap, int timeType) {
        Builder builder = new Builder(getActivity());
        builder.setMessage(msg);
        builder.setPositiveButton(this.mContext.getResources().getString(C0111R.string.detect_goto_detect), new C01503());
        builder.setNegativeButton(this.mContext.getResources().getString(C0111R.string.detect_goto_detail), new C01514(typeMap, timeType));
        builder.create().show();
    }

    private void startActivity(HashMap<String, String> typeMap, int timeType) {
        Intent intent = new Intent(this.mContext, SpecRecordPartLinerDetailActivity.class);
        intent.putExtra("PartType", typeMap);
        intent.putExtra("TimeType", timeType);
        this.mContext.startActivity(intent);
    }
}
