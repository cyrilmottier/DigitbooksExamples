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

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;
import fr.digitbooks.android.examples.util.RandomUtil;

public class AsynchronousListActivity extends ListActivity implements OnScrollListener {

    private static final String LOG_TAG = AsynchronousListActivity.class.getSimpleName();

    private static final int BACKGROUND_TASK_MIN_DURATION = 500;
    private static final int BACKGROUND_TASK_MAX_DURATION = 2000;

    private static final String FAKE_TITLE_PREFIX = "Ceci est le titre #";
    private static final String FAKE_SUBTITLE_PREFIX = "Sous-titre de la cellule #";

    private static final int NUMBER_OF_ROWS = 10000;
    /*
     * Faites en sorte de conserver un nombre peu élevé. L'utilisation d'un trop
     * grand nombre de Thread pourrait ralentir fortement le terminal et donc
     * gâcher l'expérience utilisateur.
     */
    private static final int NUMBER_OF_THREADS = 3;

    private static final int FETCH_IMAGE_MESSAGE = 0xcafe;

    private final Handler mHandler = new ImageFetchHandler();

    private ExecutorService mPoolExecutors;
    /**
     * Un SparseArray est une sorte de HashMap dont les clefs sont des int. Cet
     * objet nous servira de cache.
     */
    private final SparseArray<SoftReference<Bitmap>> mSoftCache = new SparseArray<SoftReference<Bitmap>>();

    /**
     * Ce tableau permet de conserver des références sur les ImageViews ne
     * disposant pas encore de l'image finale.
     */
    private final LinkedList<ImageView> mMissingImages = new LinkedList<ImageView>();

