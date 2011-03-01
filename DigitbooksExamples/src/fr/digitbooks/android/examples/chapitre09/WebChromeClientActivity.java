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
package fr.digitbooks.android.examples.chapitre09;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

public class WebChromeClientActivity extends Activity {

    private TextView mTitle;
    private TextView mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.web_chrome_client);

        mTitle = (TextView) findViewById(R.id.title);
        mProgress = (TextView) findViewById(R.id.progress);

        final WebView webView = (WebView) findViewById(R.id.webContent);
        webView.setWebChromeClient(mChromeClient);

        webView.clearCache(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        setupChrome();

        webView.loadUrl(getString(R.string.android_website));
    }

    private void setupChrome() {
        mTitle.setText(getString(R.string.default_title));
    }

    private WebChromeClient mChromeClient = new WebChromeClient() {

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Toast.makeText(WebChromeClientActivity.this, getString(R.string.alert_template, message, url),
                    Toast.LENGTH_LONG).show();

            result.confirm();
            return true;
        }

        public void onReceivedTitle(WebView view, String title) {
            mTitle.setText(title);
        }

        public void onProgressChanged(WebView view, int newProgress) {
            mProgress.setText(getString(R.string.progress_template, newProgress));
        }
    };

}
