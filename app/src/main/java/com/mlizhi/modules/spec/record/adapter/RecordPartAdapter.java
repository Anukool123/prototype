package com.mlizhi.modules.spec.record.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mlizhi.utils.Constants;

import java.util.HashMap;
import java.util.List;

public class RecordPartAdapter extends BaseAdapter {
    private ViewHolder holder;
    private LayoutInflater mInflater;
    private List<HashMap<String, String>> recordPartList;

    static class ViewHolder {
        ImageView categoryImage;
        TextView conditonValue;
        TextView resultCompareLabel;
        SeekBar resultCompareProcess;
        TextView resultCompareValue;
        TextView waterValValue;

        ViewHolder() {
        }
    }

    public RecordPartAdapter(Context context) {
        this.holder = null;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public List<HashMap<String, String>> getRecordPartList() {
        return this.recordPartList;
    }

    public void setRecordPartList(List<HashMap<String, String>> recordPartList) {
        this.recordPartList = recordPartList;
    }

    public int getCount() {
        return this.recordPartList.size();
    }

    public Object getItem(int position) {
        return this.recordPartList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int mapType;
        float waterValue;
        HashMap<String, String> map = (HashMap) this.recordPartList.get(position);
        if (convertView == null) {
            this.holder = new ViewHolder();
            convertView = this.mInflater.inflate(R.layout.activity_record_category_item, null);
            this.holder.categoryImage = (ImageView) convertView.findViewById(R.id.id_category_image);
            this.holder.resultCompareLabel = (TextView) convertView.findViewById(R.id.id_result_compare_label);
            this.holder.conditonValue = (TextView) convertView.findViewById(R.id.id_conditon_value);
            this.holder.waterValValue = (TextView) convertView.findViewById(R.id.id_water_val_value);
            this.holder.resultCompareValue = (TextView) convertView.findViewById(R.id.id_result_compare_value);
            this.holder.resultCompareProcess = (SeekBar) convertView.findViewById(R.id.id_compare_process);
            convertView.setTag(this.holder);
        } else {
            this.holder = (ViewHolder) convertView.getTag();
        }
        try {
            mapType = Integer.parseInt((String) map.get(Constants.BODY_PART_TYPE));
        } catch (NumberFormatException e) {
            mapType = 0;
        }
        try {
            waterValue = Float.parseFloat((String) map.get(Constants.BODY_PART_WATER_VALUE));
            if (waterValue == 0.0f) {
                waterValue = Constants.BODY_PART_MIN;
            }
        } catch (NumberFormatException e2) {
            waterValue = Constants.BODY_PART_MIN;
        }
        if (18 == mapType) {
            this.holder.categoryImage.setImageResource(R.drawable.ic_part_eye_pressed);
            if (Constants.BODY_PART_MIN < waterValue && waterValue < Constants.BODY_PART_EYE_MORMAL_MIN) {
                this.holder.conditonValue.setText(R.string.skin_health4dry);
            } else if (Constants.BODY_PART_EYE_MORMAL_MIN < waterValue && waterValue < Constants.BODY_PART_NECK_MORMAL_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4normal);
            } else if (Constants.BODY_PART_NECK_MORMAL_MAX >= waterValue || waterValue >= Constants.BODY_PART_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4none);
            } else {
                this.holder.conditonValue.setText(R.string.skin_health4moist);
            }
        } else if (17 == mapType) {
            this.holder.categoryImage.setImageResource(R.drawable.ic_part_face_pressed);
            if (Constants.BODY_PART_MIN < waterValue && waterValue < Constants.BODY_PART_FACE_MORMAL_MIN) {
                this.holder.conditonValue.setText(R.string.skin_health4dry);
            } else if (Constants.BODY_PART_FACE_MORMAL_MIN < waterValue && waterValue < Constants.BODY_PART_FACE_MORMAL_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4normal);
            } else if (Constants.BODY_PART_FACE_MORMAL_MAX >= waterValue || waterValue >= Constants.BODY_PART_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4none);
            } else {
                this.holder.conditonValue.setText(R.string.skin_health4moist);
            }
        } else if (19 == mapType) {
            this.holder.categoryImage.setImageResource(R.drawable.ic_part_hand_pressed);
            if (Constants.BODY_PART_MIN < waterValue && waterValue < Constants.BODY_PART_HAND_MORMAL_MIN) {
                this.holder.conditonValue.setText(R.string.skin_health4dry);
            } else if (Constants.BODY_PART_HAND_MORMAL_MIN < waterValue && waterValue < Constants.BODY_PART_HAND_MORMAL_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4normal);
            } else if (Constants.BODY_PART_HAND_MORMAL_MAX >= waterValue || waterValue >= Constants.BODY_PART_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4none);
            } else {
                this.holder.conditonValue.setText(R.string.skin_health4moist);
            }
        } else if (20 == mapType) {
            this.holder.categoryImage.setImageResource(R.drawable.ic_part_neck_pressed);
            if (Constants.BODY_PART_MIN < waterValue && waterValue < Constants.BODY_PART_NECK_MORMAL_MIN) {
                this.holder.conditonValue.setText(R.string.skin_health4dry);
            } else if (Constants.BODY_PART_NECK_MORMAL_MIN < waterValue && waterValue < Constants.BODY_PART_NECK_MORMAL_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4normal);
            } else if (Constants.BODY_PART_NECK_MORMAL_MAX >= waterValue || waterValue >= Constants.BODY_PART_MAX) {
                this.holder.conditonValue.setText(R.string.skin_health4none);
            } else {
                this.holder.conditonValue.setText(R.string.skin_health4moist);
            }
        }
        this.holder.resultCompareLabel.setText((CharSequence) map.get(Constants.BODY_PART_COMPARE_LABEL));
        this.holder.waterValValue.setText((CharSequence) map.get(Constants.BODY_PART_WATER_VALUE));
        this.holder.resultCompareValue.setText((CharSequence) map.get(Constants.BODY_PART_COMPARE_VALUE));
        this.holder.resultCompareProcess.setProgress((int) (((waterValue - Constants.BODY_PART_MIN) / Constants.BODY_PART_FACE_MORMAL_MAX) * 100.0f));
        this.holder.resultCompareProcess.setEnabled(false);
        return convertView;
    }
}
