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
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

public class JavascriptInterfaceActivity extends Activity {

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.js_interface);

        mWebView = (WebView) findViewById(R.id.webContent);

        final WebSettings webSettings = mWebView.getSettings();
        /*
         * Ne pas oublier d'activer le Javascript car il est désactivé par
         * défaut sur une WebView...
         */
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        mWebView.addJavascriptInterface(new NextStepJavascriptInterface(), "sample904");
        mWebView.setWebChromeClient(new AlertChromeClient());

        mWebView.loadUrl("file:///android_asset/page.html");
    }

    public class NextStepJavascriptInterface {

        /*
         * Méthode exécutée à partir du code Javascript de la page XHTML. Le
         * code utilise un Handler pour exécuter le code dans le main thread. En
         * effet, la WebView exécute toujours le code des interfaces Javascript
         * dans des threads différents du main thread.
         */
        public void onNextStepClicked() {
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                     * On demande ici à la webView d'éxécuter une méthode
                     * Javascript (exemple d'appel Javascript à partir de Java).
                     */
                    mWebView.loadUrl("javascript:nextStep()");
                }
            });
        }
    }

    /*
     * L'AlertChromeClient s'occupe d'intercepter les alert() Javascript afin de
     * les afficher sous forme de Toast sans aucune validation de l'utilisateur.
     */
    public class AlertChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(JavascriptInterfaceActivity.this, message, Toast.LENGTH_LONG).show();
            result.confirm();
            return true;
        }
    }

}
