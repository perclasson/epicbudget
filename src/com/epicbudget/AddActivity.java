package com.epicbudget;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

public class AddActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
	    TabHost tabs = (TabHost)findViewById(R.id.tabhost);
	    tabs.setup();

	    // Calculator
	    TabHost.TabSpec calculatorTab = tabs.newTabSpec("calculator");
	    calculatorTab.setContent(R.id.tab1);
	    calculatorTab.setIndicator("Calculator");
	    tabs.addTab(calculatorTab);

	    // Home
	    TabHost.TabSpec homeTab = tabs.newTabSpec("home");
	    homeTab.setContent(R.id.tab2);
	    tabs.addTab(homeTab);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

}
