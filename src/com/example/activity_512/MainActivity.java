package com.example.activity_512;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

public class MainActivity extends Activity {
	TabHost tabHost;
	LinearLayout ll, lls;
	ListView lv, lvs;

	ActivityManager am;
	// 取得安装的所有软件信息
	List<PackageInfo> allPackageInfos;
	List<PackageInfo> packages;

	List<PackageInfo> pack;

	public static long total_memory = 0; // 总内存
	public static long memSize = 0;// 可用内存
	Context context;
	ActivityManager mActivityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 初始化时先要得到当前的所有进程 (|逻辑或 &逻辑与 通常用来做位运算)
		allPackageInfos = getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES);

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();

		// 添加列表
		tabHost.addTab(tabHost.newTabSpec("spec1").setIndicator("运行程序")
				.setContent(R.id.linearLayout1));
		tabHost.addTab(tabHost.newTabSpec("spec2").setIndicator("所有程序")
				.setContent(R.id.linearLayout2));

		ll = (LinearLayout) findViewById(R.id.linearLayout1);

		Button remove = new Button(this);
		remove.setText("删除");

		lv = new ListView(this);
		final MyAdapter ma = new MyAdapter(this, app());
		lv.setAdapter(ma);
		ll.addView(remove);
		ll.addView(lv);

		lls = (LinearLayout) findViewById(R.id.linearLayout2);
		lvs = new ListView(this);
		MyAdapter mas = new MyAdapter(this, apps());
		lvs.setAdapter(mas);
		lls.addView(lvs);

		remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < ma.getCheck().length; i++) {
					if (ma.getbox(i)) {
						killProcess(packages.get(i).packageName);
					}
				}
				// am.killBackgroundProcesses("com.tencent.qqpimsecure");// err
				// Toast.makeText(getApplicationContext(),
				// "已经关闭：" + packages.get(2).packageName, 1).show();
				// pack = app();
				// ma.updataList(pack);

				packages = app();
				ma.updataList(packages);
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 这三行是需要的
				// ma.setbox(position);
				// for (int i = 0; i < ma.getCheck().length; i++) {
				// }

				deleteApps(getPackageName());// 卸载

				// am.killBackgroundProcesses(packages.get(position).packageName);
				// packages = app();
				// ma.updataList(packages);
			}
		});
	}

	public List<PackageInfo> apps() {
		for (int i = 0; i < allPackageInfos.size(); i++) {
			// Toast.makeText(getApplicationContext(), "所有数据:"
			// +allPackageInfos.get(i).packageName, 1).show();
			System.out.println("所有数据:" + allPackageInfos.get(i).packageName);
		}
		return allPackageInfos;

	}

	public List<PackageInfo> app() {

		// 通过getSystemService，获取到ActivityManager对象，对象中包含了系统中activity的信息
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 通过am对象过滤出当前系统中正在运行的APP进程数据
		List<RunningAppProcessInfo> li = am.getRunningAppProcesses();

		// 用来存放包数据 通过li，过滤出当前正在运行的APP进程中包的名字
		packages = new ArrayList<PackageInfo>();

		// 现在开始过滤
		for (int i = 0; i < allPackageInfos.size(); i++) {// 遍历所有的APP进程

			for (int j = 0; j < li.size(); j++) {// 遍历所有的正在运行的APP进程

				// 为了做比对，有些时候可能因为我们个人的操作，或者系统自身的管理（因为内补足，可能会在某个时间自动关闭程序并回收资源）
				if (allPackageInfos.get(i).packageName
						.equals(li.get(j).processName)
						&& (allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0
						&& ((allPackageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
					// packages.add(allPackageInfos.get(i).packageName);
					packages.add(allPackageInfos.get(i));
				}
			}
		}
		return packages;
	}

	// 后台关闭进程
	public boolean killProcess(String _packagename) {
		List<RunningAppProcessInfo> runningAppProcessInfos = am
				.getRunningAppProcesses();
		for (int i = 0; i < runningAppProcessInfos.size(); i++) {
			if (!runningAppProcessInfos.get(i).processName
					.equals("com.example.activity_512")// 不能杀掉自己
					&& !runningAppProcessInfos.get(i).processName
							.equals("com.miui.home")// 不能杀掉启动器（这个根据个人情况添加内容）
					&& runningAppProcessInfos.get(i).processName // 确定你要关闭的程序还在运行当中
							.equals(_packagename)) {
				am.killBackgroundProcesses(_packagename);
				return true;
			}
		}
		return false; // 关闭失败（关闭的是自己，启动器，已经被回收的程序）
	}

	// 得到总内存大小
	public String getTotalMemorys() {
		// 系统内存信息文件
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			// 读取meminfo第一行，系统总内存大小
			str2 = localBufferedReader.readLine();
			// 以空格分隔
			arrayOfString = str2.split("\\s+");
			// 获得系统总内存，单位是KB，乘以1024转换为Byte
			total_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
			localBufferedReader.close();
		} catch (IOException e) {
		}
		// Byte转换为KB或者MB，内存大小规格化
		return Formatter.formatFileSize(context, total_memory);
	}

	// 格式化内存格式
	private String formateFileSize(long size) {
		return Formatter.formatFileSize(context, size);
	}

	// 获取可用内存
	public String getSystemAvaialbeMemory() {
		// 获得MemoryInfo对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 获得系统可用内存，保存在MemoryInfo对象上
		mActivityManager.getMemoryInfo(memoryInfo);
		memSize = memoryInfo.availMem;

		// 字符类型转换
		String availMemStr = formateFileSize(memSize);

		return availMemStr;
	}

	// 删除应用程序
	public void deleteApps(String packageName) {
		Uri uri = Uri.fromParts("package", packageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		startActivityForResult(it, 0);
	}

}
