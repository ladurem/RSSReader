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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayArticle extends Activity {
	private RelativeLayout loadedLayout;
	private FrameLayout loadingLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_article);
		Intent intent = getIntent();
		loadedLayout = (RelativeLayout) findViewById(R.id.loaded);
		loadingLayout = (FrameLayout) findViewById(R.id.loading);
		final String idArticle=intent.getStringExtra(StreamActivity.JOURNAL);
		final ImageView  starButton= (ImageView)findViewById(R.id.ImageView1);

		/************  Static JSON data ***********/

		final ListView article = (ListView) findViewById(R.id.listView1);
		final List<String> listOfArticle= new ArrayList<String>();
		JSONObject jsonResponse = null;
		AsyncTask<Void, Void, String> readrequest  = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/read/"+idArticle);
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
			}

		};

		readrequest.execute();



		AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/getarticle/"+idArticle);
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


				if(lengthJsonArr ==1 ){
					for(int i=0; i < lengthJsonArr; i++) 
					{
						/****** Get Object for each JSON node.***********/
						JSONObject jsonChildNode = null;
						try {
							jsonChildNode = jsonMainNode.getJSONObject(i);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String date   = "";
						String star = "";
						String read = "";
						String title = "";
						/******* Fetch node values **********/
						//int id_article        = Integer.parseInt(jsonChildNode.optString("id").toString());
						date  = jsonChildNode.optString("date").toString();
						star = jsonChildNode.optString("star").toString();
						read = jsonChildNode.optString("read").toString();
						title = jsonChildNode.optString("title").toString();
						final String url = jsonChildNode.optString("url").toString();
						String content = jsonChildNode.optString("content").toString();
						//Update logo when stared
						System.out.println("Star = "+star);
						if(star.toString().contentEquals("0")){
							starButton.setImageResource(R.drawable.ic_unstar);
							System.out.println("STARED");
						} 

						Log.d("INSERT","I="+i+"=>"+idArticle);
						TextView text = (TextView) findViewById(R.id.titleContent);
						String htmlTitle = "<h1>"+title+"</h1>";

						text.setText(Html.fromHtml(htmlTitle));
						TextView articleContent = (TextView) findViewById(R.id.articleContent);
						String htmlContent = "<small>"+date+"</small><br /><p>"+content+"</p>";
						articleContent.setText(Html.fromHtml(htmlContent));
						if(star.toString().contentEquals("1")){

						}
						ImageView urlButton = (ImageView) findViewById(R.id.buttonUrl);
						urlButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								String urlToSee = url;
								Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( urlToSee ) );
								startActivity(intent);

							}
						});
						starButton .setOnClickListener(new OnClickListener(){
							boolean alreadyrated = false;
							@Override
							public void onClick(View v)	{

								if(alreadyrated){
									AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

										@Override
										protected String doInBackground(Void... arg0) {
											HttpClient client = new DefaultHttpClient();
											HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/unstar/"+idArticle);
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
											starButton.setImageResource(R.drawable.ic_unstar);
											Toast.makeText(getApplicationContext(), "unStared", Toast.LENGTH_SHORT).show();
											alreadyrated=false;
										}

									};

									identrequest.execute();

								}
								else
								{
									AsyncTask<Void, Void, String> identrequest  = new AsyncTask<Void, Void, String>() {

										@Override
										protected String doInBackground(Void... arg0) {
											HttpClient client = new DefaultHttpClient();
											HttpGet request = new HttpGet("http://mladure.rmorpheus.enseirb.fr/FluxRSS/appli/star/"+idArticle);
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
											Toast.makeText(getApplicationContext(), "Stared", Toast.LENGTH_SHORT).show();
											alreadyrated=true;       
											starButton.setImageResource(R.drawable.ic_star);
										}

									};


									identrequest.execute();
								}
							}
						});



					}
				} else {
					Toast.makeText(DisplayArticle.this,"Fail!", Toast.LENGTH_SHORT).show();
				}

				/************ Show Output on screen/activity **********/

				loadedLayout.setVisibility(View.VISIBLE);
				loadingLayout.setVisibility(View.GONE);
			}

		};

		identrequest.execute();





	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_article, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intent = new Intent(DisplayArticle.this,AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_addFlux:
			Intent intent1 = new Intent(DisplayArticle.this,AddFluxActivity.class);
			startActivity(intent1);
			break;
		case R.id.menu_logout:
			SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
			prefs.edit().putString("login", null).commit();
			Toast.makeText(DisplayArticle.this,"ByeBye !", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
}
