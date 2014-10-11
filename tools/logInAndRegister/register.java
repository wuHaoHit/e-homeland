package com.example.wuye1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class register extends Activity{
	private JSONObject obj = new JSONObject(); 
	private TextView registerTV;
	private EditText userNameEdit,userPassword,phoneNumber,userPasswordConfirm;
	String val;
	private Spinner houseEstate,dong,danyuan,hao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		//获取控件
		houseEstate = (Spinner)findViewById(R.id.houseEstate);
		dong = (Spinner)findViewById(R.id.dong);
		danyuan = (Spinner)findViewById(R.id.danyuan);
		hao = (Spinner)findViewById(R.id.hao);
		userNameEdit = (EditText)findViewById(R.id.userNameEdit);
		userPassword = (EditText)findViewById(R.id.userPassword);
		userPasswordConfirm = (EditText)findViewById(R.id.userPasswordConfirm);
		phoneNumber = (EditText)findViewById(R.id.phoneNumber);
		registerTV = (TextView)findViewById(R.id.register);
		//添加下拉列表
		String []estate = {"第一小区","第二小区"};
		ArrayList<String> allEstate = new ArrayList<String>();
		for(int i = 0;i < estate.length;i++){
			allEstate.add(estate[i]);
		}
		ArrayAdapter<String> estateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allEstate);
		estateAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		houseEstate.setAdapter(estateAdapter);
		String []number = {"1","2","3"};
		ArrayList<String> allNumber = new ArrayList<String>();
		for(int i = 0;i < number.length;i++){
			allNumber.add(number[i]);
		}
		ArrayAdapter<String> numberAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,allNumber);
		numberAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		dong.setAdapter(numberAdapter);
		danyuan.setAdapter(numberAdapter);
		hao.setAdapter(numberAdapter);
		//添加响应事件
		registerTV.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String passWord,passWordConfirm,name,phone;
				name = userNameEdit.getText().toString();
				passWord = userPassword.getText().toString();
				passWordConfirm = userPasswordConfirm.getText().toString();
				phone = phoneNumber.getText().toString();
				System.out.println(passWord+"password");
				System.out.println(passWordConfirm+"passwordconfirm");
				if(passWord.equals(passWordConfirm)){
					Toast.makeText(register.this,"密码两次一致",Toast.LENGTH_LONG).show();
					try {
						obj.put("name", userNameEdit.getText().toString());
						obj.put("password", getMD5Str(userPassword.getText().toString()));
						obj.put("estate", houseEstate.getSelectedItem().toString());
						obj.put("phone", phoneNumber.getText().toString());
						obj.put("address", dong.getSelectedItem().toString()+"-"+danyuan.getSelectedItem().toString()+"-"
																+hao.getSelectedItem().toString());
						System.out.println(obj.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						System.out.println(e.toString());
					}   
					new Thread(runnable).start();
				}else{
					Toast.makeText(register.this,"密码两次输入不一致",Toast.LENGTH_LONG).show();
				}	
				
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
	        Bundle data = msg.getData();
	        val = data.getString("value");
	        
	       // System.out.println("hello"+val);
	        //Log.i("mylog","请求结果-->" + val);
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
				 StringEntity s = new StringEntity(obj.toString());    
				 s.setContentEncoding("UTF-8");    
			     s.setContentType("application/json");    
				 httppost.setEntity(s);   
				 client.execute(httppost);
				 //response
				HttpResponse response = client.execute(httppost);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 取得返回的字符串
					val = EntityUtils.toString(response.getEntity());
					Toast.makeText(register.this,val,Toast.LENGTH_LONG).show();
					System.out.println(val+"wuhao");
					} else {
						
					}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("value",val);
	        msg.setData(data);
	        handler.sendMessage(msg);
	    }
	};
	
}
