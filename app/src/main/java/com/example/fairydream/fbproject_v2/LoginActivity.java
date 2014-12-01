package com.example.fairydream.fbproject_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via password.

 */
public class LoginActivity extends Activity {
    private Button status;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private String username;
    private String password;

    private SignInHandler signInHandler;
    private SignUpHandler signUpHandler;

    private final int SIGNUP_SUCCESS = 1;
    private final int SIGNUP_FAILURE = 0;
    private final int SIGNIN_SUCCESS = 1;
    private final int SIGNIN_FAILURE = 0;
    private final int INTERNET_NOT_CONNECT = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DBManager dbManager = new DBManager(this);
        if(!dbManager.isUserRegister())
        {
            // if it's the first time to launch this permission manager
            setContentView(R.layout.activity_signup);
            status = (Button) findViewById(R.id.sign_up_button);
            usernameEditText = (EditText) findViewById(R.id.username_signUpText);
            passwordEditText = (EditText) findViewById(R.id.password_signUpText);
            signUpHandler = new SignUpHandler();
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
            setContentView(R.layout.activity_signin);
            signInHandler = new SignInHandler();
            status = (Button) findViewById(R.id.sign_in_button);
            passwordEditText = (EditText) findViewById(R.id.password_signInText);
            status.setOnClickListener(new Button.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    password = passwordEditText.getText().toString();
                    SignInThread signInThread = new SignInThread();
                    new Thread(signInThread).start();
                }

            });
        }
        dbManager.close();
    }

    class SignUpHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SIGNUP_SUCCESS:
                    DBManager dbManager = new DBManager(LoginActivity.this);
                    dbManager.addUserToken(username,msg.obj.toString());
                    dbManager.close();
                    Intent intent2 = new Intent();
                    intent2.setClass(LoginActivity.this, AppManagerActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    break;
                case SIGNUP_FAILURE:
                    status.setText("Sign up failed." + msg.obj.toString());
                    break;
                case INTERNET_NOT_CONNECT:
                    status.setText("Sign up failed. Please Check Your Internet Connection.");
                    break;
            }
        }
    }


    class SignInHandler extends Handler
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
                    status.setText("Sign in failed. Incorrect password");
                    break;
                case INTERNET_NOT_CONNECT:
                    status.setText("Sign in failed. Please Check Your Internet Connection.");
                    break;
            }
        }
    }


    class SignUpThread implements Runnable
    {

        @Override
        public void run()
        {

    //        Message.obtain(signUpHandler, SIGNUP_SUCCESS, "weeweew").sendToTarget();

            try
            {
                String signUpResult = ServerConnection.signUp(username, password);
                if (signUpResult!=null)
                {
                    String[] signUpResultStems  = signUpResult.split("\t");
                    if(signUpResultStems.length == 2)
                    {
                        if(signUpResultStems[0].equals("SUCCESS"))
                        {
                            Message.obtain(signUpHandler, SIGNUP_SUCCESS, signUpResultStems[1].trim()).sendToTarget();
                        }
                        else
                        {
                            Message.obtain(signUpHandler, SIGNUP_FAILURE, signUpResultStems[1]).sendToTarget();
                        }
                    }
                    else
                    {
                        Message.obtain(signUpHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
                    }
                }
                else
                {
                    Message.obtain(signUpHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Message.obtain(signUpHandler, SIGNUP_FAILURE, "Please Check Your Internet Connection.").sendToTarget();
            }
        }
    }


    class SignInThread implements Runnable {

        @Override
        public void run()
        {

    //      Message.obtain(signInHandler, SIGNIN_SUCCESS).sendToTarget();

            // Send request to server
            DBManager dbManager = new DBManager(LoginActivity.this);
            String token = dbManager.getToken();
            String usernameStored = dbManager.getUsername();
            dbManager.close();
            try
            {
                int signInResult = ServerConnection.signIn(token, usernameStored, password);
                Message.obtain(signInHandler, signInResult).sendToTarget();
            }
            catch(Exception e)
            {
                Message.obtain(signInHandler, INTERNET_NOT_CONNECT).sendToTarget();
            }
        }
    }

}