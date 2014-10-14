package com.rss.rssreader;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button next;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final EditText loginEditText = (EditText) findViewById(R.id.loginEditText);
		final EditText passEditText = (EditText) findViewById(R.id.password);
		TextView text = (TextView) findViewById(R.id.textlogin);
		String htmlTitle = "<h1>Identification:</h1><br />";
		text.setText(Html.fromHtml(htmlTitle));
		next = (Button) findViewById(R.id.connexion);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String loginSend = loginEditText.getText().toString();
				final String passSend = passEditText.getText().toString();


				AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground(Void... arg0) {
						HttpClient client = new DefaultHttpClient();
						HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/user/connect/"+loginSend+"/"+passSend);
						HttpResponse response = null;
						String result = null;
						try {
							response = client.execute(request);
							result = EntityUtils.toString(response.getEntity());
						} catch (ClientProtocolException e) {
							e.printStackTrace(); 
						} catch (IOException e) {
							e.printStackTrace();
						}
						return result;
					}
					@Override
					protected void onPostExecute(String result) {
						super.onPostExecute(result);
						Log.d("RETURn",result.toString());
						if(result.toString().contentEquals("OK")){
							Toast.makeText(MainActivity.this,"Connexion OK!", Toast.LENGTH_SHORT).show();

							SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
							prefs.edit().putString("login", loginSend).commit();
							prefs.edit().putString("password", passSend).commit();

							Intent intent = new Intent(MainActivity.this,StreamActivity.class);
							startActivity(intent);
						} else {
							Toast.makeText(MainActivity.this,"Fail!", Toast.LENGTH_SHORT).show();
						}


					}

				};

				identrequest.execute();
			}
		});


	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addUser:
			Intent intent = new Intent(MainActivity.this,LoginActivity.class);
			startActivity(intent);
			System.err.println("jh");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