    private int mScrollState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new AsyncAdapter());
        getListView().setOnScrollListener(this);
    }

    static class ViewHolder {

        TextView mTitleView;
        StringBuilder mTitleBuilder;
        TextView mSubtitleView;
        StringBuilder mSubtitleBuilder;

        ImageView mThumbnailView;
    }

    private class AsyncAdapter extends BaseAdapter {

        private Drawable mDefaultDrawable;

        public AsyncAdapter() {
            /*
             * On charge ici le Drawable par défaut pour gagner du temps.
             */
            mDefaultDrawable = getResources().getDrawable(R.drawable.default_avatar);
        }

        public int getCount() {
            return NUMBER_OF_ROWS;
        }

        public Object getItem(int position) {
            // Méthode non utilisée
            return null;
        }

        public long getItemId(int position) {
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
                convertView = getLayoutInflater().inflate(R.layout.thumbnail_list_item, parent, false);

                holder = new ViewHolder();
                holder.mThumbnailView = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.mTitleView = (TextView) convertView.findViewById(R.id.title);
                holder.mTitleBuilder = new StringBuilder();
                holder.mSubtitleView = (TextView) convertView.findViewById(R.id.subtitle);
                holder.mSubtitleBuilder = new StringBuilder();

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bitmap bitmap = null;
            final int imageId = (int) getItemId(position);

            // Cherche une image pour la position courante dans le cache.
            synchronized (mSoftCache) {
                SoftReference<Bitmap> ref = mSoftCache.get(position);
                if (ref != null) {
                    // Une SoftReference a été trouvée dans le cache. Si cette
                    // dernière ne pointe plus sur un Bitmap, on la supprime
                    bitmap = ref.get();
                    if (bitmap == null) {
                        mSoftCache.delete(position);
                    }
                }
            }

            final ImageView thumbnail = holder.mThumbnailView;
            if (bitmap != null) {
                thumbnail.setImageBitmap(bitmap);
            } else {

                if (Config.INFO_LOGS_ENABLED) {
                    Log.i(LOG_TAG, "L'image d'id '" + imageId + "' est manquante");
                }

                mMissingImages.remove(thumbnail);

                // L'image n'est pas dans le cache ... il est nécessaire de la
                // charger. En attendant, nous allons utiliser une image par
                // défaut.
                thumbnail.setImageDrawable(mDefaultDrawable);
                thumbnail.setTag(imageId);

                mMissingImages.addFirst(thumbnail);

                // Le processus de chargement asynchrone est lancé uniquement si
                // la liste n'est pas un train de défiler
                if (mScrollState != OnScrollListener.SCROLL_STATE_FLING) {
                    sendFetchImageMessage(thumbnail);
                }
            }

            // L'intérêt est ici de bien comprendre comment charger des images
            // de façon asynchrone pour une utilisation dans une ListView. Nous
            // ne nous occupons donc pas des textes ...
            holder.mTitleBuilder.setLength(0);
            holder.mTitleBuilder.append(FAKE_TITLE_PREFIX).append(position);
            holder.mTitleView.setText(holder.mTitleBuilder);

            holder.mSubtitleBuilder.setLength(0);
            holder.mSubtitleBuilder.append(FAKE_SUBTITLE_PREFIX).append(position);
            holder.mSubtitleView.setText(holder.mSubtitleBuilder);

            return convertView;
        }
    }

    private void sendFetchImageMessage(ImageView view) {
        final int imageId = (Integer) view.getTag();
        ImageFetcher imageFetcher = new ImageFetcher(view, imageId);
        if (mPoolExecutors == null) {
            mPoolExecutors = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        }
        mPoolExecutors.execute(imageFetcher);
    }

    private void processImageFetching() {
        for (ImageView img : mMissingImages) {
            sendFetchImageMessage(img);
        }
    }

    public void clearImageFetching() {
        if (mPoolExecutors != null) {
            mPoolExecutors.shutdownNow();
            mPoolExecutors = null;
        }
        mHandler.removeMessages(FETCH_IMAGE_MESSAGE);
    }

    private class ImageFetcher implements Runnable {

        private int mImageId;
        private ImageView mImageView;

        public ImageFetcher(ImageView imageView, int imageId) {
            mImageView = imageView;
            mImageId = imageId;
        }

        public void run() {

            Bitmap bitmap = null;

            if (Thread.interrupted()) {
                return;
            }

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            try {
                // Pour faire un "réel" exemple il aurait été intéressant de
                // rapatrier des images à partir du Web. Malheureusement, cette
                // fonctionnalité engendre du code supplémentaire nuisant à la
                // lisibilité du code. Pour simplifier le chargement on simule
                // ici la connexion réseau par une simple attente.
                Thread.sleep(RandomUtil.getPositiveInt(BACKGROUND_TASK_MIN_DURATION, BACKGROUND_TASK_MAX_DURATION));
            } catch (InterruptedException e) {
                return;
            }

            synchronized (mSoftCache) {
                mSoftCache.put(mImageId, new SoftReference<Bitmap>(bitmap));
            }

            final Message msg = new Message();
            msg.what = FETCH_IMAGE_MESSAGE;
            msg.arg1 = mImageId;
            msg.obj = mImageView;
            mHandler.sendMessage(msg);
        }

    }

    private class ImageFetchHandler extends Handler {

        public void handleMessage(Message message) {

            switch (message.what) {
                case FETCH_IMAGE_MESSAGE: {

                    final ImageView imageView = (ImageView) message.obj;
                    if (imageView == null) {
                        break;
                    }

                    final Integer info = (Integer) imageView.getTag();
                    if (info == null) {
                        break;
                    }

                    final int imageId = info.intValue();
                    SoftReference<Bitmap> imageRef = mSoftCache.get(imageId);
                    if (imageRef == null) {
                        break;
                    }
                    Bitmap image = imageRef.get();
                    if (image == null) {
                        mSoftCache.remove(imageId);
                        break;
                    }

                    // Il ne nous reste plus maintenant qu'à verifier si
                    // l'identifiant de l'image n'a pas changé pendant le
                    // chargement de l'image. Si ce dernier a changé c'est que
                    // l'ImageView a été réutilisée par la ListView pour une
                    // autre position.
                    if (message.arg1 == imageId) {
                        if (Config.INFO_LOGS_ENABLED) {
                            Log.i(LOG_TAG, "L'image d'id '" + imageId + "' a été chargée");
                        }
                        imageView.setImageBitmap(image);
                        mMissingImages.remove(imageView);
                    } else {
                        if (Config.INFO_LOGS_ENABLED) {
                            Log.i(LOG_TAG, "L'image d'id '" + imageId
                                    + "' a été chargée mais l'ImageView a été réutilisée");
                        }
                    }

                    break;
                }

            }

        }

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Cette méthode n'est pas utile dans cet exemple.
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (getListView() == view) {

            if (Config.INFO_LOGS_ENABLED) {
                Log.i(LOG_TAG, "'scrollState' vient de changer à " + scrollState);
            }

            mScrollState = scrollState;
            if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                // Lorsque la liste passe dans l'état "fling", on abandonne tous
                // les chargements d'images pour éviter les "blocages" lors de
                // l'animation.
                clearImageFetching();
            } else {
                processImageFetching();
            }
        }
    }

}
