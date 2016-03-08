package com.mmrx.yunliao.presenter.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mmrx on 2015/8/10.
 */
public class MyViewHolder {
    private SparseArray<View> myViews;
    private View mConvertView;


    private MyViewHolder(Context context,ViewGroup parent,int position,int layoutID){
        this.myViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutID,parent,false);
        mConvertView.setTag(this);
    }
    /**
     * @param context
     * @param convertView
     * @param layoutId
     * @param parent
     * @param position
     * @return MyViewHolder
     * */
    public static MyViewHolder get(Context context,ViewGroup parent,
                            View convertView,int layoutId,int position){
        if(convertView !=null ){
            return (MyViewHolder)convertView.getTag();
        }
        return new MyViewHolder(context,parent,position,layoutId);
    }

    /**
     * @param viewId
     * @return View
     * */
    public View getView(int viewId){
        View v = myViews.get(viewId);
        if(v != null){
            return v;
        }
        v = this.mConvertView.findViewById(viewId);
        myViews.put(viewId,v);
        return v;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * @param id Res控件id
     * @param content 显示文本
     * @return MyViewHolder
     * */
    public MyViewHolder setTextView(int id,String content){
        TextView tv = (TextView)getView(id);
        tv.setText(content);
        return this;
    }

    /**
     * @param id Res控件id
     * @param rid 图片id
     * @return MyViewHolder
     * */
    public MyViewHolder setImageByRes(int id,int rid){
        ImageView image = (ImageView)getView(id);
        image.setImageResource(rid);
        return this;
    }
}
