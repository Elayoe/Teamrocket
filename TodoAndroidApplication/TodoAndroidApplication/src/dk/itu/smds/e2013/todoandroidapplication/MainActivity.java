package dk.itu.smds.e2013.todoandroidapplication;

import dk.itu.smds.e2013.todoandroidapplication.RegisterActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		// Rao: 20131103: add code for registering you application for Google Endpoints.
        Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
