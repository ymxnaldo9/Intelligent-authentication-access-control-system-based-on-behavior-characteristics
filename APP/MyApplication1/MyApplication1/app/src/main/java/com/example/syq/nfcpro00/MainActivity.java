package com.example.syq.nfcpro00;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.syq.nfcpro00.core.MessageToKDC;

import com.example.syq.nfcpro00.tools.crypto.rsa.RSAUtils;
import com.example.syq.nfcpro00.tools.ShadowTransformer;
import com.example.syq.nfcpro00.tools.adapter.CardFragmentPagerAdapter;
import com.example.syq.nfcpro00.tools.adapter.CardPagerAdapter;
import com.example.syq.nfcpro00.tools.helper.PreferenceHelper;
import com.example.syq.nfcpro00.tools.helper.dbhelper.MySQLiteOpenHelper;
import com.example.syq.nfcpro00.tools.items.CardItem;
import com.example.syq.nfcpro00.tools.utils.DeviceUtils;
import com.example.syq.nfcpro00.view.activity.LoginActivity;
import com.example.syq.nfcpro00.view.activity.ShowDoorListViewActivity;
import com.idescout.sql.SqlScoutServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * @author Gorio
 */

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final int MESSAGE_SENT = 1;
    Logger log = LoggerFactory.getLogger(MainActivity.class);


    private static boolean isSQLLinstening = false;

    @BindView(R.id.cardTypeBtn)
    Button mButton;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;


    Map<String,Object> keymap = null;
    String userID ;

    NfcAdapter mNfcAdapter;
    public static Map<String,String> keyMap = new HashMap<>();
    private static List<CardItem> items;
    private ShadowTransformer mCardShadowTransformer;
    private ShadowTransformer mFragmentCardShadowTransformer;

    {
        items = new ArrayList<>();
        items.add(new CardItem(R.string.tabSegment_item_1_title,
                R.string.tabSegment_item_1_content,
                R.string.tabSegment_item_1_button,
                null,
                view ->
                {
                    Intent intent = new Intent(getApplicationContext(), ShowDoorListViewActivity.class);
                    intent.putExtra("wanted_method","open_door");
                    //门锁信息展示
                    startActivity(intent);
                }));
        items.add(new CardItem(R.string.tabSegment_item_3_title, R.string.tabSegment_item_3_content,
                R.string.tabSegment_item_3_button,
                "#4B0082",
                view -> {
                    Intent intent = new Intent(getApplicationContext(), ShowDoorListViewActivity.class);
                    intent.putExtra("wanted_method","show_privilege");
                    //修改权限信息
                    startActivity(intent);
                }));
        items.add(new CardItem(R.string.tabSegment_item_4_title, R.string.tabSegment_item_4_content,
                R.string.tabSegment_item_4_button,
                null,
                view -> {
                    Intent intent = new Intent(getApplicationContext(), ShowDoorListViewActivity.class);
                    intent.putExtra("wanted_method","show_door_info");
                    startActivity(intent);
                }));
        items.add(new CardItem(R.string.tabSegment_item_5_title,
                R.string.tabSegment_item_5_content,
                R.string.tabSegment_item_5_button,
                "#BA55D3",
                view -> {
                    Intent intent = new Intent(getApplicationContext(), ShowDoorListViewActivity.class);
                    intent.putExtra("wanted_method","show_user_info");
                    startActivity(intent);
                }));

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!isSQLLinstening){
            SqlScoutServer.create(this, getPackageName());
            isSQLLinstening=!isSQLLinstening;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*
         *获取到keys 包括但不限于APP公钥，私钥，KDC公钥，IMEI
         * 获取到后放到SharedPreferences文件中
         */
        if (keyMap.size()<4) {

            MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "keys.db", null, 1);
            dbHelper.setSQL("create table app_keys (id integer primary key autoincrement,name text,key_string text)");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("select * from app_keys", null);
            //如果表里面的值小于四项就说明里面没有APP公钥，私钥，KDC公钥，需要写进去
            if (cursor.getCount() < 4) {
                //空表
                if (cursor.getCount() != 0) {
                    db.execSQL("drop table if exists app_keys");
                    db.execSQL("create table app_keys (id integer primary key autoincrement,name text,key_string text)");
                }
                ContentValues values = new ContentValues();
                try {
                    keymap = RSAUtils.genKeyPair();
                    values.put("name", "app_public_key");
                    String temp =  RSAUtils.getPublicKey(keymap);
                    values.put("key_string", temp);
                    db.insert("app_keys", null, values);
                    log.info("produce app_public_key==>{}",temp);
                    keyMap.put("app_public_key", RSAUtils.getPublicKey(keymap));
                    values.clear();
                    values.put("name", "app_private_key");
                    temp =RSAUtils.getPrivateKey(keymap);
                    values.put("key_string",temp );
                    db.insert("app_keys", null, values);
                    log.info("produce app_private_key ==>{}",temp);
                    keyMap.put("app_private_key", RSAUtils.getPrivateKey(keymap));
                    values.clear();
                    values.put("name", "kdc_public_key");
                    values.put("key_string", getResources().getString(R.string.kdc_public_key));
                    db.insert("app_keys", null, values);
                    log.info("produce kdc_public_key");
                    keyMap.put("kdc_public_key", getResources().getString(R.string.kdc_public_key));
                    values.clear();
                    values.put("name", "app_IMEI");

                    values.put("key_string", DeviceUtils.getUniqueId(this));
                    temp = DeviceUtils.getUniqueId(this);
                    keyMap.put("app_IMEI",temp);
                    db.insert("app_keys", null, values);
                    log.info("produce kdc_public_key");
                    values.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if ("Refuse".equals(MessageToKDC.newApp(keyMap))){
                        log.error("注册app失败");
                    }
                    else {
                        log.info("注册成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//所有基础项都在
            else {
                cursor = db.query("app_keys", null, null, null, null, null, null);
                //查询key表中所有的数据
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("key_string"));
                        log.info("GET Key:{},Value:{}", name, author);
                        keyMap.put(name, author);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(this);
        userID=preferences.getString("userID","");
        mButton.setText("当前用户:"+userID);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mButton.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), ShowDoorListViewActivity.class);
            intent.putExtra("wanted_method","show_user_info");
            startActivity(intent);
        });

        CardPagerAdapter mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItems(items);

        CardFragmentPagerAdapter mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mCardShadowTransformer.enadleScaling(true);
        mFragmentCardShadowTransformer.enadleScaling(true);

    }


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCardShadowTransformer.enadleScaling(b);
        mFragmentCardShadowTransformer.enadleScaling(b);
    }

    public NdefMessage createNdefMessage(NfcEvent event) {
        //TODO info从服务器中获取
        String info = "123456";
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
                "application/com.example.android.beam", info.getBytes())
        });
        return msg;
    }

    public void onNdefPushComplete(NfcEvent arg0) {
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Message sent!",
                            Toast.LENGTH_LONG).show();
                    break;
                    default:break;
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Toast.makeText(this, "msgSet" +msg.getRecords()[0].getPayload(), Toast.LENGTH_LONG).show();
    }
    public NdefRecord createMimeRecord(String mimeType, byte[] payload){
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
}


