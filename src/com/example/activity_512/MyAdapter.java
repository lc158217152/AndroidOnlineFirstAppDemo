package com.example.activity_512;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MyAdapter extends BaseAdapter {
	Context mContext;
	List<PackageInfo> apps;

	boolean[] ischeck;// 这个变量作为checkbox状态标志，与checkbox一一对应

	public MyAdapter(Context context, List<PackageInfo> list) {
		mContext = context;
		apps = list;

		ischeck = new boolean[apps.size()];// 根据list长度生成数组
	}

	// 设定指定checkbox状态
	public void setbox(int _id) {
		if (ischeck[_id]) {
			ischeck[_id] = false;
		} else {
			ischeck[_id] = true;
		}
		notifyDataSetChanged();
	}

	// 获取指定checkbox状态
	public boolean getbox(int _id) {
		return ischeck[_id];
	}

	// 获取checkbox状态数组
	public boolean[] getCheck() {
		return ischeck;
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public Object getItem(int position) {
		return apps.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		LinearLayout layout = new LinearLayout(mContext);
		// 应用名
		TextView appname = new TextView(mContext);
		appname.setText(apps.get(position).applicationInfo.loadLabel(
				mContext.getPackageManager()).toString());
		appname.setTextSize(24);
		// 应用logo
		ImageView icon = new ImageView(mContext);
		icon.setImageDrawable(apps.get(position).applicationInfo
				.loadIcon(mContext.getPackageManager()));
		// 包名
		TextView packagename = new TextView(mContext);
		packagename.setText(apps.get(position).packageName);
		// 版本名
		TextView vName = new TextView(mContext);
		vName.setText(apps.get(position).versionName);
		// 版本号
		// TextView versionCode = new TextView(mContext);
		// versionCode.setText(apps.get(position).versionCode);

		CheckBox cb = new CheckBox(mContext);
		cb.setFocusable(false);// 关闭焦点

		layout.addView(cb);
		layout.addView(icon);
		layout.addView(appname);
		layout.addView(packagename);
		layout.addView(vName);
		// layout.addView(versionCode);

		return layout;

	}

	public void updataList(List<PackageInfo> list) {
		apps = list;
		ischeck = new boolean[apps.size()];// 重新设定ischeck长度
		notifyDataSetChanged();
	}

}
