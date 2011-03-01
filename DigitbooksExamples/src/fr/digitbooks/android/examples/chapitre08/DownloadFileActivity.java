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

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

public class DownloadFileActivity extends Activity {
	
	private long mId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(Build.VERSION.SDK_INT >= 9)
        {
        
	        final DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
	
	        // Création d'un bouton
	        Button button = new Button(this);
	        button.setText("Lancer le téléchargement");
	        button.setPadding(10, 10, 10, 10);
	
	        // Démarrage du service lors de la pression du bouton
	        button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	Uri uriUrl = Uri.parse("http://www.digitbooks.fr/images/Android-Ico.jpg");  
	            	Request request = new Request(uriUrl);
	            	request.setTitle(getString(R.string.download_title));
	            	mId = downloadManager.enqueue(request);
	            }
	        });
	
	        addContentView(button, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
        else
        {
        	setContentView(R.layout.hello_activity);
        	TextView text = (TextView) findViewById(R.id.wrap_text);
        	text.setText(R.string.version);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Création d'un filtre d'intention pour configurer le récepteur
        // d'évènement pour les évènements du download manager. 
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE );
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        // Enregistrement du filtre tant que l'activité a le focus
        registerReceiver(mIntentReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // On désactive le récepteur lorsque on perds le focus
        unregisterReceiver(mIntentReceiver);
    }

    // Création du récepteur d'évènement
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent.getAction().compareTo(DownloadManager.ACTION_DOWNLOAD_COMPLETE) == 0){
        		// Id du téléchargement qui provoque cette action
        		long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        		
        		if(mId == id){
	        		//Récupération des informations du download
	        		final DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
	        		Query query = new Query();
	        		query.setFilterById(id);
	        		Cursor cursor = downloadManager.query(query);
	        		
	        		if(cursor.moveToFirst()){
        				int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
        				int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        				int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        				int totalSizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        				
        				String localUri = cursor.getString(localUriIndex);
        				int reason = cursor.getInt(reasonIndex);
        				int status = cursor.getInt(statusIndex);
        				int totalSize = cursor.getInt(totalSizeIndex);
        				
        				Log.d("TEST", "DownloadManager = " + reason + ", " + status + ", " + totalSize + ", " + localUri);
        				Toast.makeText(context, getString(R.string.file_complete, localUri), Toast.LENGTH_LONG).show();;
	        				
	        		}
	        		
	        		
        		}
        		
        	} else if (intent.getAction().compareTo(DownloadManager.ACTION_NOTIFICATION_CLICKED) == 0) {
        		
        	} else {
        		//unknow action
        	}
        }
    };
}
