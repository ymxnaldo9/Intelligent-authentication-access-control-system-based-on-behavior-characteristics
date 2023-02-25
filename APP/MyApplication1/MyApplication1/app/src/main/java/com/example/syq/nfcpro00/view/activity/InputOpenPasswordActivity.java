package com.example.syq.nfcpro00.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.syq.nfcpro00.R;
import com.example.syq.nfcpro00.core.MessageToUserServer;
import com.example.syq.nfcpro00.view.view.PasswordView;
import com.sdsmdg.tastytoast.TastyToast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import android.text.format.Time;

/**
 * Project Name MyApplication1
 * Packege Name com.example.syq.nfcpro00.view.activity
 * Class Name InputOpenPasswordActivity
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/15 14:32
 */
public class InputOpenPasswordActivity extends AppCompatActivity implements
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {
    Logger log = LoggerFactory.getLogger(InputOpenPasswordActivity.class);
    private static final int MESSAGE_SENT = 1;
    String NFCmessage;
    String doorId;
    String mode;
    PasswordView pwdView;
    String userToChange;
    String privi;

    NfcAdapter mNfcAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opw);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        log.info("进入inputopendoor Activity");
        pwdView = findViewById(R.id.pwd_view);
        doorId= getIntent().getStringExtra("doorID");
        mode= getIntent().getStringExtra("mode");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if ("addDoorToUser".equals(mode)){
            userToChange = getIntent().getStringExtra("userToChange");
            privi = getIntent().getStringExtra("privi");
        }
        if ("addUserToDoor".equals(mode)){
            userToChange = getIntent().getStringExtra("userToChange");
            privi = getIntent().getStringExtra("privi");
        }
        pwdView.setOnFinishInput(() -> {
            modeSelect(mode);
        });

    }

    private void modeSelect(String s) throws Exception {
        switch (s){
            case "open_door":
                String result=MessageToUserServer.openDoor(this,doorId,pwdView.getStrPassword());
                if (!"Refuse".equals(result)){
                    NFCmessage = result;
                    log.info("收到结果信息"+result);
                    TastyToast.makeText(this,doorId+"服务器比对成功，进行NFC开门",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
// Check for available NFC Adapter

                    if (mNfcAdapter == null) {
                        Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show();
                    }
                    if (!mNfcAdapter.isEnabled()) {
                        Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
                    }
                    // Register callback to set NDEF message
                    mNfcAdapter.setNdefPushMessageCallback(this, this);
                    // Register callback to listen for message-sent success
                    mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
//                    finish();
                }
                break;
            case "addDoorToUser":
                String result1 = MessageToUserServer.addDoorToUser(this,doorId,userToChange,privi,pwdView.getStrPassword());
                if (!"Refuse".equals(result1)){
                    TastyToast.makeText(this,"添加用户成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
                    finish();
                }
                break;
            case "addUserToDoor":

                String result2 = MessageToUserServer.addUserToDoor(this,doorId,userToChange,privi,pwdView.getStrPassword());
                if (!"Refuse".equals(result2)){
                    TastyToast.makeText(this,"添加权限成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
                    finish();
                }
                break;

            default:break;
        }
    }
    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
@Override
    public NdefMessage createNdefMessage(NfcEvent event) {

    log.info("NFC Message ==>{}",NFCmessage);
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
                "application/vnd.com.example.android.beam", NFCmessage.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the
                 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this activity starts when
                 * receiving a beamed message. For now, this code uses the tag dispatch
                 * system.
                */
                // ,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
@Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
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
                    default: break;
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
        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        //TODO 这里进行信息发送
        Toast.makeText(this, "进行信息发送" + new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_warning)
                    .setMessage(R.string.exit_isexit)
                    .setNegativeButton(R.string.exit_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }

                            })

                    .setPositiveButton(R.string.exit_sure,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    finish();

                                }
                            }).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);

        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        operateMethod();
    }

    public void operateMethod() {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.choice);
        String[] strArray = { getString(R.string.menu_settings),
                getString(R.string.menu_help) };
        b.setItems(strArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Resources resource = getResources();
                Configuration config = resource.getConfiguration();
                switch (which) {
                    case 0:
                        Intent intent = new Intent(
                                Settings.ACTION_NFCSHARING_SETTINGS);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent helpIntent = new Intent(this,
                                helpActivity.class);
                        startActivity(helpIntent);
                        break;
                }
            }
        });
        b.create().show();
    }*/
}
