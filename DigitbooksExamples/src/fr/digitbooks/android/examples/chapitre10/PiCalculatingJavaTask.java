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

import java.util.Arrays;

import android.os.AsyncTask;
import android.util.Log;
import fr.digitbooks.android.examples.util.Config;

public class PiCalculatingJavaTask extends AsyncTask<Integer, Void, String> {

    private static final String LOG_TAG = PiCalculatingJavaTask.class.getSimpleName();

    int[] resul; /* TROIS TABLEAUX DYNAMIQUES CONTENANT RESPECTIVEMENT */
    int[] resul1; /* LE RESULTAT FINAL ET LES RESULTATS INTERMEDIAIRES */
    int[] resul2;
    int lim; /* NOMBRE DE DECIMALES DEMANDEES */
    int K; /* POUR GERER LES CASES NULLES */
    int MAX; /* DIMENSION DES TABLEAUX */
    int BASE; /* BASE DE NUMERATION CORRESPOND AU GROUPEMENT DES CHIFFRES */
    int GBASE; /* NOMBRE DE GROUPEMENT DES CHIFFRES POUR LA SORTIE */
    int I, J; /* NOMBRE D'ITERATIONS POUR CHAQUE ARCTG */

    PiCalculatingListener mListener;

    @SuppressWarnings("unused")
    private PiCalculatingJavaTask() {
    }

    public PiCalculatingJavaTask(PiCalculatingListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mListener.onCalculatingStart();
    }

