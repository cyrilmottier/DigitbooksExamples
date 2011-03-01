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
package fr.digitbooks.android.examples.chapitre10;

import android.os.AsyncTask;
import android.util.Log;

public class PiCalculatingNdkTask extends AsyncTask<Integer, Void, String> {

	private static final String LOG_TAG = PiCalculatingNdkTask.class.getSimpleName();

	PiCalculatingListener mListener;
	
	private PiCalculatingNdkTask(){
	}
	
	public PiCalculatingNdkTask(PiCalculatingListener listener)
	{
		this.mListener=listener;
	}
	
	@Override
	protected void onPreExecute() {
		mListener.onCalculatingStart();
	}

	@Override
	protected String doInBackground(Integer... params) {
		int limit = params[0];
		int BASE=1000000;
		if(limit > 1500) {
			BASE=100000;
		}
		int[] array = calculatePi(limit);
		int a;
		int BS10 = BASE/10;
		StringBuffer result = new StringBuffer(array[0] + ".");
		for(int i=1;i<array.length-1;++i) {
			a = array[i];
			a = a == 0 ? 1 : a;
			while(a < BS10) {
				result.append("0");
				a=10*a;
			}
			result.append(String.format("%d%s", array[i], " "));
		}
		Log.i(LOG_TAG, "Start Ndk method " + result.toString());
		return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		mListener.onCalculatingEnd(result);
	}

	/* La mŽthode native implŽmentŽ par 
	 * la librairie pi
	 */
	public native int[]  calculatePi(int decimalNumber);


	/* permet de dŽclarer le chargement de la librairie
	 * au dŽmarage de l'application
	 * /data/data/fr.digitbooks.android/lib/libpi.so 
	 */
	static {
		System.loadLibrary("pi");
	}

}
