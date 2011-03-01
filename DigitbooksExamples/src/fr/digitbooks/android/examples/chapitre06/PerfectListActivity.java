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
package fr.digitbooks.android.examples.chapitre06;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class PerfectListActivity extends ListActivity {

    private static final int NUMBER_OF_ROWS = 10000;
    private static final String FAKE_TITLE_PREFIX = "Ceci est le titre #";
    private static final String FAKE_SUBTITLE_PREFIX = "Sous-titre de la cellule #";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new PerfectAdapter(this));
    }

    private static class PerfectAdapter extends BaseAdapter {

        static class ViewHolder {
            TextView mTitleView;
            TextView mSubtitleView;
        }

        private LayoutInflater mLayoutInflater;

        public PerfectAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return NUMBER_OF_ROWS;
        }

        public Object getItem(int position) {
            // Méthode non utilisée
            return null;
        }

        public long getItemId(int position) {
            // Méthode non utilisée
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            /*
             * On regarde si la convertView est nulle. Si c'est le cas, on créé
             * une nouvelle ligne et on conserve une référence sur les vues
             * filles.
             */
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.subtitle_list_item, parent, false);
                holder = new ViewHolder();
                holder.mTitleView = (TextView) convertView.findViewById(R.id.subtitle_item_title);
                holder.mSubtitleView = (TextView) convertView.findViewById(R.id.subtitle_item_subtitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            /*
             * Cette solution permet de réutiliser les vues et donc de minimiser
             * l'allocation d'objets. Néanmoins, les lignes ci dessous provoque
             * encore des allocations ! En effet, la concaténation de chaîne
             * (avec le +) utilise en fait un nouveau StringBuilder.
             */
            holder.mTitleView.setText(FAKE_TITLE_PREFIX + position);
            holder.mSubtitleView.setText(FAKE_SUBTITLE_PREFIX + position);

            return convertView;
        }

    }
}
