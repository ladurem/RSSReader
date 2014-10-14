package com.rss.rssreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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

public class AddFluxActivity extends Activity {
	private final String USER_AGENT = "Mozilla/5.0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_flux);
		final EditText urlToAddText = (EditText) findViewById(R.id.urlToAdd);
		final SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
		TextView text = (TextView) findViewById(R.id.textView1);
		String htmlTitle = "<h1>Ajout:</h1> Veuiller indiquer l'URL du flux à ajouter : <br />";
		text.setText(Html.fromHtml(htmlTitle));
		Button submit = (Button) findViewById(R.id.Submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final String urlToAdd = urlToAddText.getText().toString();
				if(urlToAdd.equals("")){
					urlToAddText.setError("Completez le champ");
				} else {


					AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

						@Override
						protected String doInBackground(Void... arg0) {

							String url = "http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/add/";

							HttpClient client = new DefaultHttpClient();
							HttpPost post = new HttpPost(url);

							// add header
							post.setHeader("User-Agent", USER_AGENT);

							List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
							urlParameters.add(new BasicNameValuePair("login",prefs.getString("login","")));
							urlParameters.add(new BasicNameValuePair("url", urlToAdd));
							HttpResponse response = null;
							try {
								post.setEntity(new UrlEncodedFormEntity(urlParameters));
								response = client.execute(post);
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							BufferedReader rd = null;
							try {
								rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
							} catch (IllegalStateException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							StringBuffer result = new StringBuffer();
							String line = "";
							try {
								while ((line = rd.readLine()) != null) {
									result.append(line);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}




							return result.toString();
						}
						@Override
						protected void onPostExecute(String result) {
							super.onPostExecute(result);
							Toast.makeText(AddFluxActivity.this,result, Toast.LENGTH_SHORT).show();
							Log.d("RESULT",result);
							if(result.contentEquals("OK")){
								Toast.makeText(AddFluxActivity.this,"Flux Ajouté !", Toast.LENGTH_SHORT).show();

								Intent intent = new Intent(AddFluxActivity.this,StreamActivity.class);
								startActivity(intent);
							} else {
								Toast.makeText(AddFluxActivity.this,"Fail!", Toast.LENGTH_SHORT).show();
							}


						}

					};

					identrequest.execute();
				}
			}
		});




	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_flux, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intent = new Intent(AddFluxActivity.this,AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_addFlux:
			Intent intent1 = new Intent(AddFluxActivity.this,AddFluxActivity.class);
			startActivity(intent1);
			break;
		case R.id.menu_logout:
			SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
			prefs.edit().putString("login", null).commit();
			Toast.makeText(AddFluxActivity.this,"ByeBye !", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
}
