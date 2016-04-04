package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/3/7.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mmrx.yunliao.R;

/**
 * 创建人: mmrx
 * 时间: 16/3/7上午10:15
 * 描述:  自定义对话框类
 */
public class CustomDialog extends DialogFragment{

    private static CustomDialogListener mCustomListener;
    private Integer mLayoutId;//布局
    private View mContentView;//自定义布局中的内容view
    private boolean mPosiBnShow = true;
    private boolean mNegaBnShow = true;
    public CustomDialog(){
    }
    /**
     * 获取对话框实例
     * @param title 标题
     * @param message 对话框内容
     * @param listener 监听器
     * */
    public CustomDialog newInstance(String title, String message, CustomDialogListener listener){
        CustomDialog dialog = new CustomDialog();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        dialog.mLayoutId = R.layout.layout_custom_dialog;
        dialog.setArguments(b);
        dialog.mCustomListener = listener;
        return dialog;
    }
    /**
     * 获取对话框实例
     * @param title 标题
     * @param message 对话框内容
     * @param listener 监听器
     * @param posiShow 确定按钮是否显示
     * @param negaShwo 取消按钮是否显示
     * */
    public CustomDialog newInstance(String title, String message, CustomDialogListener listener,
                                    boolean posiShow,boolean negaShwo){
        CustomDialog dialog = new CustomDialog();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        dialog.mLayoutId = R.layout.layout_custom_dialog;
        dialog.setArguments(b);
        dialog.mCustomListener = listener;
        dialog.mPosiBnShow = posiShow;
        dialog.mNegaBnShow = negaShwo;
        return dialog;
    }

    /**
     * 获取对话框实例
     * @param title 标题
     * @param message 对话框内容
     * @param contentView 自定义布局
     * @param listener 监听器
     * */
    public CustomDialog newInstance(String title, String message,
                            View contentView,CustomDialogListener listener){
        CustomDialog dialog = new CustomDialog();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        dialog.mLayoutId = R.layout.layout_custom_dialog_view;
        dialog.mContentView = contentView;
        dialog.setArguments(b);
        dialog.mCustomListener = listener;
        return dialog;
    }

    /**
     * 获取对话框实例
     * @param title 标题
     * @param message 对话框内容
     * @param contentView 自定义布局
     * @param listener 监听器
     * @param posiShow 确定按钮是否显示
     * @param negaShwo 取消按钮是否显示
     * */
    public CustomDialog newInstance(String title, String message,
                                    View contentView,
                                    CustomDialogListener listener,
                                    boolean posiShow,boolean negaShwo){
        CustomDialog dialog = new CustomDialog();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        dialog.mLayoutId = R.layout.layout_custom_dialog_view;
        dialog.mContentView = contentView;
        dialog.setArguments(b);
        dialog.mCustomListener = listener;
        dialog.mPosiBnShow = posiShow;
        dialog.mNegaBnShow = negaShwo;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Base_Theme_AppCompat_Dialog);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(mLayoutId == null){
            mLayoutId = R.layout.layout_custom_dialog;
        }
        View view = inflater.inflate(mLayoutId,null);
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        if (title != null && title.length() > 0) {
            TextView t = (TextView) view.findViewById(R.id.custom_dialog_title);
            t.setText(title);
        }
        if(mLayoutId == R.layout.layout_custom_dialog){
            if (message != null && message.length() > 0) {
                TextView m = (TextView) view
                        .findViewById(R.id.custom_dialog_message);
                m.setText(message);
            }
        }
        else{
            LinearLayout viewLayout = (LinearLayout)view.findViewById(R.id.custom_dialog_view_layout);
            viewLayout.addView(mContentView);
        }
        View ok = view.findViewById(R.id.custom_dialog_posi_bn);
        View cancel = view.findViewById(R.id.custom_dialog_nega_bn);

        if(mPosiBnShow)
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCustomListener != null) {
                        mCustomListener.doPositiveClick();
                    }
                    dialog.dismiss();
                }
            });
        else
            ok.setVisibility(View.GONE);
        if(mNegaBnShow)
            cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mCustomListener != null) {
                        mCustomListener.doNegativeClick();
                    }
                    dialog.dismiss();
                }

            });
        else
            cancel.setVisibility(View.GONE);
        dialog.setContentView(view);
        return dialog;
    }
    /**
     * 对话框监听器接口
     * */
    public static interface CustomDialogListener{
        void doNegativeClick();
        void doPositiveClick();
    }
}

