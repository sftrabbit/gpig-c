package uk.co.gpigc.androidapp;

import uk.co.gpigc.androidapp.comms.DataPusher;
import uk.co.gpigc.gpigcandroid.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private Button system1Button;
	private Button system2Button;
	private Button system3Button;
	private String coreIP = "192.168.44.179";

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
			    Intent intent = new Intent(MainActivity.this, CarSystemActivity.class);
			    intent.putExtra(DataPusher.CORE_IP_KEY, coreIP);
			    startActivity(intent);
			}
		});
		
		system2Button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(MainActivity.this, PhoneSystemActivity.class);
				    intent.putExtra(DataPusher.CORE_IP_KEY, coreIP);
				 startActivity(intent);
			}
		});
		system3Button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(MainActivity.this, FaceSystemActivity.class);
				    intent.putExtra(DataPusher.CORE_IP_KEY, coreIP);
				 startActivity(intent);
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showAlert();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void showAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Set Core IP");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(coreIP);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  coreIP = value;
		  }
		});

		alert.show();
	}
}
