package com.mlizhi.modules.spec.detect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.Session;
import com.mlizhi.widgets.cursor4water.WaterRangeView;
import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import p016u.aly.bq;

public class SpecDetectResultActivity extends Activity {
    private TextView ageText;
    private TextView compareAgeTv;
    private TextView conditionValue;
    private ImageView detectResultIv;
    private Session mSession;
    private MlzApplication mlzApplication;
    private TextView nurserTypeTextView;
    private TextView positionText;
    private TextView recommandText;
    private TextView sexText;
    private TextView skinText;
    private int tempNurserType;
    private WaterRangeView waterRangeView;
    private String compareAgeValue;

    public SpecDetectResultActivity() {
        this.mlzApplication = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        int age;
        int partType;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spec_detect_result);
        this.mlzApplication = (MlzApplication) getApplication();
        this.mSession = Session.get(getApplicationContext());
        this.detectResultIv = (ImageView) findViewById(R.id.id_detect_result_back);
        this.positionText = (TextView) findViewById(R.id.position_text);
        this.sexText = (TextView) findViewById(R.id.sex_text);
        this.ageText = (TextView) findViewById(R.id.age_text);
        this.skinText = (TextView) findViewById(R.id.skin_text);
        this.waterRangeView = (WaterRangeView) findViewById(R.id.id_water_range_view);
        this.compareAgeTv = (TextView) findViewById(R.id.id_nurser_result_compare_value);
        this.conditionValue = (TextView) findViewById(R.id.id_skan_condition_value);
        this.recommandText = (TextView) findViewById(R.id.id_detect_result_recommand_text);
        this.nurserTypeTextView = (TextView) findViewById(R.id.id_nurser_resutl_label);
        String briday = this.mSession.getUserBriday();
        String briday_year = "1990";
        if (!(briday == null || bq.f888b.equals(briday))) {
            briday_year = briday.substring(0, 4);
        }
        try {
            age = Integer.parseInt(new SimpleDateFormat("yyyy-MM-dd").format(new Date()).substring(0, 4)) - Integer.parseInt(briday_year);
        } catch (Exception e) {
            age = 25;
        }
        this.ageText.setText(String.valueOf(age));
        String sex = bq.f888b;
        if (Constants.VIA_RESULT_SUCCESS.equals(this.mSession.getUserSex())) {
            sex = getString(R.string.user_pre_setting_sex_female);
        } else if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSex())) {
            sex = getString(R.string.user_pre_setting_sex_male);
        }
        this.sexText.setText(sex);
        String skinType = getString(R.string.user_pre_setting_skin_type_unknow);
        if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_dry);
        } else if (Constants.VIA_SSO_LOGIN.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_oil);
        } else if (Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_hybird);
        } else if (Constants.VIA_TO_TYPE_QZONE.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_sensitive);
        } else if (Constants.VIA_SHARE_TYPE_TEXT.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_unknow);
        }
        this.skinText.setText(skinType);
        Intent intent = getIntent();
        float progress = intent.getFloatExtra("current_water_value", com.mlizhi.utils.Constants.BODY_PART_MIN);
        this.tempNurserType = intent.getIntExtra("nurser_type", 0);
        if (this.tempNurserType == 21) {
            this.nurserTypeTextView.setText(R.string.flag4pre);
        } else if (this.tempNurserType == 22) {
            this.nurserTypeTextView.setText(R.string.flag4post);
        }
        this.waterRangeView.setProgress(progress, com.mlizhi.utils.Constants.BODY_PART_MIN, com.mlizhi.utils.Constants.BODY_PART_MAX);
        try {
            partType = Integer.parseInt(this.mlzApplication.getGlobalVariable("current_part_type"));
        } catch (NumberFormatException e2) {
            partType = 19;
        }
        String[] suggestArr = null;
        String[] analysisArr = null;
        if (partType == 17) {
            this.positionText.setText(R.string.bodyParts4face);
            this.waterRangeView.setMeasureWidth(com.mlizhi.utils.Constants.BODY_PART_MIN, com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MIN, com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MAX, com.mlizhi.utils.Constants.BODY_PART_MAX, true);
            suggestArr = getResources().getStringArray(R.array.suggest_face);
            if (com.mlizhi.utils.Constants.BODY_PART_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MIN) {
                analysisArr = getResources().getStringArray(R.array.analysis_dry);
            } else if (com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_normal);
            } else if (com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MAX <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_moist);
            }
            if (((int) (progress - 37.0f)) >= 0) {
               // this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_large), new Object[]{Integer.valueOf(compareAgeValue)}));
            } else {
               // this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_small), new Object[]{Integer.valueOf(0 - compareAgeValue)}));
            }
        } else if (partType == 18) {
            this.positionText.setText(R.string.bodyParts4eye);
            this.waterRangeView.setMeasureWidth(com.mlizhi.utils.Constants.BODY_PART_MIN, com.mlizhi.utils.Constants.BODY_PART_EYE_MORMAL_MIN, com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX, com.mlizhi.utils.Constants.BODY_PART_MAX, true);
            suggestArr = getResources().getStringArray(R.array.suggest_eye);
            if (com.mlizhi.utils.Constants.BODY_PART_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_EYE_MORMAL_MIN) {
                analysisArr = getResources().getStringArray(R.array.analysis_dry);
            } else if (com.mlizhi.utils.Constants.BODY_PART_EYE_MORMAL_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_normal);
            } else if (com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_moist);
            }
            if (((int) (progress - com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MAX)) >= 0) {
              //  this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_large), new Object[]{Integer.valueOf(compareAgeValue)}));
            } else {
              //  this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_small), new Object[]{Integer.valueOf(0 - compareAgeValue)}));
            }
        } else if (partType == 19) {
            this.positionText.setText(R.string.bodyParts4hand);
            this.waterRangeView.setMeasureWidth(com.mlizhi.utils.Constants.BODY_PART_MIN, com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MIN, com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MAX, com.mlizhi.utils.Constants.BODY_PART_MAX, true);
            suggestArr = getResources().getStringArray(R.array.suggest_hand);
            if (com.mlizhi.utils.Constants.BODY_PART_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MIN) {
                analysisArr = getResources().getStringArray(R.array.analysis_dry);
            } else if (com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_normal);
            } else if (com.mlizhi.utils.Constants.BODY_PART_HAND_MORMAL_MAX <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_moist);
            }
            if (((int) (progress - com.mlizhi.utils.Constants.BODY_PART_FACE_MORMAL_MIN)) >= 0) {
               // this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_large), new Object[]{Integer.valueOf(compareAgeValue)}));
            } else {
               // this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_small), new Object[]{Integer.valueOf(0 - compareAgeValue)}));
            }
        } else if (partType == 20) {
            this.positionText.setText(R.string.bodyParts4neck);
            this.waterRangeView.setMeasureWidth(com.mlizhi.utils.Constants.BODY_PART_MIN, com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MIN, com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX, com.mlizhi.utils.Constants.BODY_PART_MAX, true);
            suggestArr = getResources().getStringArray(R.array.suggest_neck);
            if (com.mlizhi.utils.Constants.BODY_PART_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MIN) {
                analysisArr = getResources().getStringArray(R.array.analysis_dry);
            } else if (com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MIN <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_normal);
            } else if (com.mlizhi.utils.Constants.BODY_PART_NECK_MORMAL_MAX <= progress && progress < com.mlizhi.utils.Constants.BODY_PART_MAX) {
                analysisArr = getResources().getStringArray(R.array.analysis_moist);
            }
            if (((int) (progress - 39.5f)) >= 0) {
                //this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_large), new Object[]{Integer.valueOf(compareAgeValue)}));
            } else {
               // this.compareAgeTv.setText(String.format(getResources().getString(R.string.detect_result_compare_age_small), new Object[]{Integer.valueOf(0 - compareAgeValue)}));
            }
        }
        if (analysisArr != null && analysisArr.length > 0) {
            this.conditionValue.setText(analysisArr[Math.abs(new Random().nextInt()) % analysisArr.length]);
        }
        if (suggestArr != null && suggestArr.length > 0) {
            this.recommandText.setText(suggestArr[Math.abs(new Random().nextInt()) % suggestArr.length]);
        }
    }

    public void onSpecViewClick(View view) {
        switch (view.getId()) {
            case R.id.id_detect_result_back:
                finish();
            default:
        }
    }
}
