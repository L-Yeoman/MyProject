package com.mapuni.android.taskmanager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hp.hpl.sparta.Text;
import com.mapuni.android.MobileEnforcement.R;
import com.mapuni.android.attachment.T_Attachment;
import com.mapuni.android.attachment.TaskFile;
import com.mapuni.android.attachment.UploadFile;
import com.mapuni.android.base.Global;
import com.mapuni.android.base.controls.loading.YutuLoading;
import com.mapuni.android.base.util.AbStrUtil;
import com.mapuni.android.base.util.DisplayUitl;
import com.mapuni.android.bean.TreeBean;
import com.mapuni.android.business.RWXX;
import com.mapuni.android.business.SpinnerItem;
import com.mapuni.android.business.RWXX.ExpandableBaseAdapter;
import com.mapuni.android.dao.PCDepartmentInfoDao;
import com.mapuni.android.dao.RWZTDao;
import com.mapuni.android.dataprovider.DESSecurity;
import com.mapuni.android.dataprovider.FileHelper;
import com.mapuni.android.dataprovider.JsonHelper;
import com.mapuni.android.dataprovider.SqliteUtil;
import com.mapuni.android.enforcement.AttachmentBaseActivity;
import com.mapuni.android.enforcement.SiteEvidenceActivity;
import com.mapuni.android.enterpriseArchives.SlideView;
import com.mapuni.android.infoQuery.JCKHSearchActivity;
import com.mapuni.android.netprovider.Net;
import com.mapuni.android.netprovider.WebServiceProvider;
import com.mapuni.android.taskmanager.TaskManagerModel.ListTreeAdapter;
import com.mapuni.android.taskmanager.TaskRegisterActivity.AttachAdapter;
import com.mapuni.android.taskmanager.TaskRegisterActivity.SelectAuditorListener;
import com.mapuni.android.treeview.Node;
import com.mapuni.android.treeview.TreeListViewAdapter.OnTreeNodeClickListener;
import com.mapuni.android.uitl.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterTaskFragment extends Fragment implements OnClickListener  {
	LayoutInflater _LayoutInflater;
	View taskRegisterView;
	private RWXX rwxx;
	private String rwGuid;
	private String RWBH;
	
	// 任务来源
	/** 现场执法来源 */
	public final static String XCZF_LY = "010";
	/** 一般来源 */
	public final static String YBRW_LY = "011";
	/** 任务名称 */
	private AutoCompleteTextView etTaskName;
	/** 办结期限 */
	private EditText completeTime;
	/** 任务来源 */
	private Spinner tasksourceSpinner;
	/** 任务描述 */
	private EditText etRemark;
	/** 任务紧急程度 */
	private Spinner taskStateSpinner;
	/** 审核领导 */
	private Spinner leaderSpinner;
	/** 添加企业 */
	private Button add_com_btn,pz_btn,xz_btn,apk_btn;
	/** 企业列表 */
	private ListView qylist;

	/** 任务类型 */
	private EditText taskTypeEditText;

	YutuLoading pd;
	/** 审核领导适配器 */
	private ArrayAdapter<SpinnerItem> leaderAdapter;
	/** 紧急程度 */
	private ArrayAdapter<SpinnerItem> stateAdapter;

	/** 任务来源 */
	private ArrayAdapter<SpinnerItem> sourceAdapter;

	/** 审核领导适配器数据 */
	List<SpinnerItem> leaderAdapterData;
	/** 紧急程度数据 */
	List<SpinnerItem> stateAdapterData;
	/** 任务类型数据 */
	// List<SpinnerItem> typeAdapterData;
	/** 任务来源数据 */
	List<SpinnerItem> sourceAdapterData;
	private HashMap<String, Object> rwDetail;
	private String rwlxCode = "";
	ArrayList<HashMap<String, Object>> qynameList = null;
	/** 企业guid字符串，以，分隔 */
	String qyidStr = "";
	String bjqxDate = "";
	public static final String TASK_PATH = Global.SDCARD_RASK_DATA_PATH + "Attach/RWXF/";
	public static String fileName = "";// 附件名称

	public final int SELECT_SDKARD_FILE = 2;
	private ListView task_attach_list;
	private String imageGuid;
	/** 用户所属地区 **/
	private final String UserAreaCode = Global.getGlobalInstance().getAreaCode();

	/** 附件列表适配器 */
	private AttachAdapter attachAdapter;
	private TaskManagerModel taskManagerModel = new TaskManagerModel();
	private ArrayList<HashMap<String, Object>> attachAdapterData = new ArrayList<HashMap<String, Object>>();
	/** 用户权限 */
	private String userDuty;
	/** 用户 ID */
	private String userID;

	private boolean isAdd = true;
	
	private LinearLayout ll_task_shld,ll_task_zxr,ll_task_phr;
	/** 执行人 *//** 配合人 */
	private EditText zhrEditText,phrEditText;
	StringBuffer sbZshr = new StringBuffer();
	StringBuffer sbFshr = new StringBuffer();
	StringBuffer sbZxr = new StringBuffer();
	StringBuffer sbZhr = new StringBuffer();
	private ArrayList<HashMap<String, Object>> login_user_data;
	private LinearLayout ll_task_is_zb;
	private Spinner task_is_zb;
	private List<SpinnerItem> changedAdapterData;
	private ArrayAdapter<SpinnerItem> changedAdapter;
	private LinearLayout ll_task_zbr;
	private EditText edit_task_zbr;
	Object	result1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String sModify = getActivity().getIntent().getStringExtra("modify");
		taskRegisterView =inflater.inflate(R.layout.taskedit1, null);
		ll_task_shld = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_shld);//审核人员
		ll_task_zxr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_zxr);//执行人
		ll_task_phr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_phr);//协办人
		
		zhrEditText = (EditText)taskRegisterView.findViewById(R.id.edit_task_zxr);
		phrEditText = (EditText)taskRegisterView.findViewById(R.id.edit_task_phr);
		task_attach_list = (ListView) taskRegisterView.findViewById(R.id.taskedit_list);
		completeTime = (EditText) taskRegisterView.findViewById(R.id.completeTime);// 办结期限
		etRemark = (EditText) taskRegisterView.findViewById(R.id.etRemark);// 备注
		tasksourceSpinner = (Spinner) taskRegisterView.findViewById(R.id.Tasksource);// 任务来源
		tasksourceSpinner.setVisibility(View.GONE);
		taskStateSpinner = (Spinner) taskRegisterView.findViewById(R.id.TaskState);// 紧急程度
		leaderSpinner = (Spinner) taskRegisterView.findViewById(R.id.leader);// 审核领导
		taskTypeEditText = (EditText) taskRegisterView.findViewById(R.id.TaskType);// EditText任务类型
		add_com_btn = (Button) taskRegisterView.findViewById(R.id.addqyimg);// 添加企业
		ll_task_is_zb = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_is_zb);// 是否转办
		task_is_zb = (Spinner) taskRegisterView.findViewById(R.id.task_is_zb);// 是否转办
		ll_task_zbr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_zbr);// 转办人
		edit_task_zbr = (EditText) taskRegisterView.findViewById(R.id.edit_task_zbr);// 转办人
		qylist = (ListView) taskRegisterView.findViewById(R.id.qylist);
		pz_btn = (Button) taskRegisterView.findViewById(R.id.pz_btn);//相机
		xz_btn = (Button) taskRegisterView.findViewById(R.id.xz_btn);//文件
		apk_btn = (Button) taskRegisterView.findViewById(R.id.apk_btn);//多文件
		
		
		//		if (sModify != null && sModify.equals("1")) {
