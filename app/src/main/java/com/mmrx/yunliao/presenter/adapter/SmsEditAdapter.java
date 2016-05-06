package com.mmrx.yunliao.presenter.adapter;/**
 * Created by mmrx on 16/5/3.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.bean.sms.SmsBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 创建人: mmrx
 * 时间: 16/5/3下午12:45
 * 描述:
 */
public class SmsEditAdapter extends BaseAdapter {

    private final String TAG = "SmsListAdapterLog";

    private final int leftId = R.layout.component_left_list_item_sms_edit;
    private final int rightId = R.layout.component_right_list_item_sms_edit;

    private List<SmsBean> mList;
    private Set<Integer> mBeanSelectIdArr;
    private Context mContext;

    private boolean iconShow = false;

    public SmsEditAdapter(Context mContext, List<SmsBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mBeanSelectIdArr = new HashSet<Integer>() {
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mBeanSelectIdArr.clear();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList != null && position < getCount() ?
                mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        SmsBean bean = mList.get(position);
        if(convertView == null){
            View view = null;
            vh = new ViewHolder();
            view = getConvertView(bean.getType(),vh);
            if(view != null){
                getViewHolder(view,vh);
                convertView = view;
                convertView.setTag(vh);
            }else {
                return null;
            }
        }
        else{
            vh = (ViewHolder)convertView.getTag();
            if(getItemType(bean) != vh.type){
                vh = new ViewHolder();
                convertView = getConvertView(bean.getType(),vh);
                getViewHolder(convertView,vh);
                convertView.setTag(vh);
            }
        }
        //收件
        if(bean.getType() == 1) {
            vh.date.setText(bean.getDate_str());
            vh.content.setBackgroundColor(Color.GRAY);
        }
        //发件
        else{
            vh.date.setText(bean.getDate_sent_str());
            if(bean.getType() == 4)//正常发送
                vh.content.setBackgroundColor(Color.parseColor("#03de69"));
            else if(bean.getType() == 5)//发送失败的
                vh.content.setBackgroundColor(Color.RED);
        }
        //填充内容
        vh.content.setText(bean.getBody());
        //填充状态
        if(bean.getLocked() == 1){
            vh.status.setVisibility(View.VISIBLE);
            vh.status.setText("锁定");
        }else{
            vh.status.setVisibility(View.GONE);
        }

        //初始化为未选中状态
        vh.select.setTag(new SelectTag(bean.get_id(),false));
        //是否显示选择图标
        if(iconShow){
            vh.select.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(R.drawable.ic_launcher)
                    .into(vh.select);
            vh.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectTag tag = (SelectTag)v.getTag();
                    //选中状态点击,变成未选中状态,记录bean的id
                    if(tag.select){
                        Glide.with(mContext)
                                .load(R.drawable.ic_launcher)
                                .into((ImageView)v);
                        tag.select = false;
                        mBeanSelectIdArr.add(tag.position);
                        v.setTag(tag);
                    }else{
                        v.setBackgroundColor(Color.RED);
                        tag.select = true;
                        mBeanSelectIdArr.remove(tag.position);
                        v.setTag(tag);
                    }

                }
            });

        }else{
            vh.select.setVisibility(View.GONE);
        }

        return convertView;
    }

    private View getConvertView(int type,ViewHolder vh){
        View view = null;
        if(type == 1){
            view = LayoutInflater.from(mContext).inflate(leftId,null);
            vh.type = 1;
        }
        //发件 发送失败
        else if(type == 4 || type == 5){
            view = LayoutInflater.from(mContext).inflate(rightId,null);
            vh.type = 2;
        }

        return view;
    }

    private int getItemType(SmsBean bean){
        if(bean.getType() ==1)
            return 1;
        else
            return 2;
    }

    private void getViewHolder(View view,ViewHolder vh){
        vh.date = (TextView)view.findViewById(R.id.custom_sms_edit_item_date);
        vh.content = (TextView)view.findViewById(R.id.custom_sms_edit_item_content);
        vh.select = (ImageView)view.findViewById(R.id.custom_sms_edit_item_icon);
        vh.status = (TextView)view.findViewById(R.id.custom_sms_edit_item_content_status);
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (mList.get(position).getType() == 1)
//            return 1;
//        else
//            return 2;
//    }

    public boolean isIconShow() {
        return iconShow;
    }

    public void setIconShow(boolean iconShow) {
        this.iconShow = iconShow;
        notifyDataSetChanged();
    }

    public Set<Integer> getmBeanSelectIdArr() {
        return mBeanSelectIdArr;
    }

    static class ViewHolder{
        TextView date;
        ImageView select;
        TextView content;
        TextView status;
        int type;//1 是左 2 是右
    }

    static class SelectTag{
        int position;
        boolean select;

        public SelectTag(int position, boolean select) {
            this.position = position;
            this.select = select;
        }
    }
}
