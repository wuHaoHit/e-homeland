package com.example.wuye1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class logIn extends Activity{
	private EditText phoneNumber,passWord;
	private Button login;
	private CheckBox autoLogin;
	private String tmDevice;
	private String back = "返回结果";
	private String phone,passwordMD5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in);
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    tmDevice = "" + tm.getDeviceId();
		phoneNumber = (EditText)findViewById(R.id.phoneNumberLogin);
		passWord = (EditText)findViewById(R.id.passwordLogin);
		login = (Button)findViewById(R.id.login);
		autoLogin =(CheckBox)findViewById(R.id.autoLogin);
		login.setOnClickListener(new OnClickListener(){
				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				phone = phoneNumber.getText().toString();
				passwordMD5 = getMD5Str(passWord.getText().toString());
				//发送登录信息
				new Thread(runnable).start();
				
				SharedPreferences mySharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE); 
				SharedPreferences.Editor editor = mySharedPreferences.edit(); 
				editor.putString("name", phoneNumber.getText().toString()); 
				editor.putString("password", passWord.getText().toString());
				editor.putBoolean("ifAutoLogin",autoLogin.isChecked());
				editor.commit(); 
				
			}
			
		});
	}
	
  
	
	 /** 
     * MD5 加密 
     */  
    private String getMD5Str(String str) {  
        MessageDigest messageDigest = null;  
  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
  
            messageDigest.reset();  
  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }  
    Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        //Bundle data = msg.getData();
	       // Toast.makeText(logIn.this,back,Toast.LENGTH_LONG).show();
	        if(back == "success"){
	        	//登录成功
	        }
	        else{
	        	//登录失败
	        }
	    }
	};
	 
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	        //
	        // TODO: http request.
	        //

	    	HttpClient client = new DefaultHttpClient();
			try {
				 HttpPost httppost = new HttpPost("http://192.168.1.125:3000/post");   
				 
				 JSONObject obj = new JSONObject();  
				 obj.put("name", "your name");   
				 List<NameValuePair> map = new ArrayList <NameValuePair>();
		         map.add(new BasicNameValuePair("name",phone));
		         map.add(new BasicNameValuePair("password",passwordMD5));
		         map.add(new BasicNameValuePair("deviceID",tmDevice));
				 httppost.setEntity(new UrlEncodedFormEntity(map));   
				 
				 client.execute(httppost);  
				HttpResponse response = client.execute(httppost);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 取得返回的字符串
					back = EntityUtils.toString(response.getEntity());
					System.out.println(back+"wuhao");
				
					} else {
						back = "返回失败";
						System.out.println(back);
					}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		   // post("http://192.168.1.125:3000/post",val);
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("back",back);
	        msg.setData(data);
	        handler.sendMessage(msg);
	    }
	};
	
}