//			SetBaseStyle((RelativeLayout) findViewById(R.id.parentLayout), "任务修改");
//			isAdd = false;
//		} else {
//			SetBaseStyle((RelativeLayout) findViewById(R.id.parentLayout), "任务创建");
//			isAdd = true;
//		}

		// 获得用户的权限
		userID = Global.getGlobalInstance().getUserid();
		HashMap<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("UserID", userID);

		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("ZW", conditions, "PC_Users");
		if (data != null && data.size() > 0) {
			userDuty = data.get(0).get("zw").toString();
		}
		/** 根据用户登录用户的id查询 */
		login_user_data = SqliteUtil
				.getInstance()
				.queryBySqlReturnArrayListHashMap(
						"select * from PC_DepartmentInfo where depid =(select depparentid "
								+ "from "
								+ "PC_Users LEFT JOIN PC_DepartmentInfo "
								+ "on PC_Users.depId = PC_DepartmentInfo.depId where userid = '"
								+ Global.getGlobalInstance().getUserid() + "')");
		initData();
		initView();
		initListener();
		return taskRegisterView;
	}
   private void initListener(){
	   pz_btn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		
				takePhoto();
		}
	}) ;
	   xz_btn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			try {
				startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), SELECT_SDKARD_FILE);
			} catch (android.content.ActivityNotFoundException ex) {
				// Potentially direct the user to the Market with a Dialog
				Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
			}
		}
	});
	   apk_btn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			InstallAPK();
		}
	});
   }
	private void initData() {
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			rwxx = (RWXX) bundle.getSerializable("BusinessObj");
			rwGuid = rwxx.getCurrentID();

		} else {
			rwxx = new RWXX();
		}
		pd = new YutuLoading(getActivity());
		pd.setLoadMsg("正在加载数据，请稍等...", "");
		pd.showDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (rwGuid != null) {// 本地有缓存数据
					rwDetail = rwxx.getDetailed(rwGuid);
					attachAdapterData = getAttachAdapterData(T_Attachment.RWXF + "", rwDetail.get("rwbh").toString());
				}
				initSpinnerAdapterData();
				handler.sendEmptyMessage(4);
			}

		}).start();
		if (pd != null) {
			pd.dismissDialog();
		}
	}

	/**
	 * 初始化spinner的适配器
	 */
	private void initSpinnerAdapterData() {
		
		if (userDuty.equals("0")) {
			leaderAdapterData = rwxx.getleaderSpinnerItem("zw = '1' or zw='2'");
		} else if (userDuty.equals("1")) {
			leaderAdapterData = rwxx.getleaderSpinnerItem("zw = '3'");
		} else if (userDuty.equals("2")) {
			leaderAdapterData = rwxx.getleaderSpinnerItem("zw = '3'");
		} else if (userDuty.equals("3")) {
			handler.sendEmptyMessage(6);
			leaderAdapterData = rwxx.getleaderSpinnerItem("(zw = '3' or zw='4') and depid='" + Global.getGlobalInstance().getDepId() + "'");
		} else {
			leaderAdapterData = rwxx.getleaderSpinnerItem("zw = '1' or zw='2' or zw='3'");
		}
		leaderAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, leaderAdapterData);
		leaderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sourceAdapterData = rwxx.gettaskSourceSpinnerItem();
		sourceAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, sourceAdapterData);
		sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 紧急程度 */
		stateAdapterData = rwxx.gettaskStateSpinnerItem();
		stateAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, stateAdapterData);
		stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 任务是否转办 */
		changedAdapterData = rwxx.getTaskChangedSpinnerItem();
		changedAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, changedAdapterData);
		changedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
	
		
		
		//任务转办判断：是否显示承办人
		/** 获得登录用户父部门的id */
//		String depID = login_user_data.get(0).get("depid").toString();
//		if (depID != null && TextUtils.equals(depID, "440606000000depart") && userDuty.equals("3")) { // 是总队的人员
//			handler.sendEmptyMessage(7);
//		}
	}

	// 根据数据设置listView的高度
	public void setListViewHeightBasedOnChildren(ListView listView) {
		// ListAdapter listAdapter = listView.getAdapter();
		if (attachAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < attachAdapter.getCount(); i++) {
			View listItem = attachAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (attachAdapter.getCount() - 1));
		// ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}

	private void initView() {

		String[] aryComName = null;
		Cursor data_cursor = null;
		int i = 0;
		try {

			String sql = "select rwmc from t_ydzf_rwxx  order by UpdateTime desc limit 0, 50";
			data_cursor = SqliteUtil.getInstance().queryBySql(sql);
			aryComName = new String[data_cursor.getCount()];
			while (data_cursor.moveToNext()) {
				aryComName[i] = data_cursor.getString(0);
				i++;
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, aryComName);
			etTaskName = (AutoCompleteTextView) taskRegisterView.findViewById(R.id.etTaskName);
			etTaskName.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (data_cursor != null) {
				data_cursor.close();
			}
		}

		
		
//		if (PCDepartmentInfoDao.isTaskChanged(login_user_data, userDuty)) {
//			ll_task_is_zb.setVisibility(View.VISIBLE);
//		} else{
//			ll_task_is_zb.setVisibility(View.GONE);
//		}

		add_com_btn.setVisibility(View.GONE);
		
		ll_task_zbr.setVisibility(View.GONE);

		qylist.setVisibility(View.GONE);
		attachAdapter = new AttachAdapter(attachAdapterData);
		task_attach_list.setCacheColorHint(Color.TRANSPARENT);
		task_attach_list.setAdapter(attachAdapter);
		task_attach_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String guid = ((TextView) (arg1.findViewById(R.id.listitem_text))).getTag().toString();
				FileHelper fileHelper = new FileHelper();
				fileHelper.showFileByGuid(guid, getActivity());
			}
		});
//		task_is_zb.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				if (position == 0) {
//					ll_task_zxr.setVisibility(View.VISIBLE);
//					ll_task_phr.setVisibility(View.VISIBLE);
//					ll_task_zbr.setVisibility(View.GONE);
//				}else{
//					ll_task_zxr.setVisibility(View.GONE);
//					ll_task_phr.setVisibility(View.GONE);
//					ll_task_zbr.setVisibility(View.VISIBLE);
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				
//			}
//		});
		task_attach_list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String guid = ((TextView) (view.findViewById(R.id.listitem_text))).getTag().toString();
				String fileNam = ((TextView) (view.findViewById(R.id.listitem_text))).getText().toString();

				showDialog(guid, fileNam);
				return false;
			}
		});
//
		loadBottomMenu();
		bindListener();

		//((LinearLayout) getActivity().findViewById(R.id.middleLayout)).addView(taskRegisterView);

	}

	private void bindListener() {
		completeTime.setOnClickListener(new TaskEditTextListener());
		taskTypeEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createDialogViewForTaskType();
			}
		});
