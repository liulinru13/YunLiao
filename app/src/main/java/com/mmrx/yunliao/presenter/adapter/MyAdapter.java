package com.mmrx.yunliao.presenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * Created by Administrator on 2015/8/10.
 */
public abstract class MyAdapter<T> extends BaseAdapter{

    protected LayoutInflater mLayoutInflater;
    protected Context mContext;
    protected List<T> mList;
    protected int mItemLayoutId;
    public MyAdapter(Context context,List<T> list,int itemLayoutId){
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public T getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = null;
        MyViewHolder myViewHolder = MyViewHolder.get(mContext,parent,convertView,
                this.mItemLayoutId,position);
////        if (convertView == null){
////            convertView = mLayoutInflater.inflate(R.layout.list_item_layout,parent,false);
////            viewHolder= new ViewHolder();
////            viewHolder.mTitle = (TextView)convertView.findViewById(R.id.title);
////            viewHolder.mContent = (TextView)convertView.findViewById(R.id.content);
////            convertView.setTag(viewHolder);
////        }else{
////            viewHolder = (ViewHolder)convertView.getTag();
////        }
//        TextView title_tv = (TextView)myViewHolder.getView(R.id.title);
//        TextView content_tv = (TextView)myViewHolder.getView(R.id.content);
//
////        viewHolder.mTitle.setText("title "+position);
////        viewHolder.mContent.setText("Content "+position);
//        title_tv.setText("title "+position);
//        content_tv.setText("Content "+position);
////        return convertView;
        convert(myViewHolder,getItem(position));

        return myViewHolder.getConvertView();
    }

    /**
     * getView()��Ϊitem�������,��Ҫʵ��
     * @param item
     * @param myViewHolder
     * */
    protected abstract void convert(MyViewHolder myViewHolder,T item);

//    static class ViewHolder{
//        public TextView mTitle;
//        public TextView mContent;
//    }
}
