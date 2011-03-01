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
package fr.digitbooks.android.examples.chapitre02;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleProvider extends Activity {

	private static final String[] IMAGE_PROJECTION = new String[] {
		BaseColumns._ID,
        "_data",
        ImageColumns.DATE_TAKEN,
        ImageColumns.MINI_THUMB_MAGIC,
        ImageColumns.ORIENTATION,
        ImageColumns.MIME_TYPE
    };

	private static final String[] THUMB_PROJECTION = new String[] {
		BaseColumns._ID,           // 0
		android.provider.MediaStore.Images.Thumbnails.IMAGE_ID,      // 1
		android.provider.MediaStore.Images.Thumbnails.WIDTH,
		android.provider.MediaStore.Images.Thumbnails.HEIGHT
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Aucune image dans le fournisseur de médias");
        textView.setVisibility(View.GONE);
        // Create image
        ImageView imageView = new ImageView(this);
        // Create parameters for
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        // Add image on layout
        addContentView(imageView, params);
        addContentView(textView, params);

        // Find id of last media indexes
        ContentResolver cr = this.getContentResolver();
        Cursor medias = Images.Media.query(cr, Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                Images.Media.MIME_TYPE + " in (?, ?, ?)", new String[] {
                        "image/jpeg", "image/png", "image/gif"
                }, Images.Media.DATE_TAKEN + " DESC");

        long mediaId = -1;
        if (medias != null) {
            if (medias.getCount() != 0) {
                medias.moveToFirst();
                int index = medias.getColumnIndex(BaseColumns._ID);
                mediaId = medias.getLong(index);
            }
        }

        if (mediaId != -1) {
            Cursor c = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, THUMB_PROJECTION,
                    Thumbnails.IMAGE_ID + "=?", new String[] {
                        String.valueOf(mediaId)
                    }, null);

            long thumbId = -1;
            if (c != null) {
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    int index = c.getColumnIndex(BaseColumns._ID);
                    thumbId = c.getLong(index);
                }
            }
            if (thumbId != -1) {
                Uri uriMediaThumb = ContentUris.withAppendedId(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbId);

                try {
                    InputStream is = cr.openInputStream(uriMediaThumb);
                    if (is != null) {
                        Bitmap bmp = Bitmap.createBitmap(BitmapFactory.decodeStream(is));
                        imageView.setImageBitmap(bmp);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }
}
