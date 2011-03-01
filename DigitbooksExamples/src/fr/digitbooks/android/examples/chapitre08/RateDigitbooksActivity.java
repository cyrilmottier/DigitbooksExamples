/*
 * Copyright (C) 2010   Cyril Mottier & Ludovic Perrier
 *              (http://www.digitbooks.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.digitbooks.android.examples.chapitre08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.chapitre03.RateAndroidActivity;
import fr.digitbooks.android.examples.util.Config;
import fr.digitbooks.android.examples.util.Helpers;

public class RateDigitbooksActivity extends RateAndroidActivity implements OnClickListener {

	public static final String STATE_RATING = "fr.digibooks.android.rating";
	public static final String STATE_COMMENT = "fr.digibooks.android.comment";
	public static final String PREFERENCE_RATE_SENT = "rate_sent";
	public static final String PREFERENCE_RATING = "rating";
	public static final String PREFERENCE_COMMENT = "comment";

	private final static int DIALOG_RATE_NULL = 1;
	private final static int DIALOG_PROGRESS = 2;
	public static final String RATE_ONCE = "fr.digibooks.android.rate_once";

	private boolean mRateOnce = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Changer le texte pour voter pour le livre digitbooks
		((TextView) findViewById(R.id.notice)).setText(R.string.leave_your_digitbooks_comment);

		// On vérifie que l'on peut voter plusieurs fois
		mRateOnce = getIntent().getBooleanExtra(RATE_ONCE, false);
		if(mRateOnce){
			boolean restoreBackup = false;

			// Mode préférence local à l'activité
			SharedPreferences localPreferences = getPreferences(Context.MODE_PRIVATE);
			boolean rateSent = localPreferences.getBoolean(PREFERENCE_RATE_SENT, false);
			if(rateSent){
				String comment = localPreferences.getString(PREFERENCE_COMMENT, "");
				float rating = localPreferences.getFloat(PREFERENCE_RATING, 0.0f);
				mRatingBar.setRating(rating);
				mEditText.setText(comment);
				restoreBackup = true;
			}
			
			// Ce mode écrase le précédent
			// Mode data backup; les préférences sont accessible à toute l'application
			// pour être vue par le backup agent
			if(Config.USE_APPLICATIONS_PREFERENCES){
				SharedPreferences preferences = getSharedPreferences(Config.PREFERENCES, Context.MODE_PRIVATE);
				rateSent = preferences.getBoolean(Config.PREFERENCE_RATE_SENT, false);
				if(rateSent){
					String comment = preferences.getString(Config.PREFERENCE_COMMENT, "");
					float rating = preferences.getFloat(Config.PREFERENCE_RATING, 0.0f);
					mRatingBar.setRating(rating);
					mEditText.setText(comment);
					restoreBackup = true;
				}
			}
			
			if(restoreBackup)
			{
				mRatingBar.setEnabled(false);
				mRatingBar.setFocusable(false);
				mEditText.setEnabled(false);
				mEditText.setFocusable(false);
				mButton.setEnabled(false);
				mButton.setFocusable(false);
				((TextView) findViewById(R.id.notice)).setText(R.string.rate_sent);
			}
		}

		mButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == mButton) {
			// L'utilisateur vient de valider son commentaire.
			if (mRatingBar.getRating() == 0.0f) {
				showDialog(DIALOG_RATE_NULL);
			} else {
				rateDigitbooks();
			}

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_RATE_NULL: {
			return new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_null_title).setMessage(getString(R.string.dialog_null_comment))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					rateDigitbooks();
					dismissDialog(DIALOG_RATE_NULL);
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(DIALOG_RATE_NULL);
				}
			}).create();
		}
		case DIALOG_PROGRESS: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.send_comment));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			return dialog;
		}
		}
		return null;
	}

	private void rateDigitbooks() {
		final String comment = mEditText.getText().toString();
		final float rate = mRatingBar.getRating();
		if (!Helpers.isNetworkAvailable(this)) {
			Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_LONG).show();
		} else {
			new RateTask().execute(comment, Float.toString(rate));
		}
	}

	private class RateTask extends AsyncTask<String, Void, String> {

		private float rating;
		private String comment;

		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_PROGRESS);
		}

		@Override
		protected String doInBackground(String... params) {
			rating = Float.parseFloat(params[1]);
			comment = params[0];
			String result = null;
			StringBuffer stringBuffer = new StringBuffer("");
			BufferedReader bufferedReader = null;
			try {
				//Création d'une requête de type POST
				HttpPost httpPost = new HttpPost(Config.URL_SERVER + "add");
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				parameters.add(new BasicNameValuePair("comment", params[0]));
				parameters.add(new BasicNameValuePair("rating", params[1]));
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
				httpPost.setEntity(formEntity);
				//Création d'une connexion
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");
				HttpResponse httpResponse = httpClient.execute(httpPost);
				//Traitement de la réponse
				InputStream inputStream = httpResponse.getEntity().getContent();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024);
				String readLine = bufferedReader.readLine();
				while (readLine != null) {
					stringBuffer.append(readLine);
					readLine = bufferedReader.readLine();
				}
				httpClient.close();
			} catch (Exception e) {
				return null;
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						return null;
					}
				}

				result = stringBuffer.toString();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result != null  && mRateOnce){

				if(Config.USE_APPLICATIONS_PREFERENCES){
					// Préférence partagé entre activité pour interdire l'affichage du menu pour afficher une note
					SharedPreferences preferences = getSharedPreferences(Config.PREFERENCES, Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putBoolean(Config.PREFERENCE_RATE_SENT, true);
					editor.putString(Config.PREFERENCE_COMMENT, comment);
					editor.putFloat(Config.PREFERENCE_RATING, rating);
					editor.commit();

					BackupManager backupManager = new BackupManager(RateDigitbooksActivity.this);
					backupManager.dataChanged();
				}


				// Préférence privée de l'activité pour éviter l'envois de plusieus notes
				SharedPreferences localPreferences = getPreferences(Context.MODE_PRIVATE);
				Editor localEditor = localPreferences.edit();
				localEditor.putBoolean(PREFERENCE_RATE_SENT, true);
				localEditor.putString(PREFERENCE_COMMENT, comment);
				localEditor.putFloat(PREFERENCE_RATING, rating);
				localEditor.commit();


			}

			dismissDialog(DIALOG_PROGRESS);
			setResult(RESULT_OK);
			finish();
		}
	}

}
