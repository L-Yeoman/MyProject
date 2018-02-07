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
	
	// ������Դ
	/** �ֳ�ִ����Դ */
	public final static String XCZF_LY = "010";
	/** һ����Դ */
	public final static String YBRW_LY = "011";
	/** �������� */
	private AutoCompleteTextView etTaskName;
	/** ������� */
	private EditText completeTime;
	/** ������Դ */
	private Spinner tasksourceSpinner;
	/** �������� */
	private EditText etRemark;
	/** ��������̶� */
	private Spinner taskStateSpinner;
	/** ����쵼 */
	private Spinner leaderSpinner;
	/** �����ҵ */
	private Button add_com_btn,pz_btn,xz_btn,apk_btn;
	/** ��ҵ�б� */
	private ListView qylist;

	/** �������� */
	private EditText taskTypeEditText;

	YutuLoading pd;
	/** ����쵼������ */
	private ArrayAdapter<SpinnerItem> leaderAdapter;
	/** �����̶� */
	private ArrayAdapter<SpinnerItem> stateAdapter;

	/** ������Դ */
	private ArrayAdapter<SpinnerItem> sourceAdapter;

	/** ����쵼���������� */
	List<SpinnerItem> leaderAdapterData;
	/** �����̶����� */
	List<SpinnerItem> stateAdapterData;
	/** ������������ */
	// List<SpinnerItem> typeAdapterData;
	/** ������Դ���� */
	List<SpinnerItem> sourceAdapterData;
	private HashMap<String, Object> rwDetail;
	private String rwlxCode = "";
	ArrayList<HashMap<String, Object>> qynameList = null;
	/** ��ҵguid�ַ������ԣ��ָ� */
	String qyidStr = "";
	String bjqxDate = "";
	public static final String TASK_PATH = Global.SDCARD_RASK_DATA_PATH + "Attach/RWXF/";
	public static String fileName = "";// ��������

	public final int SELECT_SDKARD_FILE = 2;
	private ListView task_attach_list;
	private String imageGuid;
	/** �û��������� **/
	private final String UserAreaCode = Global.getGlobalInstance().getAreaCode();

	/** �����б������� */
	private AttachAdapter attachAdapter;
	private TaskManagerModel taskManagerModel = new TaskManagerModel();
	private ArrayList<HashMap<String, Object>> attachAdapterData = new ArrayList<HashMap<String, Object>>();
	/** �û�Ȩ�� */
	private String userDuty;
	/** �û� ID */
	private String userID;

	private boolean isAdd = true;
	
	private LinearLayout ll_task_shld,ll_task_zxr,ll_task_phr;
	/** ִ���� *//** ����� */
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
		ll_task_shld = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_shld);//�����Ա
		ll_task_zxr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_zxr);//ִ����
		ll_task_phr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_phr);//Э����
		
		zhrEditText = (EditText)taskRegisterView.findViewById(R.id.edit_task_zxr);
		phrEditText = (EditText)taskRegisterView.findViewById(R.id.edit_task_phr);
		task_attach_list = (ListView) taskRegisterView.findViewById(R.id.taskedit_list);
		completeTime = (EditText) taskRegisterView.findViewById(R.id.completeTime);// �������
		etRemark = (EditText) taskRegisterView.findViewById(R.id.etRemark);// ��ע
		tasksourceSpinner = (Spinner) taskRegisterView.findViewById(R.id.Tasksource);// ������Դ
		tasksourceSpinner.setVisibility(View.GONE);
		taskStateSpinner = (Spinner) taskRegisterView.findViewById(R.id.TaskState);// �����̶�
		leaderSpinner = (Spinner) taskRegisterView.findViewById(R.id.leader);// ����쵼
		taskTypeEditText = (EditText) taskRegisterView.findViewById(R.id.TaskType);// EditText��������
		add_com_btn = (Button) taskRegisterView.findViewById(R.id.addqyimg);// �����ҵ
		ll_task_is_zb = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_is_zb);// �Ƿ�ת��
		task_is_zb = (Spinner) taskRegisterView.findViewById(R.id.task_is_zb);// �Ƿ�ת��
		ll_task_zbr = (LinearLayout) taskRegisterView.findViewById(R.id.ll_task_zbr);// ת����
		edit_task_zbr = (EditText) taskRegisterView.findViewById(R.id.edit_task_zbr);// ת����
		qylist = (ListView) taskRegisterView.findViewById(R.id.qylist);
		pz_btn = (Button) taskRegisterView.findViewById(R.id.pz_btn);//���
		xz_btn = (Button) taskRegisterView.findViewById(R.id.xz_btn);//�ļ�
		apk_btn = (Button) taskRegisterView.findViewById(R.id.apk_btn);//���ļ�
		
		
		//		if (sModify != null && sModify.equals("1")) {
