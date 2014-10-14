package com.rss.rssreader;
 
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView text = (TextView) findViewById(R.id.textAbout);
		String htmlTitle = "<h1>A Propos</h1><br />" +
				"<h5>Version BETA 1</h5>"+
				"Application dévellopée dans le cadre d'un projet encadré par :" +
				"<i>Jean-Remy Falleri</i> et <i>Mathieu Foucault</i><br />" +
				"Par: <br /><h4>>Martin LADURE</h4>";
		text.setText(Html.fromHtml(htmlTitle));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent intent = new Intent(AboutActivity.this,AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_addFlux:
			Intent intent1 = new Intent(AboutActivity.this,AddFluxActivity.class);
			startActivity(intent1);
			break;
		case R.id.menu_logout:
			SharedPreferences prefs = getSharedPreferences("com.rss.rssreader", Context.MODE_PRIVATE);
			prefs.edit().putString("login", null).commit();
			Toast.makeText(AboutActivity.this,"ByeBye !", Toast.LENGTH_SHORT).show();
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
}
