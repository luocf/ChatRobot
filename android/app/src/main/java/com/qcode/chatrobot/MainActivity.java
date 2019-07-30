package com.qcode.chatrobot;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.qcode.chatrobot.common.MyQRCode;
import com.qcode.chatrobot.manager.GroupInfo;
import com.qcode.chatrobot.manager.GroupManager;
import com.qcode.chatrobot.ui.GroupListAdapter;

import java.util.List;

import static com.qcode.chatrobot.manager.GroupManager.*;

public class MainActivity extends Activity implements GroupListener {
    public static final String TAG = "ChatRobot";
    private ListView mGroupListView;
    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private Context mContext;
    private GroupListAdapter mAdapter;
    private GroupManager mGroupManager;
    private int mCurrentGroupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this.getApplicationContext();
        mCurrentGroupId = -1;
        mGroupListView = (ListView) findViewById(R.id.groupList);
        //获取当前ListView点击的行数，并且得到该数据
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupInfo groupInfo = mGroupManager.getGroupList().get(position);
                mGroupManager.switchGroup(groupInfo.mId);
            }
        });
        mGroupListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                GroupInfo groupInfo = mGroupManager.getGroupList().get(position);
                mGroupManager.switchGroup(groupInfo.mId);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
        
            }
        });
        mAdapter = new GroupListAdapter(this.getLayoutInflater());
        mGroupListView.setAdapter(mAdapter);
       
        mGroupManager = new GroupManager(mContext);
        mGroupManager.registerGroupListener(this);
        mGroupManager.recoveryGroup();//恢复旧数据
        
        Button createBtn = findViewById(R.id.createBtn);
        
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupManager.createGroup();
            }
        });
        
        
        mAdapter.setData( mGroupManager.getGroupList());
        if ( mGroupManager.getGroupsSize() > 0) {
            mGroupListView.requestFocus();
        } else {
            createBtn.requestFocus();
        }
        showGroupInfo();
    }
    
    @Override
    public void onDestroy() {
        mGroupManager.destroy();
        super.onDestroy();
    }
    
    private String getAddress() {
    
        return "";
    }
    
    private String getUserId() {
        return "";
    }
    
    private void showGroupInfo() {
        int id = mGroupManager.getCurrentId();
        GroupInfo groupInfo =  mGroupManager.getGroupInfo(id);
        if (groupInfo != null) {
            synchronized (groupInfo) {
                String address = groupInfo.mAddress;
                if (id != mCurrentGroupId || address != null) {
                    mCurrentGroupId = id;
                    if (address != null) {
                        MyQRCode qrcode = new MyQRCode(-1, 1);
                        ImageView view = findViewById(R.id.qr_image);
                        view.setImageBitmap(qrcode.getBitmap(address, 512, 512));
                        TextView text_userid = findViewById(R.id.text_groupid);
                        text_userid.setText("当前为："+(id+1)+"群");
                    }
                }
            }
        }
        //更新列表信息
        mAdapter.setData(mGroupManager.getGroupList());
    }
    @Override
    public void onGroupInfoUpdate() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                showGroupInfo();
            }
        });
    }
    
}
