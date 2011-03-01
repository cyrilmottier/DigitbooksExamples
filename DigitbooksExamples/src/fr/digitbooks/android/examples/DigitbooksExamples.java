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
package fr.digitbooks.android.examples;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fr.digitbooks.android.examples.util.Config;

public class DigitbooksExamples extends Activity implements OnItemClickListener {

    private static final String LOG_TAG = DigitbooksExamples.class.getSimpleName();

    private static final String CATEGORY_SAMPLE_CODE = "digitbooks.intent.category.SAMPLE_CODE";
    private static final String INTENT_KEY_PATH = "fr.digitbooks.android.examples.Path";

    private static final String PREFERENCE_NAME = "digitBooksPreferences";
    private static final String PREFERENCE_INFO_SHOWN = "infoShown";

    private boolean mIsMainScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = false;

        if (!prefs.contains(PREFERENCE_INFO_SHOWN)) {

            final Editor editor = prefs.edit();
            editor.putBoolean(PREFERENCE_INFO_SHOWN, true);
            editor.commit();

            isFirstLaunch = true;
        }

        if (isFirstLaunch) {
            setContentView(R.layout.about);
        } else {

            setContentView(R.layout.main);

            String path = getIntent().getStringExtra(INTENT_KEY_PATH);
            if (path == null) {
                if (Config.INFO_LOGS_ENABLED) {
                    Log.i(LOG_TAG, "No value found for key '" + INTENT_KEY_PATH + "'");
                }
                path = "";
                findViewById(R.id.bookLink).setVisibility(View.VISIBLE);
            }

            mIsMainScreen = (path == null || path.length() == 0);

            ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setAdapter(new SimpleAdapter(this, getData(path), android.R.layout.simple_list_item_1,
                    new String[] {
                        "title"
                    }, new int[] {
                        android.R.id.text1
                    }));
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mIsMainScreen;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onValidate(View v) {
        startActivity(new Intent(this, DigitbooksExamples.class));
        finish();
    }

    public void onBuy(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.digitbooks.fr/catalogue/9782815002028.html")));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        Class<?> klass = null;
        switch (item.getItemId()) {
            case R.id.main_menu_licence:
                klass = LicenseActivity.class;
                break;
            case R.id.main_menu_about:
                klass = AboutActivity.class;
                break;
        }

        if (klass != null) {
            startActivity(new Intent(this, klass));
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    protected List<Map<String, Object>> getData(String prefix) {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_SAMPLE_CODE);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        String[] prefixPath;

        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;

            if (prefix.length() == 0 || label.startsWith(prefix)) {

                String[] labelPath = label.split("/");

                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData, nextLabel,
                            activityIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/"
                                + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);

        return myData;
    }

    private final static Comparator<Map<String, Object>> sDisplayNameComparator = new Comparator<Map<String, Object>>() {
        private final Collator collator = Collator.getInstance();

        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, DigitbooksExamples.class);
        result.putExtra(INTENT_KEY_PATH, path);
        return result;
    }

    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    @SuppressWarnings("unchecked")
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

}
