package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/3/18.
 */

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.IThemeChange;

/**
 * 创建人: mmrx
 * 时间: 16/3/18下午2:28
 * 描述: 用于动态改变view主题的工具类
 */
public class ViewAttributeUtil {

    private static final String TAG = "ViewAttributeUtilLog";

    /**
     * 切换当前view/viewGroup的主题
     * @param rootView
     * @param theme
     */
    public static void changeTheme(View rootView,Resources.Theme theme){
        if(rootView instanceof IThemeChange){
            ((IThemeChange)rootView).setTheme(theme);
            //递归调用
            if(rootView instanceof ViewGroup){
                int count = ((ViewGroup) rootView).getChildCount();
                for(int i=0;i<count;i++)
                    changeTheme(((ViewGroup)rootView).getChildAt(i),theme);
            }
        }
    }

    /**
     * 获取在AttributeSet集合中特定名称的变量的值
     * 该AttributeSet对象属于某个特定的Theme对象
     * @param attrs
     * @param paramInt Res id
     * @return 如果包含所要查找的属性值,则返回-1
     */
    public static int getAttributeValue(AttributeSet attrs,int paramInt){
        int value = -1;
        int count = attrs.getAttributeCount();
        for(int i=0;i<count;i++){
            if(attrs.getAttributeNameResource(i) == paramInt){
                String str = attrs.getAttributeValue(i);
                //以?开头表示是定义在attrs文件中的一个变量
                if(null != str && str.startsWith("?")){
                    value = Integer.valueOf(str.substring(1,str.length())).intValue();
                    return value;
                }
            }
        }
        return value;
    }

    /*====================================获取特定属性的属性值===============================*/

    /**
     * 获取背景色
     * @param attrs
     * @return
     */
    public static  int getBackgroundAttributeValue(AttributeSet attrs){
        return getAttributeValue(attrs, R.attr.custom_background);
    }

    /**
     * 获取字体颜色
     * @param attrs
     * @return
     */
    public static int getTextColorAttributeValue(AttributeSet attrs){
        return getAttributeValue(attrs, R.attr.custom_textcolor);
    }

    /*====================================设置特定属性的属性值===============================*/

    /**
     * 应用背景
     * @param itc
     * @param theme
     * @param paramId
     */
    public static void applyBackGroundDrawable(IThemeChange itc,Resources.Theme theme,int paramId){
        //利用theme对象构建出一个ta数组
        TypedArray ta = theme.obtainStyledAttributes(new int[paramId]);
        Drawable drawable = ta.getDrawable(0);
        if(null != itc){
            (itc.getView()).setBackground(drawable);
        }
        ta.recycle();
    }

    /**
     * 应用字体颜色,目前仅用于TextVIew
     * @param itc
     * @param theme
     * @param paramId
     */
    public static void applyTextColor(IThemeChange itc,Resources.Theme theme,int paramId){
        //利用theme对象构建出一个ta数组
        TypedArray ta = theme.obtainStyledAttributes(new int[paramId]);
        int resId = ta.getColor(0, 0);
        if(null != itc && itc instanceof TextView){
            ((TextView)(itc.getView())).setTextColor(resId);
        }
        ta.recycle();
    }

//    private static boolean apiLevelLowerThan21(){
//        if(Build.VERSION.SDK_INT >= 21)
//            return true;
//        L.e(TAG,"当前系统版本号低于21,不支持动态更换主题");
//        return false;
//    }
}
