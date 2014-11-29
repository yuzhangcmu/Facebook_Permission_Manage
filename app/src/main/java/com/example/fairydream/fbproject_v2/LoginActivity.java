package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A login screen that offers login via password.

 */
public class LoginActivity extends Activity {
    private Button status;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private String username;
    private String password;

    private final String serverAddress = "http://ec2-54-165-160-47.compute-1.amazonaws.com";
    private LoginHandler loginHandler;

    private final int SIGNUP_SUCCESS = 1;
    private final int SIGNUP_FAILURE = 2;
    private final int SIGNIN_SUCCESS = 3;
    private final int SIGNIN_FAILURE = 4;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        status = (Button) findViewById(R.id.sign_in_button);
        usernameEditText = (EditText) findViewById(R.id.username_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        loginHandler = new LoginHandler();
        DBManager dbManager = new DBManager(this);
        if(!dbManager.isUserRegister())
        {
            // if it's the first time to launch this permission manager
            status.setText("Sign Up");
            status.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();

                    SignUpThread signUpThread = new SignUpThread();
                    new Thread(signUpThread).start();
                }

            });
        }
        else
        {
            status.setOnClickListener(new Button.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();

                    SignInThread signInThread = new SignInThread();
                    new Thread(signInThread).start();

                }

            });
        }
        dbManager.close();
    }


    class LoginHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what) {

                case SIGNIN_SUCCESS:
                    Intent intent1 = new Intent();
                    intent1.setClass(LoginActivity.this, AppManagerActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    break;
                case SIGNIN_FAILURE:
                    status.setText("Sign in failed. " + msg.obj.toString());
                    break;
                case SIGNUP_SUCCESS:
                    DBManager dbManager = new DBManager(LoginActivity.this);
                    dbManager.addUserToken(msg.obj.toString(),username);
                    Intent intent2 = new Intent();
                    intent2.setClass(LoginActivity.this, AppManagerActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    break;
                case SIGNUP_FAILURE:
                    status.setText("Sign up failed." + msg.obj.toString());
                    break;
            }
        }
    }


    class SignUpThread implements Runnable
    {

        @Override
        public void run()
        {
            //

            //       Message.obtain(loginHandler, SIGNUP_SUCCESS, "weeweew").sendToTarget();

            try
            {
                String signUpResult = ServerConnection.signUp(username, password);
                if (signUpResult!=null)
                {
                    String[] signUpResultStems  = signUpResult.split("\t");
                    if(signUpResult.length() == 2)
                    {
                        if(signUpResultStems[0].equals("SUCCESS"))
                        {
                            Message.obtain(loginHandler, SIGNUP_SUCCESS, signUpResultStems[1]).sendToTarget();
                        }
                        else
                        {
                            Message.obtain(loginHandler, SIGNUP_FAILURE, signUpResultStems[1]).sendToTarget();
                        }
                    }
                    else
                    {
                        Message.obtain(loginHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
                    }
                }
                else
                {
                    Message.obtain(loginHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
                }
            }
            catch(Exception e)
            {
                Message.obtain(loginHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
            }
        }
    }


    class SignInThread implements Runnable {

        @Override
        public void run()
        {

            //   Message.obtain(loginHandler, SIGNIN_SUCCESS).sendToTarget();

            // Send request to server
            DBManager dbManager = new DBManager(LoginActivity.this);
            String token = dbManager.getToken();
            dbManager.close();
            try
            {
                String signInResult = ServerConnection.signIn(token, username, password);
                if (signInResult!=null)
                {
                    if(signInResult.equals("VALID"))
                    {
                        Message.obtain(loginHandler, SIGNIN_SUCCESS).sendToTarget();
                    }
                    else
                    {
                        Message.obtain(loginHandler, SIGNIN_FAILURE, "Incorrect Password.").sendToTarget();
                    }
                }
                else
                {
                    Message.obtain(loginHandler, SIGNIN_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
                }
            }
            catch(Exception e)
            {
                Message.obtain(loginHandler, SIGNIN_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
            }
        }
    }

}