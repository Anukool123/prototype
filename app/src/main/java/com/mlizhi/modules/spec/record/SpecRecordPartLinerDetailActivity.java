package com.mlizhi.modules.spec.record;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.Session;
import com.mlizhi.modules.spec.dao.DaoSession;
import com.mlizhi.modules.spec.dao.DetectDao;
import com.mlizhi.modules.spec.dao.model.DetectModel;
import com.mlizhi.modules.spec.util.DateFormatUtil;
import com.mlizhi.utils.Constants;
import com.mlizhi.widgets.chart.ChartView4Line;
import com.philips.skincare.skincareprototype.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import p016u.aly.bq;

public class SpecRecordPartLinerDetailActivity extends Activity {
    private TextView averageValuePost;
    private TextView averageValuePre;
    private ChartView4Line chartView4Line;
    private Date currentDate;
    private DaoSession daoSession;
    private DetectDao detectDao;
    private String detectType;
    private Session mSession;
    private MlzApplication mlzApplication;
    private Date nowDate;
    private int partType;
    private TextView recordItemTitle;
    private TextView selectedTime;
    private TextView timeIndicatorLabel;
    private int timeType;
    private String userId;
    private View view;

    public SpecRecordPartLinerDetailActivity() {
        this.view = null;
        this.partType = 0;
        this.timeType = 0;
        this.currentDate = null;
        this.nowDate = null;
        this.chartView4Line = null;
        this.detectType = String.valueOf(23);
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSession = Session.get(getApplicationContext());
        this.mlzApplication = (MlzApplication) getApplication();
        setContentView(R.layout.activity_spec_record_part_chart);
        this.selectedTime = (TextView) findViewById(R.id.id_selected_time_value);
        this.timeIndicatorLabel = (TextView) findViewById(R.id.id_spec_type_indicator_label);
        this.chartView4Line = (ChartView4Line) findViewById(R.id.id_chart_line_view);
        this.averageValuePre = (TextView) findViewById(R.id.id_current_average_value_pre);
        this.averageValuePost = (TextView) findViewById(R.id.id_current_average_value_post);
        this.daoSession = this.mlzApplication.getDaoSession();
        this.detectDao = this.daoSession.getDetectDao();
        this.userId = this.mSession.getUid();
        if (this.userId == null || bq.f888b.equals(this.userId)) {
            this.userId = this.mSession.getMac();
        }
        Intent intent = getIntent();
        HashMap<String, String> typeMap = (HashMap) intent.getSerializableExtra("PartType");
        this.timeType = intent.getIntExtra("TimeType", 0);
        try {
            this.partType = Integer.parseInt((String) typeMap.get(Constants.BODY_PART_TYPE));
        } catch (NumberFormatException e) {
            this.partType = 0;
        }
        this.recordItemTitle = (TextView) findViewById(R.id.id_spec_record_item_tv);
        if (this.partType == 18) {
            this.recordItemTitle.setText(R.string.bodyParts4eye);
        } else if (this.partType == 19) {
            this.recordItemTitle.setText(R.string.bodyParts4hand);
        } else if (this.partType == 17) {
            this.recordItemTitle.setText(R.string.bodyParts4face);
        } else if (this.partType == 20) {
            this.recordItemTitle.setText(R.string.bodyParts4neck);
        }
        this.currentDate = DateFormatUtil.formatDate(new Date());
        this.nowDate = this.currentDate;
        initTimeValue(this.currentDate);
    }

    public void onChoiceChartView(View view) {
        switch (view.getId()) {
            case R.id.id_spec_record_item_back:
                finish();
            case R.id.id_btn4arrow_left:
                if (this.timeType == 24) {
                    this.currentDate = DateFormatUtil.getBeforeDay(this.currentDate);
                } else if (this.timeType == 25) {
                    this.currentDate = DateFormatUtil.getBeforeMonthCurrentDate(this.currentDate);
                } else if (this.timeType == 32) {
                    this.currentDate = DateFormatUtil.getBeforeMonthCurrentDate(this.currentDate);
                }
                initTimeValue(this.currentDate);
            case R.id.id_btn4arrow_right:
                if (this.currentDate.getTime() < this.nowDate.getTime()) {
                    if (this.timeType == 24) {
                        this.currentDate = DateFormatUtil.getNextDay(this.currentDate);
                    } else if (this.timeType == 25) {
                        this.currentDate = DateFormatUtil.getNextWeekCurrentDate(this.currentDate);
                    } else if (this.timeType == 32) {
                        this.currentDate = DateFormatUtil.getNextMonthCurrentDate(this.currentDate);
                    }
                    initTimeValue(this.currentDate);
                } else if (this.timeType == 24) {
                    Toast.makeText(this, R.string.record_nodata_day, Toast.LENGTH_LONG).show();
                } else if (this.timeType == 25) {
                    Toast.makeText(this, R.string.record_nodata_week, Toast.LENGTH_LONG).show();
                } else if (this.timeType == 32) {
                    Toast.makeText(this, R.string.record_nodata_month, Toast.LENGTH_LONG).show();
                }
            default:
        }
    }

