package com.example.syq.nfcpro00.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.TextView;

import com.example.syq.nfcpro00.MainActivity;
import com.example.syq.nfcpro00.R;

import com.example.syq.nfcpro00.core.MessageToKDC;
import com.example.syq.nfcpro00.core.MessageToUserServer;
import com.example.syq.nfcpro00.tools.MessageDecomposition;
import com.example.syq.nfcpro00.tools.crypto.Aes;
import com.example.syq.nfcpro00.tools.crypto.rsa.RSAUtils;
import com.example.syq.nfcpro00.tools.crypto.sha1.Sha1;
import com.example.syq.nfcpro00.tools.helper.PreferenceHelper;
import com.example.syq.nfcpro00.tools.helper.TransformationHelper;
import com.example.syq.nfcpro00.tools.utils.MessageVerification;
import com.sdsmdg.tastytoast.TastyToast;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Class Name LoginActivity
 * Created by Gorio on 2018/3/18.
 *
 * @author Gorio
 * @date 2018/3/18
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Logger log = LoggerFactory.getLogger(LoginActivity.class);
    private static final int REQUEST_SIGN_UP = 0;

    EditText inputEmail;

    EditText inputPassword;

    AppCompatButton btnLogin;

    TextView linkSignup;

    MaterialProgressBar progressBar;


    String userID ;
    String loginPassword;
    String privilege;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        inputEmail =findViewById(R.id.inputUserId);

        inputPassword = findViewById(R.id.inputPassWd);

        btnLogin = findViewById(R.id.btnlogin);

        linkSignup =findViewById(R.id.linksignup);

        progressBar = findViewById(R.id.loginprogressBar);


        //获取到ID 和开门密码

        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(this);
        String lastLoginUserId = preferences.getString("userID","");
        if (!Strings.isNullOrEmpty(lastLoginUserId)){
            inputEmail.setText(lastLoginUserId);
        }
        userID = inputEmail.getText().toString();
        loginPassword = inputPassword.getText().toString();
        btnLogin.setOnClickListener(this);
        linkSignup.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login() throws Exception {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        userID = inputEmail.getText().toString();
        loginPassword = inputPassword.getText().toString();
        btnLogin.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.showContextMenu();
        progressBar.setVisibility(View.VISIBLE);
        String kc = MessageToKDC.updataUserSharedKey(userID);
        log.info("kc==>"+kc);
        if (!"Refuse".equals(kc)){
            SharedPreferences.Editor editor = PreferenceHelper.getSharedPreferences(this).edit();
            editor.putString("userID",userID);
            editor.putString("kc",kc);
            editor.apply();
            String result = MessageToUserServer.userLogin(this,loginPassword);
            log.info("result==>"+result);
            if (!"Refuse".equals(result)){

                new Handler().postDelayed((() -> {
                    onLoginSuccess();
                    progressBar.setVisibility(View.GONE);
                }),3000);
            }
            else {
                new Handler().postDelayed((() -> {
                    onLoginFailed();
                    progressBar.setVisibility(View.GONE);
                }),3000);
            }
        }
        else {
            new Handler().postDelayed((() -> {
                onLoginFailed();
                progressBar.setVisibility(View.GONE);
            }),3000);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public boolean validate() {
        boolean valid = true;
        userID = inputEmail.getText().toString();
        loginPassword = inputPassword.getText().toString();
        String username = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (username.isEmpty() || username.length() != 6) {
            inputEmail.setError("请输入一个用户名");
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            inputPassword.setError("密码长度为6到16位");
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    /**
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnlogin:
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.linksignup:
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        Intent intentUserId = new Intent(LoginActivity.this, MainActivity.class);
        TastyToast.makeText(this,"Login success!",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
        startActivity(intentUserId);
        finish();
    }

    public void onLoginFailed() {
        TastyToast.makeText(getBaseContext(), "Login failed", TastyToast.LENGTH_LONG,
                TastyToast.ERROR).show();
        btnLogin.setEnabled(true);
    }

    /**
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == RESULT_OK) {


                this.finish();
            }
        }
    }
}