    @Override
    protected String doInBackground(Integer... params) {
        Log.i(LOG_TAG, "BackGround Task Execute");
        int NB = 0;
        lim = params[0];

        if (lim <= 1500) {
            BASE = 1000000;
            NB = 6;
            GBASE = 10;
        } else {
            if (lim > 15000) {
                Log.e(LOG_TAG, "Pas plus de 15000 decimales");
                return null;
            }
            BASE = 100000;
            NB = 5;
            GBASE = 12;
        }

        MAX = 3 + lim / NB;
        resul = new int[MAX];
        resul1 = new int[MAX];
        resul2 = new int[MAX];
        // on mets les tableaux ˆ 0
        Arrays.fill(resul, 0);
        Arrays.fill(resul1, 0);
        Arrays.fill(resul2, 0);
        I = arctg_5(resul1, 5);
        System.arraycopy(resul, 0, resul2, 0, MAX);
        Arrays.fill(resul, 0);
        J = arctg_239(resul1, 239);
        display("resul", resul);
        K = 0;
        sous(resul2, resul);
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "Decimales affichees: " + NB * (MAX - 2) + "  nombre d'iterations: " + I + " , " + J + " ("
                    + (int) (I + J) + ")");
        }

        return todec(resul2);
    }

    @Override
    protected void onPostExecute(String result) {
        mListener.onCalculatingEnd(result);
    }

    private String todec(int[] tab) {
        int i;
        int a;
        int BS10 = BASE / 10;
        StringBuffer result = new StringBuffer("pi = " + tab[0] + ",");
        for (i = 1; i < MAX - 1; ++i) {
            a = tab[i];
            a = a == 0 ? 1 : a;
            while (a < BS10) {
                result.append("0");
                a = 10 * a;
            }
            result.append(tab[i] + " ");
        }
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, result.toString());
        }
        return result.toString();
    }

    private void sous(int[] a, int[] b) {
        int i, r = 0;
        for (i = MAX - 1; i >= K; --i) {
            r = a[i] - r - b[i];
            if (r < 0) {
                a[i] = r + BASE;
                r = 1;
            } else {
                a[i] = r;
                r = 0;
            }
        }
        while (r != 0) { /* PROPAGATION DE LA RETENUE */
            // Log.d(LOG_TAG, "a.length = " + a.length + " i = " + i);
            // Log.d(LOG_TAG, "value = " + a[i]);
            r = a[i] - r;
            if (r < 0) {
                a[i] = r + BASE;
                r = 1;
            } else {
                a[i] = r;
                r = 0;
            }
            --i;
            if (i == -1) {
                if (Config.INFO_LOGS_ENABLED) {
                    Log.d(LOG_TAG, "break");
                }
                break;
            }
        }

    }

    private void mul(int[] tab, int n) {
        int i, S = 0;
        for (i = MAX - 1; i >= K; --i) {
            tab[i] = tab[i] * n + S;
            S = tab[i] / BASE;
            tab[i] = tab[i] % BASE;
        }
        tab[i] = tab[i] * n + S;
        while ((S = tab[i]) >= BASE) {
            tab[i - 1] = tab[i - 1] + S / BASE;
            tab[i] = S % BASE;
            --i;
            --K;
        }
        --K;
        while (tab[K] == 0) {
            ++K; /* GESTION DES CASES NULLES */
            if (K == tab.length) {
                // Log.d(LOG_TAG, "break");
                break;
            }
            // Log.d(LOG_TAG, "t.length = " + t.length + " K = " + K);
            // Log.d(LOG_TAG, "value = " + t[K]);
        }
    }

    private void add(int[] a, int[] b) {
        int i, r = 0; /* r EST LA RETENUE */
        for (i = MAX - 1; i >= K; --i) {
            r = a[i] + r + b[i];
            if (r >= BASE) {
                a[i] = r - BASE;
                r = 1;
            } else {
                a[i] = r;
                r = 0;
            }
        }
        while (r != 0) { /* PROPAGATION DE LA RETENUE */
            r = a[i] + r;
            if (r >= BASE) {
                a[i] = r - BASE;
                r = 1;
            } else {
                a[i] = r;
                r = 0;
            }
            --i;
        }
    }

    private void divisi(int[] t, int n) {
        int a = 0, i;
        // Log.d(LOG_TAG, "K = " + K);
        if (K < MAX)
            a = t[K];
        // Log.d(LOG_TAG, "Max = " + MAX);
        for (i = K; i < MAX; ++i) {
            // Log.d(LOG_TAG, "a/n = " + a + "/" + n);
            t[i] = a / n;
            // Log.d(LOG_TAG, "i = " + i);
            if (i < MAX - 1)
                a = BASE * (a % n) + t[i + 1];
            else
                a = 0;
            // Log.d(LOG_TAG, "a = " + a);
        }

        if (K < MAX)
            while (t[K] == 0) {
                ++K; /* GESTION DES CASES NULLES */
                if (K == t.length) {
                    // Log.d(LOG_TAG, "break");
                    break;
                }
                // Log.d(LOG_TAG, "t.length = " + t.length + " K = " + K);
                // Log.d(LOG_TAG, "value = " + t[K]);
            }
    }

    private int arctg_5(int[] tab, int p) {
        int i;
        K = 0;
        tab[0] = 16;
        // display("tab", tab);
        // display("resul", resul);
        divisi(tab, p);
        // display("tab", tab);
        add(resul, tab);
        // display("tab", tab);
        // display("resul", resul);
        p = p * p;
        for (i = 0; K < MAX; ++i) {
            divisi(tab, (2 * i + 3));
            // display("tab", tab);
            divisi(tab, p);/* ICI p EST TROP PETIT POUR RISQUER DE DEPASSER */
            // display("tab", tab);
            mul(tab, (2 * i + 1));/* LES 31 BITS. */
            // display("tab", tab);
            if ((i + 1) % 2 != 0)
                sous(resul, tab);
            else
                add(resul, tab);
            // display("tab", tab);
            // display("resul", resul);
        }
        return (i);
    }

    private int arctg_239(int[] tab, int p) {
        int i;
        K = 0;
        tab[0] = 4;
        divisi(tab, p);
        add(resul, tab);
        for (i = 0; K < MAX; ++i) {
            divisi(tab, (2 * i + 3));
            divisi(tab, p);/* ON FAIT 2 DIVISIONS SUCCESSIVES AU LIEU D'UNE */
            divisi(tab, p);/* POUR EVITER DE DEPASSER LES 31 BITS */
            mul(tab, (2 * i + 1));
            if ((i + 1) % 2 != 0) {
                sous(resul, tab);
            } else
                add(resul, tab);
        }
        return i;
    }

    private void display(String c, int[] tab) {
        int i;
        StringBuffer value = new StringBuffer(c + " = {");
        for (i = 0; i < MAX; i++) {
            value.append(tab[i]);
            if (i < MAX - 1) {
                value.append(", ");
            }
        }
        value.append("}");
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, value.toString());
        }
    }

}
