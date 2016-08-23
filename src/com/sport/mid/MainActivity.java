  package com.sport.mid;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.serialport.apis.PortControl;
import com.serialport.apis.SerialPort;

public class MainActivity extends Activity {

	private static final String TAG = SerialPort.class.getSimpleName();

	private PortControl mCom;
	private Spinner spPort;
	private Spinner Baund;
	private String[] devicePaths;
	private int nCurSelDev = 0;
	private int BaundIndex = 0;
	private String[] baundStrings;
	private Button btStart, btSend,btClear;
	private EditText textdata;
	private TextView showdata;
	private ScrollView mscViews;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textdata = (EditText) findViewById(R.id.sendData);
		showdata = (TextView) findViewById(R.id.showtext);
		mscViews = (ScrollView) findViewById(R.id.scView1);
		mCom = new PortControl(this);
		baundStrings = getResources().getStringArray(R.array.BaundString);

		String[] devices = mCom.getSPortFinder().getAllDevices();
		devicePaths = mCom.getSPortFinder().getAllDevicesPath();

		spPort = (Spinner) findViewById(R.id.spPort);
		Baund = (Spinner) findViewById(R.id.Baund);
		ArrayAdapter<String> adProvince = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, devices);
		adProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spPort.setAdapter(adProvince);

		ArrayAdapter<String> adProvince1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, baundStrings);
		adProvince1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Baund.setAdapter(adProvince1);

		spPort.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				nCurSelDev = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		Baund.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				BaundIndex = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		btStart = (Button) findViewById(R.id.btStart);
		btSend = (Button) findViewById(R.id.btSend);
		btClear=(Button)findViewById(R.id.btClear);

		btStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btStart.getText().equals("Start")) {
					
					int res = open();
					if (res == 1) {
						btStart.setText("Stop");
						btStart.setBackgroundColor(Color.parseColor("#FF0000"));
						
						mCom.getSPort().startRead();
						Toast.makeText(MainActivity.this, "打开成功",Toast.LENGTH_SHORT).show();
					} else if (res == 0) {
						Toast.makeText(MainActivity.this, "已打开",Toast.LENGTH_SHORT).show();
					} else if (res < 0) {
						Toast.makeText(MainActivity.this, "打开失败",Toast.LENGTH_SHORT).show();
					}
				} else {
					btStart.setText("Start");
					btStart.setBackgroundColor(Color.parseColor("#33dd11"));
					mCom.getSPort().ClosePort();
					Toast.makeText(MainActivity.this, "Close",Toast.LENGTH_SHORT).show();
				}
			}
		});

		btSend.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = textdata.getText().toString().trim();
				str="测试数据。。。。";
				if (str != null && str.length()!=0) {
					//int res = mCom.getSPort().write_string(str);
					//二进制数据
					ByteBuffer buffer0=ByteBuffer.allocate(256);
					// 0X24	0X23 	0X05	0X02	ID0	ID1	COM	~COM	0X23	0X24
					byte[] buffer=new byte[9];
					buffer[0]=0x7E;
					buffer[1]=0x01;
					buffer[2]=0x04;
					buffer[3]=0x09;
					buffer[4]=0x01;
					buffer[5]=0x02;
					buffer[6]=0x07;
					buffer[7]=0x2A;
					buffer[8]=0x7E;
					
					int res = mCom.getSPort().write_bytes(buffer);
					if (res >= 0) {
						showTips("发送数据："+ str);
					} else {
						showTips("发送数据："+ str + " 发送失败！");
					}
				}
			}
		});
		
		btClear.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showdata.setText("");
			}
		});

		IntentFilter filter = new IntentFilter("CUS.OPEN");
		Receiver receiver = new Receiver();
		registerReceiver(receiver, filter);
	}
	
	private void showTips(String tip){
		String tips=(showdata.getText()==null?tip:showdata.getText()+tip);
		showdata.setText(tips+"\n\r");
		srun();
	}

	public void srun() {
		int off = showdata.getMeasuredHeight() - mscViews.getHeight();
		if (off > 0) {
			mscViews.scrollTo(0, off);
		}
	}

	private int open() {
		Log.d(TAG, "devicePath=" + devicePaths[nCurSelDev]);
		return mCom.getSPort().OpenPortWithPath(devicePaths[nCurSelDev], Integer.valueOf(baundStrings[BaundIndex]), 8, 'N', 1, 0, 512, mCom.getCallBack());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
			}
		});
		b.show();
	}

	

	public class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub 
			Bundle bundle=intent.getExtras();
			String bytess=bundle.getString("buffer");
			
			showTips("接收数据:"+bytess);
		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mCom != null) {
			mCom.freePortControl();
		}
		mCom = null;

		super.onDestroy();
	}
}

