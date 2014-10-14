package com.rss.rssreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class StreamActivity extends Activity { 
	public final static String JOURNAL="journal";
	private LinearLayout loadedLayout;
	private FrameLayout noArticleLayout;
	private FrameLayout loadingLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stream);
		loadedLayout = (LinearLayout) findViewById(R.id.loaded);
		loadingLayout = (FrameLayout) findViewById(R.id.loading);
		noArticleLayout = (FrameLayout) findViewById(R.id.noData);
		final SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
		if (prefs.getString("login", null)== null){
			Intent intent = new Intent(StreamActivity.this,MainActivity.class);
			startActivity(intent);
		}

		/************  Static JSON data ***********/
		final ListView article = (ListView) findViewById(R.id.listView1);
		final List<String> listOfArticle= new ArrayList<String>();
		final String tableOfId[]= new String[300];
		AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/abo/"+prefs.getString("login", ""));
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
				/******** Listener for button click ********/

				JSONObject jsonResponse = null;
				/****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
				Log.d("RESULT",result);
				try {
					jsonResponse = new JSONObject(result);
				} catch (JSONException e) {
					System.out.println("FAIL");
					e.printStackTrace();
				}
				/***** Returns the value mapped by name if it exists and is a JSONArray. ***/
				/*******  Returns null otherwise.  *******/
				JSONArray jsonMainNode = null;
				int lengthJsonArr =0;  
				try{
					jsonMainNode = jsonResponse.optJSONArray("flux");
					lengthJsonArr = jsonMainNode.length();  
				} catch (Throwable t) {
					Log.e("My App", "Could not parse malformed JSON");
				}
				/*********** Process each JSON Node ************/



				for(int i=0; i < lengthJsonArr; i++) 
				{
					/****** Get Object for each JSON node.***********/
					JSONObject jsonChildNode = null;
					try {
						jsonChildNode = jsonMainNode.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					
					/******* Fetch node values **********/
					//int id_article        = Integer.parseInt(jsonChildNode.optString("id").toString());
					String idArticle   = jsonChildNode.optString("id").toString();
					String star = jsonChildNode.optString("star").toString();
					String title = jsonChildNode.optString("title").toString();
					tableOfId[i]=idArticle;
					listOfArticle.add(title);
				}
				if(lengthJsonArr==0){
					noArticleLayout.setVisibility(View.VISIBLE);
					Button boutonAdd = (Button) findViewById(R.id.addFlux);
					boutonAdd.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(StreamActivity.this,AddFluxActivity.class);
							startActivity(intent);
						}
					});
				}


				/************ Show Output on screen/activity **********/
				loadedLayout.setVisibility(View.VISIBLE);
				loadingLayout.setVisibility(View.GONE);

			}

		};

		identrequest.execute();


	






		ArrayAdapter<String> adaptater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listOfArticle);
		article.setAdapter(adaptater);

		article.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(StreamActivity.this,DisplayArticle.class);
				System.out.println("Position="+position);
				intent.putExtra("journal", tableOfId[position]);
				startActivity(intent);
			}
		});


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stream, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intent = new Intent(StreamActivity.this,AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_addFlux:
			Intent intent1 = new Intent(StreamActivity.this,AddFluxActivity.class);
			startActivity(intent1);
			break;
		case R.id.menu_logout:
			SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
			prefs.edit().putString("login", null).commit();
			Toast.makeText(StreamActivity.this,"ByeBye !", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
}
