package com.example.syq.nfcpro00.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.example.syq.nfcpro00.MainActivity;
import com.example.syq.nfcpro00.R;

import com.example.syq.nfcpro00.core.MessageToKDC;
import com.example.syq.nfcpro00.core.MessageToUserServer;
import com.example.syq.nfcpro00.tools.helper.PreferenceHelper;
import com.sdsmdg.tastytoast.TastyToast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


/**
 * Class Name SignupActivity
 * Created by Gorio on 2018/3/18.
 *
 * @author Gorio
 * @date 2018/3/18
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
     Logger log = LoggerFactory.getLogger(LoginActivity.class);


    EditText tx_nickName;

    EditText tx_loginPassWd;

    EditText tx_openPassWd;

    AppCompatButton btnSignup;

    TextView linkLogin;

    MaterialProgressBar signuprogressBar;

    String nickName;
    String loginPassword;
    String openPassword;
    String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        ButterKnife.bind(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        tx_nickName = findViewById(R.id.new_user_nick_name);
        tx_loginPassWd =findViewById(R.id.input_NEW_password);

        tx_openPassWd = findViewById(R.id.input_openPassword);
        btnSignup = findViewById(R.id.btn_signup);

        linkLogin = findViewById(R.id.link_login);

        signuprogressBar =findViewById(R.id.signuprogressBar);



        btnSignup.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
        signuprogressBar.setVisibility(View.GONE);
    }
    public void signup() throws Exception {
        if (!validate()) {

            onSignupFailed();
            return;
        }
        nickName = tx_nickName.getText().toString();
        loginPassword = tx_loginPassWd.getText().toString();
        openPassword = tx_openPassWd.getText().toString();
        btnSignup.setEnabled(false);
        signuprogressBar.setIndeterminate(true);
        signuprogressBar.showContextMenu();
        signuprogressBar.setVisibility(View.VISIBLE);
        userID = MessageToKDC.getUserID(this);
        log.info("userID==>"+userID);
        if (!"Refuse".equals(userID)){
            SharedPreferences.Editor editor = PreferenceHelper.getSharedPreferences(this).edit();
            editor.putString("userID",userID);
            editor.apply();
            String kc = MessageToKDC.updataUserSharedKey(userID);
            log.info("kc==>"+kc);
            if (!"Refuse".equals(kc)){
                editor.putString("kc",kc);
                editor.apply();
                String result = MessageToUserServer.newUser(this,loginPassword,nickName,openPassword);
                log.info("result==>{}",result);
                if (!"Refuse".equals(result)){
                    editor.putString("nickName",nickName);
                    editor.apply();
                    new Handler().postDelayed((() -> {
                        onSignupSuccess();
                        signuprogressBar.setVisibility(View.GONE);
                    }),3000);
                }
                else {
                    new Handler().postDelayed((() -> {
                        onSignupFailed();
                        signuprogressBar.setVisibility(View.GONE);
                    }),3000);
                }
            }
            else {
                new Handler().postDelayed((() -> {
                    onSignupFailed();
                    signuprogressBar.setVisibility(View.GONE);
                }),3000);
            }
        }
        else {
            new Handler().postDelayed((() -> {
                onSignupFailed();
                signuprogressBar.setVisibility(View.GONE);
            }),3000);
        }


    }
    public void onSignupSuccess() {
        SharedPreferences.Editor editor = PreferenceHelper.getSharedPreferences(this).edit();
        editor.putString("nickName",nickName);
        editor.putString("userID",userID);
        editor.apply();
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        TastyToast.makeText(getBaseContext(), "Sign up failed", TastyToast.LENGTH_LONG,
                TastyToast.ERROR).show();
        btnSignup.setEnabled(true);
    }

    public boolean validate() {
        nickName = tx_nickName.getText().toString();
        loginPassword = tx_loginPassWd.getText().toString();
        openPassword = tx_openPassWd.getText().toString();
        boolean valid = true;
        log.info(" nickName "+nickName);
        log.info("loginPassword "+loginPassword);
        log.info("openPassword "+openPassword);
        if (nickName.isEmpty() || nickName.length() > 16 || nickName.length() < 4) {
            tx_nickName.setError("昵称长度位4到15位");
            valid = false;
        } else {
            tx_nickName.setError(null);
        }
        if (loginPassword.isEmpty() || loginPassword.length() > 16 || loginPassword.length() < 6) {
            tx_loginPassWd.setError("登陆密码长度为6到16位");
            valid = false;
        } else {
            tx_loginPassWd.setError(null);
        }
        if (openPassword.isEmpty() || openPassword.length() > 16 || openPassword.length() < 6) {
            tx_openPassWd.setError("开门密码长度为6到16位");
            valid = false;
        } else {
            tx_openPassWd.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                try {
                    signup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.link_login:
                finish();
                break;
            default:
                break;
        }
    }
}
