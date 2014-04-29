package uk.co.gpigc.androidapp;

import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button system1Button;
	private Button system2Button;
	private Button system3Button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		system1Button = (Button) findViewById(R.id.system1);
		system2Button = (Button) findViewById(R.id.system2);
		system3Button = (Button) findViewById(R.id.system3);	
		
		system1Button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
			    Intent intent = new Intent(MainActivity.this, System1Activity.class);
			    startActivity(intent);
			}
		});
		
		system2Button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				 //Intent intent = new Intent(MainActivity.this, Sensor2Activity.class);
				 //startActivity(intent);
			}
		});
		system3Button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(MainActivity.this, Sensor3Activity.class);
				 //startActivity(intent);
			}
		});
	}
}
