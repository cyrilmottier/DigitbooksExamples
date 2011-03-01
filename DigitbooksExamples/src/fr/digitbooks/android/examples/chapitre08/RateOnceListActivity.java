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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;

public class RateOnceListActivity extends RateListActivity {
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case FIRST_MENU_ID:
                Intent intent = new Intent(this, RateDigitbooksActivity.class);
                intent.putExtra(RateDigitbooksActivity.RATE_ONCE, true);
                startActivityForResult(intent, ADD_RATING);
                return true;
        }

        return false;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences preferences = getSharedPreferences(Config.PREFERENCES, Context.MODE_PRIVATE);
		boolean rateSent = preferences.getBoolean(Config.PREFERENCE_RATE_SENT, false);
		if(rateSent && Config.USE_APPLICATIONS_PREFERENCES)
			menu.findItem(FIRST_MENU_ID)
			.setTitle(R.string.see_rate)
			.setIcon(android.R.drawable.ic_menu_view);
		return super.onPrepareOptionsMenu(menu);
	}
}
