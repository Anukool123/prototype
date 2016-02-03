package com.mlizhi.modules.spec.content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlizhi.base.imageloader.core.DisplayImageOptions;
import com.mlizhi.base.imageloader.core.DisplayImageOptions.Builder;
import com.mlizhi.base.imageloader.core.ImageLoader;
import com.mlizhi.utils.Constants;

import java.util.HashMap;
import java.util.List;

public class ContentAdapter extends BaseAdapter {
    private List<HashMap<String, String>> contentList;
    DisplayImageOptions displayImageOptions;
    private LayoutInflater mInflater;
    private int time;

    static class ViewHolder {
        public TextView contentTitle;
        public ImageView contentTitleImage;
        public TextView customerBrowse;
        public TextView customerPraise;

        ViewHolder() {
        }
    }

    public ContentAdapter(Context context) {
        this.displayImageOptions = null;
        this.mInflater = LayoutInflater.from(context);
        this.displayImageOptions = new Builder().showImageOnLoading((int) R.drawable.ic_content_preview_holder).showImageForEmptyUri((int) R.drawable.ic_content_preview_holder).showImageOnFail((int) R.drawable.ic_content_preview_holder).cacheInMemory(false).cacheOnDisk(true).build();
    }

    public List<HashMap<String, String>> getContentList() {
        return this.contentList;
    }

    public void setContentList(List<HashMap<String, String>> contentList) {
        this.contentList = contentList;
    }

    public int getCount() {
        return this.contentList.size();
    }

    public Object getItem(int position) {
        return this.contentList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.activity_spec_content_item, null);
            holder = new ViewHolder();
            holder.contentTitleImage = (ImageView) convertView.findViewById(R.id.content_title_image_item);
            holder.contentTitle = (TextView) convertView.findViewById(R.id.content_title_item);
            holder.customerPraise = (TextView) convertView.findViewById(R.id.content_customer_operator_praise_value);
            holder.customerBrowse = (TextView) convertView.findViewById(R.id.content_customer_operator_browse_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> contentHashMap = (HashMap) this.contentList.get(position);
        ImageLoader.getInstance().displayImage((String) contentHashMap.get(Constants.CONTENT_ITEM_IMAGE), holder.contentTitleImage, this.displayImageOptions);
        holder.contentTitle.setText((CharSequence) contentHashMap.get(Constants.CONTENT_ITEM_TITLE));
        holder.customerPraise.setText((CharSequence) contentHashMap.get(Constants.CONTENT_ITEM_PRAISE_NUM));
        holder.customerBrowse.setText((CharSequence) contentHashMap.get(Constants.CONTENT_ITEM_VIEW_NUM));
        return convertView;
    }
}
