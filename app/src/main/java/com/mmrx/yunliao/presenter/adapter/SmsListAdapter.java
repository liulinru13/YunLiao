package com.mmrx.yunliao.presenter.adapter;/**
 * Created by mmrx on 16/4/5.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.util.L;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 创建人: mmrx
 * 时间: 16/4/5下午6:40
 * 描述: 短信列表适配器
 */
public class SmsListAdapter extends BaseAdapter{

    private final String TAG = "SmsListAdapterLog";

    private List<ISmsListBean> mList;
    private Context mContext;
    private ISwipeButtonClickListener mListener;
    public SmsListAdapter(Context context,List<ISmsListBean> list){
        this.mList = list;
        this.mContext = context;
    }

    public SmsListAdapter(Context context,List<ISmsListBean> list,
                          ISwipeButtonClickListener listener){
        this.mList = list;
        this.mContext = context;
        this.mListener = listener;
    }

    public void setSwipeListViewButtonListener(ISwipeButtonClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position < mList.size() ? mList.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.component_list_item_sms,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }
        AutoUtils.autoSize(convertView);
        ISmsListBean bean = (ISmsListBean)getItem(position);
        vh.addr.setText(bean.getContacts());
        vh.content.setText(bean.getSnippet());
        vh.date.setText(bean.getDate());

//        vh.unread.setVisibility(bean.isRead() ? View.GONE : View.VISIBLE);
        vh.mark.setClickable(!bean.isRead());

        vh.mark.setTag(position);
        vh.delete.setTag(position);
        vh.mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = (Integer) v.getTag();
                L.i(TAG, "mark position is " + posi);
                if(SmsListAdapter.this.mListener != null)
                    SmsListAdapter.this.mListener.onMarkBnClicked(posi);
            }
        });
        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = (Integer)v.getTag();
                L.i(TAG,"delete position is " +posi);
                if(SmsListAdapter.this.mListener != null)
                    SmsListAdapter.this.mListener.onDeleteBnClicked(posi);
            }
        });

        return convertView;
    }

    class ViewHolder{

        @Bind(R.id.sms_item_addr_tv)
        TextView addr;
        @Bind(R.id.sms_item_content)
        TextView content;
        @Bind(R.id.sms_item_date_tv)
        TextView date;
        @Bind(R.id.sms_item_mark)
        TextView mark;
        @Bind(R.id.sms_item_delete)
        TextView delete;
//        @Bind(R.id.sms_itme_unread_icon)
//        ImageView unread;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    public interface ISwipeButtonClickListener{
        /**
         * 删除按钮按下
         * @param position
         */
        void onDeleteBnClicked(int position);

        /**
         * 标记已读按钮按下
         * @param position
         */
        void onMarkBnClicked(int position);
    }
}