    public void initTimeValue(Date currentDate) {
        Date startDate = null;
        Date endDate = null;
        if (this.timeType == 24) {
            this.selectedTime.setText(DateFormatUtil.date2str(currentDate));
            this.timeIndicatorLabel.setText(R.string.current_day);
            startDate = currentDate;
            endDate = DateFormatUtil.getNextDay(currentDate);
        } else if (this.timeType == 25) {
            this.selectedTime.setText(String.valueOf(new StringBuilder(String.valueOf(DateFormatUtil.date2string(DateFormatUtil.getFirstDayOfWeek(currentDate), this))).append(" ~ ").append(DateFormatUtil.date2string(DateFormatUtil.getNextDay(DateFormatUtil.getLastDayOfWeek(currentDate)), this)).toString()));
            this.timeIndicatorLabel.setText(R.string.current_week);
            startDate = DateFormatUtil.getFirstDayOfWeek(currentDate);
            endDate = DateFormatUtil.getLastDayOfWeek(currentDate);
        } else if (this.timeType == 32) {
            this.selectedTime.setText(String.valueOf(new StringBuilder(String.valueOf(DateFormatUtil.date2string(DateFormatUtil.getFirstDayOfMonth(currentDate), this))).append(" ~ ").append(DateFormatUtil.date2string(DateFormatUtil.getNextDay(DateFormatUtil.getLastDayOfMonth(currentDate)), this)).toString()));
            this.timeIndicatorLabel.setText(R.string.current_month);
            startDate = DateFormatUtil.getFirstDayOfMonth(currentDate);
            endDate = DateFormatUtil.getLastDayOfMonth(currentDate);
        }
        initChart(String.valueOf(this.partType), this.userId, String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime()));
    }

    private void initChart(String partType, String userId, String startTime, String endTime) {
        float sumValue;
        List<DetectModel> detectModelPreList = this.detectDao.queryRaw("WHERE PART_TYPE = ? AND DETECT_TYPE = ? AND USER_ID = ? AND INSERT_TIME > ? AND INSERT_TIME < ? AND NURSER_TYPE = ? ORDER BY _id ASC", partType, this.detectType, userId, startTime, endTime, String.valueOf(21));
        List<DetectModel> detectModelPostList = this.detectDao.queryRaw("WHERE PART_TYPE = ? AND DETECT_TYPE = ? AND USER_ID = ? AND INSERT_TIME > ? AND INSERT_TIME < ? AND NURSER_TYPE = ? ORDER BY _id ASC", partType, this.detectType, userId, startTime, endTime, String.valueOf(22));
        if (detectModelPreList == null || detectModelPreList.size() <= 1) {
            this.chartView4Line.setPoints4pre(new float[]{0.0f});
            this.averageValuePre.setText(String.valueOf("0.0%"));
        } else {
            float[] points4pre = new float[detectModelPreList.size()];
            int i = 0;
            sumValue = 0.0f;
            for (DetectModel detectModel : detectModelPreList) {
                points4pre[i] = (float) (detectModel.getDetectValue() * 0.10000000149011612d);
                sumValue += points4pre[i];
                i++;
            }
            this.averageValuePre.setText(String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) ((sumValue / ((float) detectModelPreList.size())) * 1000.0f)) / 10.0d)).append("%").toString()));
            this.chartView4Line.setPoints4pre(points4pre);
        }
        if (detectModelPostList == null || detectModelPostList.size() <= 1) {
            this.averageValuePost.setText(String.valueOf("0.0%"));
            this.chartView4Line.setPoints4post(new float[]{0.0f});
        } else {
            float[] points4post = new float[detectModelPostList.size()];
            int j = 0;
            sumValue = 0.0f;
            for (DetectModel detectModel2 : detectModelPostList) {
                points4post[j] = (float) (detectModel2.getDetectValue() * 0.10000000149011612d);
                sumValue += points4post[j];
                j++;
            }
            this.averageValuePost.setText(String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) ((sumValue / ((float) detectModelPostList.size())) * 1000.0f)) / 10.0d)).append("%").toString()));
            this.chartView4Line.setPoints4post(points4post);
        }
        this.chartView4Line.invalidate();
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