//		queryImg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (insertOneTask(false)) {
//					Toast.makeText(getActivity(), "暂存任务成功", Toast.LENGTH_SHORT).show();
//					getActivity().finish();
//				}
//			}
//		});

	}

	/** 办结期限监听事件,判断所选日期大于当前日期 **/
	private class TaskEditTextListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Calendar c = Calendar.getInstance();
			final int year1 = c.get(Calendar.YEAR);
			final int month1 = c.get(Calendar.MONTH);
			final int day1 = c.get(Calendar.DAY_OF_MONTH);

			new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

					int flag;
					if (year > year1) { // 设置年大于当前年，直接设置，不用判断下面的
						completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
						flag = 1;
					} else if (year == year1) {
						// 设置年等于当前年，则向下开始判断月
						if (monthOfYear > month1) {
							// 设置月大于当前月，直接设置，不用判断下面的
							flag = 1;
							completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + day1);
						} else if (monthOfYear == month1) {
							// 设置月等于当前月，则向下开始判断日
							if (dayOfMonth > day1) {
								// 设置日大于当前日，直接设置，不用判断下面的
								flag = 1;
								completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
							} else if (dayOfMonth == day1) {
								// 设置日等于当前日，则向下开始判断时
								flag = 2;
								completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
							} else { // 设置日小于当前日，提示重新设置
								flag = 3;
								completeTime.setText("");
								Toast.makeText(getActivity(), "所选择日期不能小于当前日期,请重新设置", 2000).show();
							}
						} else { // 设置月小于当前月，提示重新设置
							flag = 3;
							completeTime.setText("");
							Toast.makeText(getActivity(), "所选择日期不能小于当前日期,请重新设置", 2000).show();
						}
					} else { // 设置年小于当前年，提示重新设置
						flag = 3;
						completeTime.setText("");
						Toast.makeText(getActivity(), "所选择日期不能小于当前日期,请重新设置", 2000).show();
					}
					if (flag != 3) {
						Calendar nowDate = Calendar.getInstance(), newDate = Calendar.getInstance();
						nowDate.setTime(new Date());// 设置为当前系统时间
						newDate.set(year, monthOfYear, dayOfMonth);// 设置为1990年（6）月29日
						long timeNow = nowDate.getTimeInMillis();
						long timeNew = newDate.getTimeInMillis();
//						long dd = (timeNew - timeNow) / (1000 * 60 * 60 * 24)+1;// 化为天
						long dd = (timeNew - timeNow) / (1000 * 60 * 60 * 24);// 保持和服务端一致
						bjqxDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "  (离办结还有" + dd + "天)");
					} else {
						completeTime.setText("");
					}
				}

			}, year1, month1, day1).show();

		}
	}

	/**
	 * 加载底部菜单
	 */
	public void loadBottomMenu() {
		// 获取手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 算出按钮的高宽
		int width = (int) (dm.widthPixels / (double) 2);
		LinearLayout bottom = (LinearLayout) taskRegisterView.findViewById(R.id.bottomLayout);
		bottom.setVisibility(View.VISIBLE);
		final Button tasksend;
		Button back;
		tasksend = new Button(getActivity());
		back = new Button(getActivity());
		tasksend.setBackgroundResource(R.drawable.btn_click);
		tasksend.setText("提   交");
		tasksend.setTextColor(android.graphics.Color.WHITE);
		tasksend.getPaint().setFakeBoldText(true);// 加粗
		tasksend.setWidth(1);
		back.setBackgroundResource(R.drawable.btn_click);
		back.setText("返   回");
		back.setTextColor(android.graphics.Color.WHITE);
		back.getPaint().setFakeBoldText(true);// 加粗
		back.setWidth(1);

		tasksend.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.FILL_PARENT, 0));
		back.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.FILL_PARENT, 0));
		
		LinearLayout.LayoutParams params_fgx = new LinearLayout.LayoutParams(2, SlideView.dip2px(getActivity(), 30));
		params_fgx.gravity = Gravity.CENTER_VERTICAL;
		ImageView splite = new ImageView(getActivity());
		splite.setScaleType(ScaleType.FIT_XY);
		splite.setLayoutParams(params_fgx);
		splite.setBackgroundResource(R.color.fgx);
		
		ImageView splite1 = new ImageView(getActivity());
		splite1.setScaleType(ScaleType.FIT_XY);
		splite1.setLayoutParams(new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.FILL_PARENT));
		splite1.setBackgroundResource(R.drawable.bg_bottombutton_splite);
		bottom.addView(tasksend);
		bottom.addView(splite);
		bottom.addView(back);
		/**
		 * 任务下发
		 */
		tasksend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upFileAndSendTask();//任务创建