//			SetBaseStyle((RelativeLayout) findViewById(R.id.parentLayout), "�����޸�");
//			isAdd = false;
//		} else {
//			SetBaseStyle((RelativeLayout) findViewById(R.id.parentLayout), "���񴴽�");
//			isAdd = true;
//		}

		// ����û���Ȩ��
		userID = Global.getGlobalInstance().getUserid();
		HashMap<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("UserID", userID);

		ArrayList<HashMap<String, Object>> data = SqliteUtil.getInstance().getList("ZW", conditions, "PC_Users");
		if (data != null && data.size() > 0) {
			userDuty = data.get(0).get("zw").toString();
		}
		/** �����û���¼�û���id��ѯ */
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
				startActivityForResult(Intent.createChooser(intent, "��ѡ��һ��Ҫ�ϴ����ļ�"), SELECT_SDKARD_FILE);
			} catch (android.content.ActivityNotFoundException ex) {
				// Potentially direct the user to the Market with a Dialog
				Toast.makeText(getActivity(), "�밲װ�ļ�������", Toast.LENGTH_SHORT).show();
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
		pd.setLoadMsg("���ڼ������ݣ����Ե�...", "");
		pd.showDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (rwGuid != null) {// �����л�������
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
	 * ��ʼ��spinner��������
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

		// �����̶� */
		stateAdapterData = rwxx.gettaskStateSpinnerItem();
		stateAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, stateAdapterData);
		stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// �����Ƿ�ת�� */
		changedAdapterData = rwxx.getTaskChangedSpinnerItem();
		changedAdapter = new ArrayAdapter<SpinnerItem>(getActivity(), android.R.layout.simple_spinner_item, changedAdapterData);
		changedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
	
		
		
		//����ת���жϣ��Ƿ���ʾ�а���
		/** ��õ�¼�û������ŵ�id */
//		String depID = login_user_data.get(0).get("depid").toString();
//		if (depID != null && TextUtils.equals(depID, "440606000000depart") && userDuty.equals("3")) { // ���ܶӵ���Ա
//			handler.sendEmptyMessage(7);
//		}
	}

	// ������������listView�ĸ߶�
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
//					Toast.makeText(getActivity(), "�ݴ�����ɹ�", Toast.LENGTH_SHORT).show();
//					getActivity().finish();
//				}
//			}
//		});

	}

	/** ������޼����¼�,�ж���ѡ���ڴ��ڵ�ǰ���� **/
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
					if (year > year1) { // ��������ڵ�ǰ�ֱ꣬�����ã������ж������
						completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
						flag = 1;
					} else if (year == year1) {
						// ��������ڵ�ǰ�꣬�����¿�ʼ�ж���
						if (monthOfYear > month1) {
							// �����´��ڵ�ǰ�£�ֱ�����ã������ж������
							flag = 1;
							completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + day1);
						} else if (monthOfYear == month1) {
							// �����µ��ڵ�ǰ�£������¿�ʼ�ж���
							if (dayOfMonth > day1) {
								// �����մ��ڵ�ǰ�գ�ֱ�����ã������ж������
								flag = 1;
								completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
							} else if (dayOfMonth == day1) {
								// �����յ��ڵ�ǰ�գ������¿�ʼ�ж�ʱ
								flag = 2;
								completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
							} else { // ������С�ڵ�ǰ�գ���ʾ��������
								flag = 3;
								completeTime.setText("");
								Toast.makeText(getActivity(), "��ѡ�����ڲ���С�ڵ�ǰ����,����������", 2000).show();
							}
						} else { // ������С�ڵ�ǰ�£���ʾ��������
							flag = 3;
							completeTime.setText("");
							Toast.makeText(getActivity(), "��ѡ�����ڲ���С�ڵ�ǰ����,����������", 2000).show();
						}
					} else { // ������С�ڵ�ǰ�꣬��ʾ��������
						flag = 3;
						completeTime.setText("");
						Toast.makeText(getActivity(), "��ѡ�����ڲ���С�ڵ�ǰ����,����������", 2000).show();
					}
					if (flag != 3) {
						Calendar nowDate = Calendar.getInstance(), newDate = Calendar.getInstance();
						nowDate.setTime(new Date());// ����Ϊ��ǰϵͳʱ��
						newDate.set(year, monthOfYear, dayOfMonth);// ����Ϊ1990�꣨6����29��
						long timeNow = nowDate.getTimeInMillis();
						long timeNew = newDate.getTimeInMillis();
//						long dd = (timeNew - timeNow) / (1000 * 60 * 60 * 24)+1;// ��Ϊ��
						long dd = (timeNew - timeNow) / (1000 * 60 * 60 * 24);// ���ֺͷ����һ��
						bjqxDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						completeTime.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "  (���ỹ��" + dd + "��)");
					} else {
						completeTime.setText("");
					}
				}

			}, year1, month1, day1).show();

		}
	}

	/**
	 * ���صײ��˵�
	 */
	public void loadBottomMenu() {
		// ��ȡ�ֻ��ֱ���
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// �����ť�ĸ߿�
		int width = (int) (dm.widthPixels / (double) 2);
		LinearLayout bottom = (LinearLayout) taskRegisterView.findViewById(R.id.bottomLayout);
		bottom.setVisibility(View.VISIBLE);
		final Button tasksend;
		Button back;
		tasksend = new Button(getActivity());
		back = new Button(getActivity());
		tasksend.setBackgroundResource(R.drawable.btn_click);
		tasksend.setText("��   ��");
		tasksend.setTextColor(android.graphics.Color.WHITE);
		tasksend.getPaint().setFakeBoldText(true);// �Ӵ�
		tasksend.setWidth(1);
		back.setBackgroundResource(R.drawable.btn_click);
		back.setText("��   ��");
		back.setTextColor(android.graphics.Color.WHITE);
		back.getPaint().setFakeBoldText(true);// �Ӵ�
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
		 * �����·�
		 */
		tasksend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upFileAndSendTask();//���񴴽�
//				if (task_is_zb.getSelectedItemPosition() == 0) {
//				} else {
//					//����ת��
//					
//				}
			}
		});

		/**
		 * ����
		 */
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Builder builder = new Builder(getActivity());
				builder.setTitle("ȷ���˳���ҳ����");
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.create().show();

			}
		});
	}
	/**
	 * ��������
	 */
	private void upFileAndSendTask(){
		String rwmc = etTaskName.getText().toString();
		String bjqx = bjqxDate;
		String shldId = ((SpinnerItem) (leaderSpinner.getSelectedItem())).getCode();
		if (rwmc.equals("") || bjqx.equals("") || shldId.equals("")) {
			handler.sendEmptyMessage(3);
			return;
		}
		if (TextUtils.isEmpty(RWBH)) {// û��ѡ�񸽼�
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
	private final Handler handler = new Handler() {// UI�߳�Handler
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (pd != null)
					pd.dismissDialog();
				Toast.makeText(getActivity(), "���񴴽��ɹ���", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getActivity(), "���񴴽�ʧ��,�ѱ��浱ǰ����ȴ��ύ��", Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				if (pd != null) {
					pd.dismissDialog();
				}
				Toast.makeText(getActivity(), "���粻ͨ�������������ã�", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if (pd != null)
					pd.dismissDialog();
				Toast.makeText(getActivity(), "������Ϣ��ȫ�������ƴ�*�ŵ�ѡ�", Toast.LENGTH_SHORT).show();
				break;
			case 4:// �������ݼ�����ɣ�ˢ�½���
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
				Toast.makeText(getActivity(), "���񴴽�ʧ��", Toast.LENGTH_SHORT).show();
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
				pd.setLoadMsg("�����ύ�������Ե�...", "");
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
		   
							if (requestRegisterTask(rwGuid).toString().contains("�ɹ�")) {
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
		// ������Դ
		tasksourceSpinner.setAdapter(sourceAdapter);
		String rwly = rwDetail.get("rwly").toString();
		for (int i = 0; i < sourceAdapterData.size(); i++) {
			if (sourceAdapterData.get(i).getCode().equals(rwly)) {
				tasksourceSpinner.setSelection(i);
				break;
			}
		}
		// �����̶�
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
		
		// �ָ���ִ���˺������
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
	 * ����webservice����һ������
	 * 
	 * @param rwGuids
	 *            �����guid
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
		if (TextUtils.isEmpty(RWBH)) {// û��ѡ�񸽼�
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
	 * ����webservice����һ����������
	 * 
	 * @param rwGuid
	 *            �����guid
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
	 *            true Ϊֱ���·� false ���汾�ش��ύ
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
		// �������������͵�¼�û�����������ͬ
		cv.put("syncdataregioncode", UserAreaCode);
		if (tag) {// ֱ���·���Ҫ�ͷ�����ʱ��ͬ��
			String updateTime = DisplayUitl.getServerTime();
			if (updateTime.equals("")) {
				return false;
			}
			cv.put("UpdateTime", updateTime);
			cv.put("FBSJ", updateTime);
			cv.put("rwzt", RWXX.RWZT_WAIT_AUDIT);
			if (rwGuid != null && !rwGuid.equals("")) {// ���²���
				String[] whereArgs = { rwGuid };
				try {
					SqliteUtil.getInstance().update("T_YDZF_RWXX", cv, "guid=?", whereArgs);
					// delFromTaskEnpriLink();
					// addinfoToTaskEnpriLink();
					result = true;
					//�жӳ���������ʱ    ��Ҫ����ִ���˺�Э����
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
					//�жӳ���������ʱ    ��Ҫ����ִ���˺�Э����
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrid", sbZxr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrid", sbZhr.toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zshrname", zhrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "fshrname", phrEditText.getText().toString());
					DisplayUitl.saveAppInfoDataToPreference(getActivity(), rwGuid + "zbhrname", edit_task_zbr.getText().toString());
				}

			}
		} else {
			if (rwGuid != null && !rwGuid.equals("")) {// ���²���
				String[] whereArgs = { rwGuid };
				try {
					SqliteUtil.getInstance().update("T_YDZF_RWXX", cv, "guid=?", whereArgs);
					result = true;
					//�жӳ���������ʱ    ��Ҫ����ִ���˺�Э����
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
					//�жӳ���������ʱ    ��Ҫ����ִ���˺�Э����
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

	// ��������
	public void createDialogViewForTaskType() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View viewex = inflater.inflate(R.layout.expand_list_dialog, null);
		TextView expand_title_tv = (TextView) viewex.findViewById(R.id.expand_title_tv);
		expand_title_tv.setText("�������");

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
		expandableListView.setCacheColorHint(0);// �����϶��б��ʱ���ֹ���ֺ�ɫ����
		expandableListView.setGroupIndicator(getResources().getDrawable(R.layout.expandablelistviewselector));

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.yutu);
		builder.setTitle("��ѡ����������");
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

//	// ����
//	public void photograph(View view) {
//		Toast.makeText(getActivity(), "ssss", Toast.LENGTH_LONG).show();
//	//	takePhoto();
//	}

	private void takePhoto() {// ����

		imageGuid = UUID.randomUUID().toString();
		Intent photo_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(TASK_PATH);
		if (!file.exists())// ��һ�����մ�����Ƭ�ļ���
			file.mkdirs();
		photo_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TASK_PATH + imageGuid + "." + "jpg")));
		photo_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		photo_intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
		startActivityForResult(photo_intent, 1);

	}

	// ѡ��
//	public void takefigure(View view) {
//
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*");
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		try {
//			startActivityForResult(Intent.createChooser(intent, "��ѡ��һ��Ҫ�ϴ����ļ�"), SELECT_SDKARD_FILE);
//		} catch (android.content.ActivityNotFoundException ex) {
//			// Potentially direct the user to the Market with a Dialog
//			Toast.makeText(getActivity(), "�밲װ�ļ�������", Toast.LENGTH_SHORT).show();
//		}
//	}

	// ��װ�ļ�������
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
	 * ��ȡ�����б�����
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
		String[] selections = { "������", "ɾ��" };
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
					tv.setText("���ƣ�");
					ly.addView(tv);
					ly.addView(edtext);
					AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
					ab.setTitle("������");
					ab.setIcon(R.drawable.icon_rename);
					ab.setView(ly);
					ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newName = edtext.getText().toString();

							String id = guid;

							String sql = "update T_Attachment set FileName = '" + newName + "' where guid = '" + id + "'";
							SqliteUtil.getInstance().execute(sql);

							upDataAttathListView();
							Toast.makeText(getActivity(), "�������ɹ���", Toast.LENGTH_LONG).show();
						}
					});
					ab.setNegativeButton("ȡ��", null);
					ab.show();
					break;
				case 1:
					AlertDialog.Builder deleteab = new AlertDialog.Builder(getActivity());
					deleteab.setTitle("ɾ��");
					deleteab.setMessage("��ȷ��Ҫɾ��" + fileName + "��");
					deleteab.setIcon(R.drawable.icon_delete);
					deleteab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {

							deleteFile(guid);
							String sql = "delete from T_Attachment " + " where guid = '" + guid + "'";
							SqliteUtil.getInstance().execute(sql);
							upDataAttathListView();
							Toast.makeText(getActivity(), "ɾ��" + fileName + "�ɹ���", Toast.LENGTH_LONG).show();
						}

					});
					deleteab.setNegativeButton("ȡ��", null);
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
	 * ѡ��ִ���˺������
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
					showDialog("��ѡ��а���", v, 2);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "��ȡ�û���Ϣʧ�ܣ�����ͬ���û�����", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.edit_task_phr:
				try {
					showDialog("��ѡ��а���", v, 3);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "��ȡ�û���Ϣʧ�ܣ�����ͬ���û�����", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.edit_task_zbr:
				try {
					showDialog("��ѡ��ת����", v, 4);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getActivity(), "��ȡ�û���Ϣʧ�ܣ�����ͬ���û�����", Toast.LENGTH_SHORT).show();
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
		String sql = "select * from  PC_DepartmentInfo where depName not like '%˳��%'";
		depData = SqliteUtil.getInstance().queryBySqlReturnArrayListHashMap(sql);
		
		final List<String> groupList = new ArrayList<String>();
		final ArrayList<ArrayList<HashMap<String, Object>>> childMapData = new ArrayList<ArrayList<HashMap<String, Object>>>();
		HashMap<String, Object> condition = new HashMap<String, Object>();
		String currentDeptId = Global.getGlobalInstance().getDepId();
		String userid = Global.getGlobalInstance().getUserid();
		ArrayList<HashMap<String, Object>> temp = null;
		
		switch (userTag) {
		case 0:// �������
			if (userDuty.equals("1")) { //�ֳ�
				condition.put("zw", "'2'");
				//ֻ�����쵼����
				temp = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < depData.size(); i++) {
					if (currentDeptId.equals(depData.get(i).get("depid").toString())) {
						temp.add(depData.get(i));
						break;
					}
				}
				depData.clear();
				depData = temp;
			} else if (userDuty.equals("2")) { //���ֳ�
				condition.put("zw", "'3'");
				//ȥ���쵼����
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
		case 1:// �������
			if (userDuty.equals("1")) {
				condition.put("zw", "'2'");
				//ֻ�����쵼����
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
				//ȥ���쵼����
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
		case 2:// ִ����
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
		case 3:// Э����
			condition.put("1", "2' or zw='0' or zw='3' or zw='4");
			break;
		case 4:// ת����
			condition.put("zw", " '1' or zw='3' ");
			break;
		default:
			break;
		}
		if (userTag == 4) {
			PCDepartmentInfoDao pcDepartmentInfoDao = new PCDepartmentInfoDao();
			depData = pcDepartmentInfoDao.getElseDep(depData);
			depData.remove(login_user_data.get(0));//�Ƴ��������ڵĵ�λ
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
		/** �û�id���� */
		final LinkedList<String> linkedList = new LinkedList<String>();
		/** �û��������� */
		final LinkedList<String> linkedName = new LinkedList<String>();
		ListTreeAdapter selectAuditorAdapter = taskManagerModel
				.getselectAuditorAdapter(customListView,treeBeans, linkedList,
						linkedName, getActivity());
		
		if (userTag == 0) {// ѡ����ִ����
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
		} else if (userTag == 2 || userTag == 4) {// ѡ��ִ����
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
			ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (DisplayUitl.isFastDoubleClick()) {
						return;
					}
					StringBuffer userName = new StringBuffer();
					if (userTag == 1) {// ѡ�������
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

					} else {// ѡ��֪����
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
			ab.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
