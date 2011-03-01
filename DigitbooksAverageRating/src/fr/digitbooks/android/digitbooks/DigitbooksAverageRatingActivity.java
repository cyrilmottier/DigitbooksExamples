package fr.digitbooks.android.digitbooks;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DigitbooksAverageRatingActivity extends Activity {

	private static final boolean DEBUG_LOGS_FILE_ENABLED = false;
	private static final String LOG_TAG = DigitbooksAverageRatingActivity.class.getSimpleName();

	private static final String AUTHORITY = "fr.digitbooks.android.examples";
	public static final Uri CONTENT_URI =
		Uri.parse("content://" + AUTHORITY);

	private static final String[] PROJECTION = {
		"_id",
		"rating"
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		
	}

	@Override
	protected void onResume() {
		final RatingBar r = (RatingBar)findViewById(R.id.ratingBar);
		final TextView t = (TextView)findViewById(R.id.notice);
		final Button b = (Button)findViewById(R.id.button);
		r.setVisibility(View.VISIBLE);
		b.setVisibility(View.GONE);

		final Uri uri = new Uri.Builder()
		.scheme(android.content.ContentResolver.SCHEME_CONTENT)
		.authority(AUTHORITY)
		.path("rates")
		.build();

		boolean provider_empty_or_null = false;
		try{
			Cursor cursor = getContentResolver().query(uri, PROJECTION, null, null, null);
			if(cursor!= null)
			{
				
				if(cursor.getCount() == 0){
					provider_empty_or_null = true;
					Log.i(LOG_TAG, "empty");
				}
				else
				{
					float countRates = 0;
					float sumRates = 0;
					int indexColumn  = cursor.getColumnIndex("rating");
					while(cursor.moveToNext())
					{
						sumRates += cursor.getFloat(indexColumn);
						countRates+= 1.0f;
					}
					Log.i(LOG_TAG, "count rates = " + countRates);
					Log.i(LOG_TAG, "sum of rates = " + sumRates);
					Log.i(LOG_TAG, "average = " + sumRates / countRates);
	
					r.setRating(sumRates / countRates);
					r.setEnabled(false);
					r.setFocusable(false);
				}
				cursor.close();	
			}
			else
			{
				provider_empty_or_null = true;
			}
			
			if(provider_empty_or_null)
			{
				r.setVisibility(View.GONE);
				t.setText(R.string.provider_empty);
				b.setVisibility(View.VISIBLE);
				b.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						try{
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setType("vnd.android.cursor.dir/vnd.digitbooks.rate");
							startActivity(i);
						}
						catch(ActivityNotFoundException e)
						{
							Toast.makeText(DigitbooksAverageRatingActivity.this, R.string.digitbooks_examples_not_found, Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}
		catch(SecurityException e)
		{
			r.setVisibility(View.GONE);
			t.setText(R.string.permission_problem);
		}
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		super.onResume();
	}
	
	
}