//				if (task_is_zb.getSelectedItemPosition() == 0) {
//				} else {
//					//任务转办
//					
//				}
			}
		});

		/**
		 * 返回
		 */
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Builder builder = new Builder(getActivity());
				builder.setTitle("确认退出该页面吗？");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();

			}
		});
	}
	/**
	 * 创建任务
	 */
	private void upFileAndSendTask(){
		String rwmc = etTaskName.getText().toString();
		String bjqx = bjqxDate;
		String shldId = ((SpinnerItem) (leaderSpinner.getSelectedItem())).getCode();
		if (rwmc.equals("") || bjqx.equals("") || shldId.equals("")) {
			handler.sendEmptyMessage(3);
			return;
		}
		if (TextUtils.isEmpty(RWBH)) {// 没有选择附件
			handler.sendEmptyMessage(UploadFile.callBackCode);
			return;
		}
//		handler.sendEmptyMessage(UploadFile.callBackCode);
		ArrayList<TaskFile> taskFile = getAllUploadFile(T_Attachment.RWXF, RWBH);
		if (taskFile != null && taskFile.size() > 0) {
			UploadFile uploadFile = new UploadFile();
			uploadFile.upLoadFilesMethod(taskFile, handler, getActivity());
		} else {
			handler.sendEmptyMessage(UploadFile.callBackCode);
		}
	
	}
	private final Handler handler = new Handler() {// UI线程Handler
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (pd != null)
					pd.dismissDialog();
				Toast.makeText(getActivity(), "任务创建成功！", Toast.LENGTH_SHORT).show();
				etRemark.setText("");
				bjqxDate = "";
				completeTime.setText("");
				RWBH = "";	
				taskTypeEditText.setText("");
				rwlxCode = "";
				taskStateSpinner.setSelection(0);
				leaderSpinner.setSelection(0);
				etTaskName.setText("");
				attachAdapterData.clear();
				attachAdapter.updateData(attachAdapterData);
				break;

			case 1:
				if (pd != null)
					pd.dismissDialog();

				if (msg.obj != null) {
					String reason = msg.obj.toString();
					Toast.makeText(getActivity(), reason, Toast.LENGTH_SHORT).show();
				} else {

					ContentValues cv = new ContentValues();
					cv.put("rwzt", RWXX.RWZT_WAIT_DISPATCH);
					String[] whereArgs = { rwGuid };
					try {
						SqliteUtil.getInstance().update("T_YDZF_RWXX", cv, "guid=?", whereArgs);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Toast.makeText(getActivity(), "任务创建失败,已保存当前任务等待提交！", Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				if (pd != null) {
					pd.dismissDialog();
				}
				Toast.makeText(getActivity(), "网络不通，请检查网络设置！", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if (pd != null)
					pd.dismissDialog();
				Toast.makeText(getActivity(), "任务信息不全，请完善带*号的选项！", Toast.LENGTH_SHORT).show();
				break;
			case 4:// 任务数据加载完成，刷新界面
				if (pd != null)
					pd.dismissDialog();
				if (rwDetail != null) {
					bindValue(rwDetail);
				} else {
					tasksourceSpinner.setAdapter(sourceAdapter);
					taskStateSpinner.setAdapter(stateAdapter);
					task_is_zb.setAdapter(changedAdapter);
					leaderSpinner.setAdapter(leaderAdapter);

				}
				break;
			case 5:
				if (pd != null)
					pd.dismissDialog();
				Toast.makeText(getActivity(), "任务创建失败", Toast.LENGTH_SHORT).show();
				break;
			case 6:
				ll_task_shld.setVisibility(View.GONE);
				ll_task_zxr.setVisibility(View.VISIBLE);
				ll_task_phr.setVisibility(View.VISIBLE);
				SelectAuditorListener listener = new SelectAuditorListener();
				edit_task_zbr.setOnClickListener(listener);
				zhrEditText.setOnClickListener(listener);
				phrEditText.setOnClickListener(listener);
				break;
			case 7:
				ll_task_shld.setVisibility(View.GONE);
				ll_task_zxr.setVisibility(View.GONE);
				ll_task_phr.setVisibility(View.GONE);
				break;
			case UploadFile.callBackCode:
				pd = new YutuLoading(getActivity());
				pd.setLoadMsg("正在提交任务，请稍等...", "");
				pd.setCancelable(true);
				pd.showDialog();
				new Thread(new Runnable() {
					@Override
					public void run() {
					
						if (!Net.checkURL(Global.getGlobalInstance().getSystemurl())) {
							handler.sendEmptyMessage(2);
							return;
						}
			//			if (insertOneTask(true)) {
		   
							if (requestRegisterTask(rwGuid).toString().contains("成功")) {
								/*
								 * ArrayList<TaskFile>
								 * taskFile=getAllUploadFile(T_Attachment.RWXF,
								 * RWBH); if(taskFile!=null &&
								 * taskFile.size()>0){
								 * 
								 * }
								 */
								handler.sendEmptyMessage(0);
							} else {
								handler.sendEmptyMessage(1);
							}
//						} else {
//							// handler.sendEmptyMessage(1);
//						}
					}
				}).start();
				break;
			}
		}

	};

	private void bindValue(HashMap<String, Object> rwDetail) {
		RWBH = rwDetail.get("rwbh").toString();
		etTaskName.setText(rwDetail.get("rwmc").toString());
		bjqxDate = rwDetail.get("bjqx").toString();
		completeTime.setText(rwDetail.get("bjqx").toString());
		etRemark.setText(rwDetail.get("bz").toString());
		HashMap<String, Object> conditions = new HashMap<String, Object>();
		rwlxCode = rwDetail.get("rwlx").toString();
		if (rwlxCode != null && !rwlxCode.equals("")) {
			conditions.put("code", rwlxCode);
			String lxName = SqliteUtil.getInstance().getList("name", conditions, "T_YDZF_RWLX").get(0).get("name").toString();
			taskTypeEditText.setText(lxName);
		}
		// 任务来源
		tasksourceSpinner.setAdapter(sourceAdapter);
		String rwly = rwDetail.get("rwly").toString();
		for (int i = 0; i < sourceAdapterData.size(); i++) {
			if (sourceAdapterData.get(i).getCode().equals(rwly)) {
				tasksourceSpinner.setSelection(i);
				break;
			}
		}
		// 紧急程度
		taskStateSpinner.setAdapter(stateAdapter);
		String jjcd = rwDetail.get("jjcd").toString();
		for (int i = 0; i < stateAdapterData.size(); i++) {
			if (stateAdapterData.get(i).getCode().equals(jjcd)) {
				taskStateSpinner.setSelection(i);
				break;
			}
		}

		leaderSpinner.setAdapter(leaderAdapter);
		String shkz = rwDetail.get("shkz").toString();
		for (int i = 0; i < leaderAdapterData.size(); i++) {
			if (leaderAdapterData.get(i).getCode().equals(shkz)) {
				leaderSpinner.setSelection(i);
				break;
			}
		}
		attachAdapterData = getAttachAdapterData(T_Attachment.RWXF + "", RWBH);
		attachAdapter = new AttachAdapter(attachAdapterData);
		task_attach_list.setAdapter(attachAdapter);
		attachAdapter.updateData(attachAdapterData);
		
		// 恢复主执行人和配合人
		sbZxr = new StringBuffer();
		sbZhr = new StringBuffer();
		sbZxr.append(DisplayUitl.getAppInfoDataToPreference(
				getActivity(), rwGuid + "zshrid", ""));
		sbZhr.append(DisplayUitl.getAppInfoDataToPreference(
				getActivity(), rwGuid + "fshrid", ""));
		zhrEditText.setText(DisplayUitl.getAppInfoDataToPreference(
				getActivity(), rwGuid + "zshrname", ""));
		phrEditText.setText(DisplayUitl.getAppInfoDataToPreference(
				getActivity(), rwGuid + "fshrname", ""));
		phrEditText.setText(DisplayUitl.getAppInfoDataToPreference(
				getActivity(), rwGuid + "zbhrname", ""));
	}

	/**
	 * 调用webservice创建一条任务
	 * 
	 * @param rwGuids
	 *            任务的guid
	 */
	public Object  requestRegisterTask(String rwGuid) {
		String result = "";
		String methodName = "SaveLeTask";
//		HashMap<String, Object> conditions = new HashMap<String, Object>();
//		conditions.put("guid", rwGuid);
//		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("T_YDZF_RWXX", conditions);
//		String taskInfoJson = JsonHelper.listToJSON(data);
		ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> param = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>>	data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		String rwmc = etTaskName.getText().toString();
		String shldId = ((SpinnerItem) (leaderSpinner.getSelectedItem())).getCode();
		String updateTime = DisplayUitl.getServerTime();
		String bjqx = bjqxDate;
		String jjcd = ((SpinnerItem) (taskStateSpinner.getSelectedItem())).getCode();
		String rwms = etRemark.getText().toString();
		hashMap.put("RWMC", rwmc);
		hashMap.put("FBR",Global.getGlobalInstance().getUserid().toString());
		hashMap.put("AuditUserId", shldId);
		hashMap.put("FBSJ", updateTime);
		hashMap.put("BJQX", bjqx);
		hashMap.put("JJCD", jjcd);
		hashMap.put("BZ", rwms);
		hashMap.put("ZW", "2");
		hashMap.put("RWLY", YBRW_LY);
		hashMap.put("RWLX", rwlxCode);
		if (TextUtils.isEmpty(RWBH)) {// 没有选择附件
			RWBH =  rwxx.returnRWBH();
		}
		hashMap.put("RWBH",RWBH);
//		Global.getGlobalInstance().getUserZW().toString()
		data.add(hashMap);
				ArrayList<TaskFile> listAllFile = getAllUploadFile(T_Attachment.RWXF, RWBH);
				ArrayList<HashMap<String, Object>> params0 = new ArrayList<HashMap<String, Object>>();
				if(params0.size()>0)
					params0 .clear();	
		for (int n = 0; n < listAllFile.size(); n++) {
			TaskFile taskFile = listAllFile.get(n);
			HashMap<String, Object> param0 = new HashMap<String, Object>();
			String path = T_Attachment.transitToChinese(Integer.valueOf(taskFile.getUnitId())) + "/" + taskFile.getFilePath();
			param0.put("FilePath",path);
			param0.put("FileName", taskFile.getFileName());
			param0.put("Extension",  taskFile.getExtension());
			param0.put("FK_Unit", taskFile.getUnitId());
			param0.put("Remark", "");
			param0.put("LinkUrl", "");
			param0.put("FileType", "02");	
			param0.put("FK_Id",RWBH);
			params0.add(param0);
			//getAllUploadFile(T_Attachment.RWXF, RWBH);
		}
	   String json=  JsonHelper.listToJSON(data);
	   String json1="";
	   if(params0!=null&&params0.size()!=0){
		   json1=  JsonHelper.listToJSON(params0); 
	   }else{
		   json1="[]";  
	   }
		param.put("TaskJson", json);
//		param.put("AttachmentJosn", json1);
		params.add(param);
		try {
			result1 =  WebServiceProvider.callWebService(Global.NAMESPACE, methodName, params, Global.getGlobalInstance().getSystemurl() + Global.WEBSERVICE_URL,
					WebServiceProvider.RETURN_STRING, true);
		
		} catch (IOException e) { 

			e.printStackTrace();
		}

		return result1;
	}

	/**
	 * 调用webservice创建一条任务流程
	 * 
	 * @param rwGuid
	 *            任务的guid
	 */
//	public Boolean registerTaskFlow(String rwbh) {
//		Boolean result = false;
//		String methodName = "SaveWorkFlowInfo";
//		HashMap<String, Object> conditions = new HashMap<String, Object>();
//		conditions.put("Tid", rwbh);
//		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("YDZF_RWLC", conditions);
//		String workFlowJson = JsonHelper.listToJSON(data);
//
//		ArrayList<HashMap<String, Object>> params = new ArrayList<HashMap<String, Object>>();
//		HashMap<String, Object> param = new HashMap<String, Object>();
//		param.put("workFlowJson", workFlowJson);
//		String token = "";
//		try {
//			token = DESSecurity.encrypt(methodName);
//		} catch (Exception e1) {
//
//			e1.printStackTrace();
//		}
//		param.put("token", token);
//		params.add(param);
//		try {
//			result = (Boolean) WebServiceProvider.callWebService(Global.NAMESPACE, methodName, params, Global.getGlobalInstance().getSystemurl() + Global.WEBSERVICE_URL,
//					WebServiceProvider.RETURN_BOOLEAN, true);
//			if (result == null) {
//				result = false;
//			}
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//
//		return result;
//	}

	/**
	 * 
	 * @param tag
	 *            true 为直接下发 false 保存本地待提交
	 * @return
	 */
	public Boolean insertOneTask(Boolean tag) {
		Boolean result = false;
		ContentValues cv = new ContentValues();

		String rwmc = etTaskName.getText().toString();
		String bjqx = bjqxDate;
		String shldId = ((SpinnerItem) (leaderSpinner.getSelectedItem())).getCode();
		String zxrString = zhrEditText.getText().toString();
		String zbhrString = edit_task_zbr.getText().toString();
		if (task_is_zb != null && task_is_zb.getSelectedItemPosition() == 0) {
			if (rwmc.equals("") || bjqx.equals("") || shldId.equals("") || (userDuty.equals("3") && zxrString.equals(""))
					) {
				handler.sendEmptyMessage(3);
				return false;
			}
		} else {
			if (rwmc.equals("") || bjqx.equals("") || shldId.equals("") || (userDuty.equals("4") && zbhrString.equals(""))) {
				handler.sendEmptyMessage(3);
				return false;
			}
		}
		if (RWBH == null || RWBH.equals("")) {
			RWBH = rwxx.returnRWBH();
		}
		// String
		// rwly=((SpinnerItem)(tasksourceSpinner.getSelectedItem())).getCode();
		String jjcd = ((SpinnerItem) (taskStateSpinner.getSelectedItem())).getCode();
		String rwms = etRemark.getText().toString();
//		cv.put("rwly", RWXX.YBRW_LY);
		cv.put("BZ", rwms);
		cv.put("BJQX", bjqx);
		cv.put("RWMC", rwmc);
//		cv.put("rwbh", RWBH);
		cv.put("JJCD", jjcd);
		cv.put("RWLX", rwlxCode);
		cv.put("AuditUserId", shldId);
		cv.put("FBR", Global.getGlobalInstance().getUserid());
		cv.put("ZW", "2");
		// 任务所属地区和登录用户所属地区相同
		cv.put("syncdataregioncode", UserAreaCode);
		if (tag) {// 直接下发需要和服务器时间同步
			String updateTime = DisplayUitl.getServerTime();
			if (updateTime.equals("")) {
				return false;
			}
			cv.put("UpdateTime", updateTime);
			cv.put("FBSJ", updateTime);
			cv.put("rwzt", RWXX.RWZT_WAIT_AUDIT);
			if (rwGuid != null && !rwGuid.equals("")) {// 更新操作
				String[] whereArgs = { rwGuid };
				try {
					SqliteUtil.getInstance().update("T_YDZF_RWXX", cv, "guid=?", whereArgs);
					// delFromTaskEnpriLink();
					// addinfoToTaskEnpriLink();
					result = true;
					//中队长创建任务时    需要保存执行人和协办人
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrid", sbZxr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrid", sbZhr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrname", zhrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrname", phrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zbhrname", edit_task_zbr.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				rwGuid = UUID.randomUUID().toString();
				// addinfoToTaskEnpriLink();
				cv.put("guid", rwGuid);
				if (SqliteUtil.getInstance().insert(cv, "T_YDZF_RWXX") > 0) {
					result = true;
					//中队长创建任务时    需要保存执行人和协办人
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrid", sbZxr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrid", sbZhr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrname", zhrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrname", phrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zbhrname", edit_task_zbr.getText().toString());
				}

			}
		} else {
			if (rwGuid != null && !rwGuid.equals("")) {// 更新操作
				String[] whereArgs = { rwGuid };
				try {
					SqliteUtil.getInstance().update("T_YDZF_RWXX", cv, "guid=?", whereArgs);
					result = true;
					//中队长创建任务时    需要保存执行人和协办人
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrid", sbZxr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrid", sbZhr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrname", zhrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrname", phrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zbhrname", edit_task_zbr.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				rwGuid = UUID.randomUUID().toString();
				// addinfoToTaskEnpriLink();
				cv.put("guid", rwGuid);
				cv.put("rwzt", RWXX.RWZT_WAIT_DISPATCH);
				if (SqliteUtil.getInstance().insert(cv, "T_YDZF_RWXX") > 0) {
					result = true;
					//中队长创建任务时    需要保存执行人和协办人
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrid", sbZxr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrid", sbZhr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrname", zhrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrname", phrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zbhrname", edit_task_zbr.getText().toString());
				}

			}
		}
		return result;

	}

	// 任务类型
	public void createDialogViewForTaskType() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewex = inflater.inflate(R.layout.expand_list_dialog, null);
		TextView expand_title_tv = (TextView) viewex.findViewById(R.id.expand_title_tv);
		expand_title_tv.setText("任务分类");

		ArrayList<HashMap<String, Object>> groupData = rwxx.getTask_type();
		final List<String> groupList = new ArrayList<String>();
		if (groupData != null && groupData.size() > 0) {
			for (HashMap<String, Object> hashMap : groupData) {
				String tnameStr = hashMap.get("name").toString();
				groupList.add(tnameStr);
			}
		}
		final ArrayList<ArrayList<HashMap<String, Object>>> childMapData = rwxx.getTask_type_child(groupData);

		final ExpandableListView expandableListView = (ExpandableListView) viewex.findViewById(R.id.expandablelistview);
		ExpandableBaseAdapter adapter = new ExpandableBaseAdapter(getActivity(), groupList, childMapData);
		expandableListView.setAdapter(adapter);
		expandableListView.setCacheColorHint(0);// 设置拖动列表的时候防止出现黑色背景
		expandableListView.setGroupIndicator(getResources().getDrawable(R.layout.expandablelistviewselector));

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.yutu);
		builder.setTitle("请选择任务类型");
		builder.setView(viewex);

		final AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < groupList.size(); i++) {
					if (groupPosition != i) {
						expandableListView.collapseGroup(i);
					}
				}
			}
		});
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				String cnames = childMapData.get(groupPosition).get(childPosition).get("name").toString();
				rwlxCode = childMapData.get(groupPosition).get(childPosition).get("code").toString();
				taskTypeEditText.setText(cnames);

				dialog.cancel();
				return false;
			}
		});
	}

	public void upDataAttathListView() {
		if (attachAdapter != null) {
			attachAdapter.updateData(getAttachAdapterData(T_Attachment.RWXF + "", RWBH));
		}

	}

