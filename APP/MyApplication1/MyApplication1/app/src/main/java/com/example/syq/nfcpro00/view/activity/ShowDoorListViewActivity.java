package com.example.syq.nfcpro00.view.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.aes.userserver.entity.DoorInfoToSend;
import com.aes.userserver.entity.Privilege;
import com.aes.userserver.entity.User;
import com.example.syq.nfcpro00.R;
import com.example.syq.nfcpro00.core.MessageToUserServer;
import com.example.syq.nfcpro00.tools.helper.SerializeHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;

import org.assertj.core.util.Strings;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

//import com.example.syq.nfcpro00.tools.DBhelper.TestOpenHelper;


/**
 * Class Name ShowDoorListViewActivity
 * Created by Gorio on 2018/3/18.
 *
 * @author Gorio
 * @date 2018/3/18
 */
public class ShowDoorListViewActivity extends AppCompatActivity {
org.slf4j.Logger log = LoggerFactory.getLogger(ShowDoorListViewActivity.class);
    private static final String TAG = "ShowDoorListActivity";
    private String wanted_method;
    List<String> list = new ArrayList<>();
    {
        list.add("ROOT");
        list.add("TEMP");
        list.add("COMM");
    }
    ArrayAdapter<String> adapter ;

    String privi="";

    QMUITopBar topbar;

