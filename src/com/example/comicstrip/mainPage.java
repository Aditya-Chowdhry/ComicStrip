package com.example.comicstrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class mainPage extends Activity {

	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
	}
	public void startButton(View v)
	{
		intent=new Intent(this,com.example.comicstrip.TemplatePage.class);
	     startActivity(intent);
	 	
	}
}
