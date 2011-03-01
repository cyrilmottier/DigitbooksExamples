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
package fr.digitbooks.android.examples.chapitre05;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import fr.digitbooks.android.examples.R;

public class RessourcesAndroidActivity extends ListActivity {

    private static final int FIRST_MENU_ID = Menu.FIRST;
    private static final int SECOND_MENU_ID = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] mStrings = getResources().getStringArray(R.array.days_of_week);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mStrings));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, FIRST_MENU_ID, 0, "Add").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, SECOND_MENU_ID, 0, "Delete").setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }

}