    QMUIGroupListView mGroupListView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlist);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        wanted_method = getIntent().getStringExtra("wanted_method");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);
        topbar = findViewById(R.id.topbar);
        mGroupListView = findViewById(R.id.groupListView);
        try {
            initGroupListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        topbar.addLeftBackImageButton().setOnClickListener((v)->{
            finish();
        });
//        topbar.setTitle("列表详情");

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initGroupListView() throws Exception {
        wanted_method = getIntent().getStringExtra("wanted_method");


        if (!Strings.isNullOrEmpty(wanted_method)){

            switch (wanted_method){
                case "open_door":
                    topbar.setTitle("门锁列表");
                    List<DoorInfoToSend> doorInfoToSends = (List<DoorInfoToSend>)
                            SerializeHelper.deserializeFromString(MessageToUserServer.getDoorInfo(this));
                    List<QMUICommonListItemView> listroot = new ArrayList<>();
                    List<QMUICommonListItemView> listcomm = new ArrayList<>();
                    List<QMUICommonListItemView> listtemp = new ArrayList<>();
                    doorInfoToSends.forEach(d->{
                        QMUICommonListItemView normalItem = mGroupListView.createItemView("Door:"+d.getDoorId());
                        normalItem.setOrientation(QMUICommonListItemView.VERTICAL);
                        normalItem.setDetailText(d.getAddress());
                        switch (d.getUserPrivilege()){
                            case "ROOT":listroot.add(normalItem);break;
                            case "TEMP":listtemp.add(normalItem);break;
                            case "COMM":listcomm.add(normalItem);break;
                                default:break;
                        }
                    });
                    View.OnClickListener onClickListener = v -> {
                        if (v instanceof QMUICommonListItemView) {
                            String text = ((QMUICommonListItemView) v).getText().toString();
                            String doorID = text.substring(5);
                            Intent intent = new Intent(this,InputOpenPasswordActivity.class);
                            intent.putExtra("doorID",doorID);
                            intent.putExtra("mode","open_door");
                            startActivity(intent);
                        }
                    };
                    if (listroot.size()!=0){
                       QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有ROOT权限的门：");
                       listroot.forEach(d-> section.addItemView(d,onClickListener));
                       section.addTo(mGroupListView);
                    }
                    if (listcomm.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有COMM权限的门：");
                        listcomm.forEach(d-> section.addItemView(d,onClickListener));
                        section.addTo(mGroupListView);
                    }
                    if (listtemp.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有TEMP权限的门：");
                        listtemp.forEach(d-> section.addItemView(d,onClickListener));
                        section.addTo(mGroupListView);
                    }
                    break;
                case "show_privilege":
                    topbar.setTitle("权限列表");

                    List<Privilege> privileges = (List<Privilege>) SerializeHelper.deserializeFromString(MessageToUserServer.show_Privilege(this));
                    List<QMUICommonListItemView> prroot = new ArrayList<>();
                    List<QMUICommonListItemView> prcomm = new ArrayList<>();
                    List<QMUICommonListItemView> prtemp = new ArrayList<>();
                    privileges.forEach(p->{
                        QMUICommonListItemView normalItem = mGroupListView.createItemView("Door:"+p.getDoorid());
                        normalItem.setOrientation(QMUICommonListItemView.VERTICAL);
                        normalItem.setDetailText(p.getPrivilege());
                        switch (p.getPrivilege()){
                            case "ROOT":prroot.add(normalItem);break;
                            case "TEMP":prtemp.add(normalItem);break;
                            case "COMM":prcomm.add(normalItem);break;
                            default:break;
                        }

                    });
                    View.OnClickListener onClickListener1 = v -> {
                        if (v instanceof QMUICommonListItemView) {
                            String text = ((QMUICommonListItemView) v).getText().toString();
                            String doorID = text.substring(5);
                            Intent intent = new Intent(this,InputOpenPasswordActivity.class);

                            intent.putExtra("mode","addUserToDoor");

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("将门"+doorID+"赋予新用户");
                            LinearLayout addDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_view,null);

                            Button sayyes = (Button) addDialog.findViewById(R.id.sayyes);
                            Spinner spinner = (Spinner) addDialog.findViewById(R.id.spinner);
                            EditText inputaddUserId = (EditText) addDialog.findViewById(R.id.input_add_UserId);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                            spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                    privi = adapterView.getItemAtPosition(position).toString();
                                    log.info("privilege {} select",privi);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    privi = "ROOT";
                                }
                            });
                            builder.setView(addDialog);
                            builder.setCancelable(true);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            sayyes.setOnClickListener(v1->{
                                String userToChange = inputaddUserId.getText().toString();

                                intent.putExtra("doorID",doorID);
                                intent.putExtra("mode","addUserToDoor");
                                intent.putExtra("userToChange",userToChange);
                                intent.putExtra("privi",privi);
                                startActivity(intent);

                            });

                        }
                    };
                    if (prroot.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("ROOT权限下的门：");
                        prroot.forEach(d-> section.addItemView(d,onClickListener1));
                        section.addTo(mGroupListView);
                    }
                    if (prcomm.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("COMM权限下的门：");
                        prcomm.forEach(d-> section.addItemView(d,onClickListener1));
                        section.addTo(mGroupListView);
                    }
                    if (prtemp.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("TEMP权限下的门：");
                        prtemp.forEach(d-> section.addItemView(d,onClickListener1));
                        section.addTo(mGroupListView);
                    }
                    break;
                case "show_door_info":
                    topbar.setTitle("门锁列表");
                    List<DoorInfoToSend> doorInfoToSends1 = (List<DoorInfoToSend>)
                            SerializeHelper.deserializeFromString(MessageToUserServer.getDoorInfo(this));
                    List<QMUICommonListItemView> listroot1 = new ArrayList<>();
                    List<QMUICommonListItemView> listcomm1 = new ArrayList<>();
                    List<QMUICommonListItemView> listtemp1 = new ArrayList<>();
                    doorInfoToSends1.forEach(d->{
                        QMUICommonListItemView normalItem = mGroupListView.createItemView("Door:"+d.getDoorId());
                        normalItem.setOrientation(QMUICommonListItemView.VERTICAL);
                        normalItem.setDetailText(d.getAddress());
                        switch (d.getUserPrivilege()){
                            case "ROOT":listroot1.add(normalItem);break;
                            case "TEMP":listtemp1.add(normalItem);break;
                            case "COMM":listcomm1.add(normalItem);break;
                            default:break;
                        }
                    });
                    View.OnClickListener onClickListener2 = v -> {
                        if (v instanceof QMUICommonListItemView) {
                            String text = ((QMUICommonListItemView) v).getText().toString();
                            String doorID = text.substring(5);
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("将门"+doorID+"赋予新用户");
                            LinearLayout addDialog = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_view,null);

                            Button sayyes = (Button) addDialog.findViewById(R.id.sayyes);
                            Spinner spinner = (Spinner) addDialog.findViewById(R.id.spinner);
                            EditText inputaddUserId = (EditText) addDialog.findViewById(R.id.input_add_UserId);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                            spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                    privi = adapterView.getItemAtPosition(position).toString();
                                    log.info("privilege {} select",privi);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    privi = "ROOT";
                                }
                            });
                            builder.setView(addDialog);
                            builder.setCancelable(true);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            sayyes.setOnClickListener(v1->{
                                String userToChange = inputaddUserId.getText().toString();
                                Intent intent = new Intent(this,InputOpenPasswordActivity.class);
                                intent.putExtra("doorID",doorID);
                                intent.putExtra("mode","addDoorToUser");
                                intent.putExtra("userToChange",userToChange);
                                intent.putExtra("privi",privi);
                                startActivity(intent);

                            });

                        }
                    };
                    if (listroot1.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有ROOT权限的门：");
                        listroot1.forEach(d-> section.addItemView(d,onClickListener2));
                        section.addTo(mGroupListView);
                    }
                    if (listcomm1.size()!=0){
                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有COMM权限的门：");
                        listcomm1.forEach(d-> section.addItemView(d,onClickListener2));
                        section.addTo(mGroupListView);
                    }
                    if (listtemp1.size()!=0){

                        QMUIGroupListView.Section section= QMUIGroupListView.newSection(getBaseContext())
                                .setTitle("拥有TEMP权限的门：");
                        listtemp1.forEach(d-> section.addItemView(d,onClickListener2));
                        section.addTo(mGroupListView);
                    }
                    break;
                case "show_user_info":
                    topbar.setTitle("个人信息");
                    User user = (User)SerializeHelper.deserializeFromString(MessageToUserServer.show_User_Info(this));
                    QMUICommonListItemView normalItem = mGroupListView.createItemView("ID");
                    normalItem.setDetailText(user.getClientId());
                    QMUICommonListItemView normalItem1 = mGroupListView.createItemView("昵称");
                    normalItem1.setDetailText(user.getClientName());
                    View.OnClickListener onClickListener3 = v -> {
                        if (v instanceof QMUICommonListItemView) {
                            String text = ((QMUICommonListItemView) v).getText().toString();
                            TastyToast.makeText(this,text,TastyToast.LENGTH_SHORT,TastyToast.INFO).show();
                        }
                    };
                    QMUIGroupListView.newSection(getBaseContext())
                            .setTitle("个人信息展示：")
                            .addItemView(normalItem,onClickListener3)
                            .addItemView(normalItem1,onClickListener3)
                            .addTo(mGroupListView);

                    break;
                default:
                    TastyToast.makeText(this,"页面创建失败，请检测网络状况",TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
                    break;
            }
        }
        else {
            TastyToast.makeText(this,"页面创建失败，请检测网络状况",TastyToast.LENGTH_SHORT,TastyToast.ERROR).show();
        }

    }
}