//	// 拍照
//	public void photograph(View view) {
//		Toast.makeText(getActivity(), "ssss", Toast.LENGTH_LONG).show();
//	//	takePhoto();
//	}

	private void takePhoto() {// 拍照

		imageGuid = UUID.randomUUID().toString();
		Intent photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(TASK_PATH);
		if (!file.exists())// 第一次拍照创建照片文件夹
			file.mkdirs();
		photo_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TASK_PATH + imageGuid + "." + "jpg")));
		photo_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		photo_intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
		startActivityForResult(photo_intent, 1);

	}

	// 选照
//	public void takefigure(View view) {
//
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*");
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		try {
//			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), SELECT_SDKARD_FILE);
//		} catch (android.content.ActivityNotFoundException ex) {
//			// Potentially direct the user to the Market with a Dialog
//			Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
//		}
//	}

	// 安装文件管理器
	public void InstallAPK() {

		String p = Global.SDCARD_RASK_DATA_PATH + "data/RootExplorer.apk";
		File file = new File(p);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		if (RWBH == null || RWBH.equals("")) {
			RWBH = rwxx.returnRWBH();
		}
		
	if (requestCode == SiteEvidenceActivity.TAKE_PHOTOS) {

			if (resultCode == -1) {
				Date now = new Date();
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				String fileName = Global.getGlobalInstance().getRealName() + "_" + dateFormat.format(now);
          
				String sql = "insert into T_Attachment (Guid,FileName,FilePath,Extension,FK_Unit,FK_Id) " + "values ('" + imageGuid + "','" + fileName + "','" + imageGuid
						+ ".jpg','.jpg','" + T_Attachment.RWXF + "','" + RWBH + "')";
			    
				SqliteUtil.getInstance().execute(sql);
			}

		}
		if (data != null && requestCode == SELECT_SDKARD_FILE) {
			AttachmentBaseActivity.selectSDcardFile(data, getActivity(), T_Attachment.RWXF, RWBH);
		}
		attachAdapterData = getAttachAdapterData(T_Attachment.RWXF + "", RWBH);
		attachAdapter.updateData(attachAdapterData);

	}

	/**
	 * 获取附件列表数据
	 * 
	 * @param fk_unit
	 * @param fk_id
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getAttachAdapterData(String fk_unit, String fk_id) {

		HashMap<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("FK_Unit", fk_unit);
		conditions.put("FK_Id", fk_id);
		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("Guid,FileName", conditions, "T_Attachment");
		return data;

	}

	public ArrayList<TaskFile> getAllUploadFile(int fk_unit, String fk_id) {
		ArrayList<TaskFile> _ListFile = new ArrayList<TaskFile>();
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("FK_Unit", fk_unit + "");
		condition.put("fk_id", fk_id);
		ArrayList<HashMap<String, Object>> fileLists = SqliteUtil.getInstance().getList(" * ", condition, "T_Attachment");
		if (fileLists != null || fileLists.size() > 0) {

			for (HashMap<String, Object> map : fileLists) {
				TaskFile taskFile = new TaskFile();
				String absolutePath = Global.SDCARD_RASK_DATA_PATH + "Attach/" + T_Attachment.transitToChinese(fk_unit) + "/" + map.get("filepath").toString();
				taskFile.setGuid(map.get("guid").toString());
				taskFile.setFileName(map.get("filename").toString());
				taskFile.setFilePath(map.get("filepath").toString());
				taskFile.setAbsolutePath(absolutePath);
				taskFile.setUnitId(map.get("fk_unit").toString());
				taskFile.setExtension(map.get("extension").toString());
				_ListFile.add(taskFile);
			}
		}
		return _ListFile;
	}

	protected void showDialog(final String guid, final String fileName) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		String[] selections = { "重命名", "删除" };
		dialog.setItems(selections, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {

				case 0:
					LinearLayout ly = new LinearLayout(getActivity());
					ly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
					final EditText edtext = new EditText(getActivity());
					edtext.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					TextView tv = new TextView(getActivity());
					tv.setText("名称：");
					ly.addView(tv);
					ly.addView(edtext);
					AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
					ab.setTitle("重命名");
					ab.setIcon(R.drawable.icon_rename);
					ab.setView(ly);
					ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newName = edtext.getText().toString();

							String id = guid;

							String sql = "update T_Attachment set FileName = '" + newName + "' where guid = '" + id + "'";
							SqliteUtil.getInstance().execute(sql);

							upDataAttathListView();
							Toast.makeText(getActivity(), "重命名成功！", Toast.LENGTH_LONG).show();
						}
					});
					ab.setNegativeButton("取消", null);
					ab.show();
					break;
				case 1:
					AlertDialog.Builder deleteab = new AlertDialog.Builder(getActivity());
					deleteab.setTitle("删除");
					deleteab.setMessage("你确定要删除" + fileName + "吗？");
					deleteab.setIcon(R.drawable.icon_delete);
					deleteab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {

							deleteFile(guid);
							String sql = "delete from T_Attachment " + " where guid = '" + guid + "'";
							SqliteUtil.getInstance().execute(sql);
							upDataAttathListView();
							Toast.makeText(getActivity(), "删除" + fileName + "成功！", Toast.LENGTH_LONG).show();
						}

					});
					deleteab.setNegativeButton("取消", null);
					AlertDialog ad = deleteab.create();
					ad.show();
					break;

				}
			}
		}).show();
	}

	public boolean deleteFile(String guid) {
		HashMap<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("guid", guid);
		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("FilePath,Extension,FK_Unit", conditions, "T_Attachment");
		if (data != null && data.size() == 1) {
			HashMap<String, Object> map = data.get(0);
			String fk_unit = map.get("fk_unit").toString();
			String extension = map.get("extension").toString();
			T_Attachment.transitToChinese(Integer.valueOf(fk_unit));
			String filepath = Global.SDCARD_RASK_DATA_PATH + "Attach/" + T_Attachment.transitToChinese(Integer.valueOf(fk_unit)) + "/" + guid + extension;
			File file = new File(filepath);
			if (file.exists()) {
				file.delete();
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	public class AttachAdapter extends BaseAdapter {
		ArrayList<HashMap<String, Object>> attachAdapterData;

		public AttachAdapter(ArrayList<HashMap<String, Object>> attachAdapterData) {
			this.attachAdapterData = attachAdapterData;

		}

		@Override
		public int getCount() {
			int size = attachAdapterData.size();
			/*
			 * if(size==0){ return 1; }
			 */
			return size;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return attachAdapterData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void addData(ArrayList<HashMap<String, Object>> CompanyAdapterData) {
			this.attachAdapterData.addAll(CompanyAdapterData);
			notifyDataSetChanged();
		}

		public void updateData(ArrayList<HashMap<String, Object>> CompanyAdapterData) {
			this.attachAdapterData = CompanyAdapterData;
			setListViewHeightBasedOnChildren(task_attach_list);
			notifyDataSetChanged();
		}

		public ArrayList<HashMap<String, Object>> getData() {
			return attachAdapterData;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.listitem, null);

			}
			ImageView rw_icon = (ImageView) convertView.findViewById(R.id.listitem_left_image);
			rw_icon.setImageResource(R.drawable.icon_table);
			TextView rwmc_text = (TextView) convertView.findViewById(R.id.listitem_text);
			rwmc_text.setText(attachAdapterData.get(position).get("filename").toString());
			rwmc_text.setTextSize(20);
			rwmc_text.setTag(attachAdapterData.get(position).get("guid").toString());

			return convertView;
		}
	}
	
	/**
	 * 选择执行人和配合人
	 * 
	 * @author wangliugeng
	 * 
	 */
	public class SelectAuditorListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.edit_task_zxr:
				try {
					showDialog("请选择承办人", v, 2);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "获取用户信息失败，请先同步用户数据", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.edit_task_phr:
				try {
					showDialog("请选择承办人", v, 3);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "获取用户信息失败，请先同步用户数据", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.edit_task_zbr:
				try {
					showDialog("请选择转办人", v, 4);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "获取用户信息失败，请先同步用户数据", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}

		}

	}

	public void showDialog(String title, final View textView, final int userTag) {
		
		View dialogView = _LayoutInflater.inflate(
				R.layout.enforcementmodel_select_commonpeople_list, null);
		String headDepId = "";
		AlertDialog.Builder ab = new AlertDialog.Builder(
				getActivity());
		ab.setTitle(title);
		ab.setIcon(getResources().getDrawable(R.drawable.yutu));
		ab.setView(dialogView);
		final AlertDialog ad = ab.create();
		final ListView customListView = (ListView) dialogView
				.findViewById(R.id.enforcementmodel_select_commonpeople_listview);
		
		ArrayList<HashMap<String, Object>> depData = new ArrayList<HashMap<String, Object>>();
		String sql = "select * from  PC_DepartmentInfo where depName not like '%顺德%'";
		depData = SqliteUtil.getInstance().queryBySqlReturnArrayListHashMap(sql);
		
		final List<String> groupList = new ArrayList<String>();
		final ArrayList<ArrayList<HashMap<String, Object>>> childMapData = new ArrayList<ArrayList<HashMap<String, Object>>>();
		HashMap<String, Object> condition = new HashMap<String, Object>();
		String currentDeptId = Global.getGlobalInstance().getDepId();
		String userid = Global.getGlobalInstance().getUserid();
		ArrayList<HashMap<String, Object>> temp = null;
		
		switch (userTag) {
		case 0:// 主审核人
			if (userDuty.equals("1")) { //局长
				condition.put("zw", "'2'");
				//只保留领导部门
				temp = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < depData.size(); i++) {
					if (currentDeptId.equals(depData.get(i).get("depid").toString())) {
						temp.add(depData.get(i));
						break;
					}
				}
				depData.clear();
				depData = temp;
			} else if (userDuty.equals("2")) { //副局长
				condition.put("zw", "'3'");
				//去掉领导部门
				temp = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < depData.size(); i++) {
					if (!currentDeptId.equals(depData.get(i).get("depid").toString())) {
						temp.add(depData.get(i));
					}
				}
				depData.clear();
				depData = temp;
			}
			break;
		case 1:// 副审核人
			if (userDuty.equals("1")) {
				condition.put("zw", "'2'");
				//只保留领导部门
				temp = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < depData.size(); i++) {
					if (currentDeptId.equals(depData.get(i).get("depid").toString())) {
						temp.add(depData.get(i));
						break;
					}
				}
				depData.clear();
				depData = temp;
			} else if (userDuty.equals("2")) {
				condition.put("zw", "'3'");
				//去掉领导部门
				temp = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < depData.size(); i++) {
					if (!currentDeptId.equals(depData.get(i).get("depid").toString())) {
						temp.add(depData.get(i));
					}
				}
				depData.clear();
				depData = temp;
			}
			break;
		case 2:// 执行人
			condition.put("1", "2' or zw='0' or zw='3' or zw='4");
			currentDeptId = Global.getGlobalInstance().getDepId();
			ArrayList<HashMap<String, Object>> self = new ArrayList<HashMap<String,Object>>();
			for (int i = 0; i < depData.size(); i++) {
				try {
					if(String.valueOf(depData.get(i).get("depid")).contains(currentDeptId)){
						self.add(depData.get(i));
					}
				} catch (Exception e) {
				}
			}
			for (int i = 0; i < depData.size(); i++) {
				if (self.get(0).get("depparentid").toString().equals(depData.get(i).get("depid").toString())) {
					self.add(depData.get(i));
				}
			}
			depData.clear();
			depData.addAll(self);
			break;
		case 3:// 协办人
			condition.put("1", "2' or zw='0' or zw='3' or zw='4");
			break;
		case 4:// 转办人
			condition.put("zw", " '1' or zw='3' ");
			break;
		default:
			break;
		}
		if (userTag == 4) {
			PCDepartmentInfoDao pcDepartmentInfoDao = new PCDepartmentInfoDao();
			depData = pcDepartmentInfoDao.getElseDep(depData);
			depData.remove(login_user_data.get(0));//移出自身所在的单位
		}else {
			PCDepartmentInfoDao pcDepartmentInfoDao = new PCDepartmentInfoDao();
			depData = pcDepartmentInfoDao.getSelfDep(depData);
		}
		List<TreeBean> treeBeans = new ArrayList<TreeBean>();
		List<TreeBean> twoBeans = new ArrayList<TreeBean>();
		List<TreeBean> threeBeans = new ArrayList<TreeBean>();
		try {
			for (HashMap<String, Object> map : depData) {
				if (AbStrUtil.isNumber(map.get("depparentid").toString())) {
					map.put("userTag", userTag);
					TreeBean bean = new TreeBean(treeBeans.size() + 1, 0,0, map.get("depname").toString(), map);
					treeBeans.add(bean);
				}
			}
			for (TreeBean treeBean : treeBeans) {
				for (HashMap<String, Object> map : depData) {
					if (treeBean.getpId() == 0 && map.get("depparentid").toString().equalsIgnoreCase(treeBean.getData().get("depid").toString())) {
						map.put("userTag", userTag);
						TreeBean bean = new TreeBean(treeBeans.size() + twoBeans.size() + 1, treeBean.getId(),
								1,map.get("depname").toString(), map);
						twoBeans.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (HashMap<String, Object> map : depData) {
			String depName = map.get("depname").toString();
			groupList.add(depName);
			String string = "";
			if(condition.get("zw") != null){
				string = " and (zw=" + condition.get("zw")+")";
			}
			String sqlChildPeople="select t.UserID,t.U_RealName,t.depid from PC_Users t  where t.depid='"+ map.get("depid").toString() +"'" + string + " order by t.zw";
			ArrayList<HashMap<String, Object>> usersData_s = SqliteUtil.getInstance().queryBySqlReturnArrayListHashMap(sqlChildPeople);
			ArrayList<HashMap<String, Object>> usersData = new ArrayList<HashMap<String,Object>>();
			RWZTDao rwztDao = new RWZTDao();
			for (HashMap<String, Object> user : usersData_s) {
				String userId = (String) user.get("userid");
				String userName = (String) user.get("u_realname");
				String depid = (String) user.get("depid");
				HashMap<String, Object> wateInfo = (HashMap<String, Object>) rwztDao.withUserId(userId).getWateInfo();
				wateInfo.put("userid", userId);
				wateInfo.put("u_realname", userName);
				wateInfo.put("depid", depid);
				usersData.add(wateInfo);
			}
			if (usersData_s.isEmpty()) {
				groupList.remove(depName);
			}else{
				childMapData.add(usersData);
			}
		}
		try {
			for (TreeBean treeBean : twoBeans) {
				for (ArrayList<HashMap<String, Object>> childListData : childMapData) {
					for (HashMap<String, Object> childData : childListData) {
						childData.put("userTag", userTag);
						if (childData.get("depid").toString().equalsIgnoreCase(treeBean.getData().get("depid").toString())) {
							TreeBean bean = new TreeBean(treeBeans.size() + twoBeans.size() + threeBeans.size() +1, 
									treeBean.getId(),2, childData.get("u_realname").toString(), childData);
							threeBeans.add(bean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		treeBeans.addAll(twoBeans);
		treeBeans.addAll(threeBeans);
		/** 用户id集合 */
		final LinkedList<String> linkedList = new LinkedList<String>();
		/** 用户姓名集合 */
		final LinkedList<String> linkedName = new LinkedList<String>();
		ListTreeAdapter selectAuditorAdapter = taskManagerModel
				.getselectAuditorAdapter(customListView,treeBeans, linkedList,
						linkedName, getActivity());
		
		if (userTag == 0) {// 选择主执行人
			customListView.setAdapter(selectAuditorAdapter);
			selectAuditorAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

				@Override
				public void onClick(View v,Node node, int position) {
					if (node.isLeaf() && node.getLevel() == 2){
						Map<String, Object> data = ((TreeBean)node.getData()).getData();
						String userCheckedId = data.get("userid").toString();
						sbZshr = new StringBuffer();
						sbZshr.append(userCheckedId);
						HashMap<String, Object> conditions = new HashMap<String, Object>();
						conditions.put("userid", sbZshr.toString());
						ArrayList<HashMap<String, Object>> data2 = SqliteUtil
								.getInstance().getList("u_realname",
										conditions, "PC_Users");

						((TextView) textView).setText(data2.get(0)
								.get("u_realname").toString());
						ad.cancel();
					}
				}
			});
			
			ad.show();
		} else if (userTag == 2 || userTag == 4) {// 选择执行人
			customListView.setAdapter(selectAuditorAdapter);
			selectAuditorAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

				@Override
				public void onClick(View v,Node node, int position) {
					if (node.isLeaf() && node.getLevel() == 2){
						Map<String, Object> map = ((TreeBean)node.getData()).getData();
						String userCheckedId = map.get("userid").toString();
						sbZxr = new StringBuffer();
						sbZxr.append(userCheckedId);
						HashMap<String, Object> conditions = new HashMap<String, Object>();
						conditions.put("userid", sbZxr.toString());
						ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("u_realname", conditions, "PC_Users");
						((TextView) textView).setText(data.get(0).get("u_realname").toString());
						ad.cancel();
					}
				}
				
			});
			ad.show();
		} else {
			customListView.setAdapter(selectAuditorAdapter);
			selectAuditorAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {

				@Override
				public void onClick(View v,Node node, int position) {
					if (node.isLeaf() && node.getLevel() == 2){
						CheckBox two_class_cb = (CheckBox) v
								.findViewById(R.id.two_class_cb);
						two_class_cb.toggle();
						node.setChecked(two_class_cb.isChecked());
						Map<String, Object> map = ((TreeBean)node.getData()).getData();
						String userCheckedId = map.get("userid").toString();
						String realName = map.get("u_realname").toString();
						if (two_class_cb.isChecked()) {
							if (!linkedList.contains(userCheckedId)) {
								linkedList.add(userCheckedId);
							}
							if (!linkedName.contains(realName)) {
								linkedName.add(realName);
							}
						} else {
							if (linkedList.contains(userCheckedId)) {
								linkedList.remove(userCheckedId);
							}
							if (linkedName.contains(realName)) {
								linkedName.remove(realName);
							}
						}
					}
				}
				
			});
			ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (DisplayUitl.isFastDoubleClick()) {
						return;
					}
					StringBuffer userName = new StringBuffer();
					if (userTag == 1) {// 选择副审核人
						sbFshr = new StringBuffer();
						for (int i = 0; i < linkedList.size(); i++) {
							sbFshr.append(linkedList.get(i) + ",");
							userName.append(linkedName.get(i) + ",");
						}

						if (sbFshr.length() > 0) {
							sbFshr.deleteCharAt(sbFshr.length() - 1);
						}
						if (userName.length() > 0) {
							userName.deleteCharAt(userName.length() - 1);
						}
						((TextView) textView).setText(userName.toString());

					} else {// 选择知会人
						sbZhr = new StringBuffer();
						for (int i = 0; i < linkedList.size(); i++) {
							sbZhr.append(linkedList.get(i) + ",");
							userName.append(linkedName.get(i) + ",");
						}
						if (sbZhr.length() > 0) {
							sbZhr.deleteCharAt(sbZhr.length() - 1);
						}
						if (userName.length() > 0) {
							userName.deleteCharAt(userName.length() - 1);
						}
						((TextView) textView).setText(userName.toString());
					}
				}

			});
			ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			ab.create().show();
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
