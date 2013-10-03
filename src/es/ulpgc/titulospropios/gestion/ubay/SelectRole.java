package es.ulpgc.titulospropios.gestion.ubay;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SelectRole extends Activity {

	private String ROLE = "manager";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_role_activity);

		if (getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		Button button_teacher = (Button) findViewById(R.id.button_teacher);
		Button button_manager = (Button) findViewById(R.id.button_manager);

		button_teacher.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(), LoginUser.class);
				intent.putExtra(ROLE, false);
				startActivity(intent);
			}
		});
		button_manager.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(), LoginUser.class);
				intent.putExtra(ROLE, true);
				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_role, menu);
		return true;
	}

}
