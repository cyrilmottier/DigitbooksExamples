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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class ItemTypeActivity extends ListActivity {

    private static final int ITEM_VIEW_TYPE_VIDEO = 0;
    private static final int ITEM_VIEW_TYPE_SEPARATOR = ITEM_VIEW_TYPE_VIDEO + 1;

    private static final int ITEM_VIEW_TYPE_COUNT = ITEM_VIEW_TYPE_SEPARATOR + 1;

    private static class Video {
        public String title;
        public String description;

        public Video(String title) {
            this(title, "Aucune description disponible");
        }

        public Video(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private static final Object[] OBJECTS = {
            "Films",
            new Video("Tron Legacy", "Long-métrage américain\nGenre : Science-fiction\nRéalisé par Joseph Kosinski"),
            new Video("Harry Potter et les reliques de la mort - I",
                    "Long-métrage américain, britannique\nGenre : Fantastique\nRéalisé par David Yates"),
            new Video("Le Cinquième élément",
                    "Long-métrage français, américain\nGenre : Science-fiction\nRéalisé par Luc Besson"),
            new Video("I, Robot", "Long-métrage américain\nGenre : Science-fiction, Action\nRéalisé par Alex Proyas"),
            new Video("The Island", "Long-métrage américain\nGenre : Science-fiction, Action\nRéalisé par Michael Bay"),
            new Video("Minority Report",
                    "Long-métrage américain\nGenre : Science-fiction\nRéalisé par Steven Spielberg"),
            new Video("Bienvenue à Gattaca",
                    "Long-métrage américain\nGenre : Science-fiction\nRéalisé par Andrew Niccol"),
            new Video("Inception",
                    "Long-métrage américain, britannique\nGenre : Science fiction, Thriller\nRéalisé par Christopher Nolan"),
            "Series", new Video("Dr House (Docteur House)"), new Video("True Blood"), new Video("Smallville"),
            new Video("Sanctuary"), new Video("Desperate Housewives"), new Video("Spartacus: Blood and Sand"),
            new Video("Lost, les disparus"), new Video("Stargate Universe"), new Video("How I Met Your Mother")
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListView listView = getListView();
        /*
         * Puisque la ListView n'a pas été créée à partir d'un fichier XML, il
         * est nécessaire de modifier quelques-unes des propriétés de cette
         * dernière via le code Java.
         */
        listView.setCacheColorHint(Color.DKGRAY);
        /*
         * Modifie la couleur d'arrière plan (équivalent à
         * android:background="@android:color/white")
         */
        listView.setBackgroundColor(Color.DKGRAY);
        /*
         * Modifie le Drawable utilisé pour séparer les éléments
         */
        listView.setDivider(new ColorDrawable(Color.WHITE));
        /*
         * Un ColorDrawable n'ayant pas de taille, il est nécessaire de définir
         * la hauteur des séparations.
         */
        listView.setDividerHeight(1);

        setListAdapter(new TwoRowTypesAdapter(this));
    }

    private static class TwoRowTypesAdapter extends BaseAdapter {

        static class ViewHolder {
            TextView mTitleView;
            TextView mSubtitleView;
        }

        private final LayoutInflater mLayoutInflater;

        public TwoRowTypesAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return OBJECTS.length;
        }

        public Object getItem(int position) {
            return OBJECTS[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public int getViewTypeCount() {
            return ITEM_VIEW_TYPE_COUNT;
        }

        public int getItemViewType(int position) {
            return (OBJECTS[position] instanceof String) ? ITEM_VIEW_TYPE_SEPARATOR : ITEM_VIEW_TYPE_VIDEO;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final int type = getItemViewType(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(type == ITEM_VIEW_TYPE_SEPARATOR ? R.layout.separator_list_item
                        : R.layout.video_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mTitleView = (TextView) convertView.findViewById(R.id.title);
                viewHolder.mSubtitleView = (TextView) convertView.findViewById(R.id.subtitle);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            switch (type) {
                case ITEM_VIEW_TYPE_SEPARATOR:
                    viewHolder.mTitleView.setText((String) getItem(position));
                    break;

                case ITEM_VIEW_TYPE_VIDEO:
                    final Video video = (Video) getItem(position);
                    viewHolder.mTitleView.setText(video.title);
                    viewHolder.mSubtitleView.setText(video.description);
            }

            return convertView;
        }

    }

}
