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
package fr.digitbooks.android.examples.chapitre11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.RandomUtil;

public class BallView extends View {

    /**
     * Classe permettant de représenter une balle dans un environnement 2D
     * disposant d'une position et d'une vitesse ajustable.
     */
    private static class Ball {
        public int pX;
        public int pY;
        public float vX;
        public float vY;
    }

    private static final int POST_DELAY = 1000 / 60;

    /**
     * Définit les vitesses minimales et maximales qu'aura la balle.
     */
    private static final float MAX_VELOCITY = 10.0f;

    private final Handler mHandler = new Handler();

    /**
     * Runnable qui sera exécuté de façon répétitive et permettra d'animer la
     * balle.
     */
    private final Runnable mUpdaterRunnable = new Runnable() {
        public void run() {
            if (mInitialized) {
                mBall.pX += mBall.vX;
                mBall.pY += mBall.vY;

                final int frameRight = getWidth() - mBallBitmap.getWidth();
                final int frameBottom = getHeight() - mBallBitmap.getHeight();

                if (mBall.pX < 0) {
                    mBall.pX = 0;
                } else if (mBall.pX > frameRight) {
                    mBall.pX = frameRight;
                }

                if (mBall.pY < 0) {
                    mBall.pY = 0;
                } else if (mBall.pY > frameBottom) {
                    mBall.pY = frameBottom;
                }
                invalidate();
            }

            /*
             * On reposte ici le Runnable après un temps équivalent à
             * POST_DELAY. Ce temps est calculé en fonction du taux de
             * rafraîchissement maximal de l'écran (60 frames par secondes).
             * POST_DELAY équivaut alors à 1000/60
             */
            mHandler.postDelayed(this, POST_DELAY);
        }
    };

    private boolean mInitialized = false;

    private Bitmap mBallBitmap;
    private Ball mBall;

    public BallView(Context context) {
        this(context, null);
    }

    public BallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mBallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    }

    public void start() {
        /*
         * Cette méthode permet d'animer la vue. Si cette dernière n'est pas
         * visible. Il n'est pas nécessaire de lancer le Runnable.
         */
        if (getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mUpdaterRunnable);
            mHandler.post(mUpdaterRunnable);
        }
    }

    public void stop() {
        /*
         * Pour stopper l'animation on supprime simplement le Runnable du
         * Handler.
         */
        mHandler.removeCallbacks(mUpdaterRunnable);
    }

    /*
     * Fonction a appelé pour modifier la vitesse de la balle
     */
    public void setVelocities(float vx, float vy) {
        // On valide que les valeurs données en paramètre sont dans une
        // fourchette
        // acceptable
        if (vx < -MAX_VELOCITY) {
            vx = -MAX_VELOCITY;
        } else if (vx > MAX_VELOCITY) {
            vx = MAX_VELOCITY;
        }
        mBall.vX = vx;

        if (vy < -MAX_VELOCITY) {
            vy = -MAX_VELOCITY;
        } else if (vy > MAX_VELOCITY) {
            vy = MAX_VELOCITY;
        }
        mBall.vY = vy;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*
         * Cette méthode est appelée lorsque la vue est en train d'être
         * supprimée. Il est donc nécessaire de stopper l'animation.
         */
        stop();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        /*
         * Le système nous notifie ici dès que la vue a changé de dimension.
         * C'est donc l'occasion pour nous d'ajouter la balle
         */
        if (!mInitialized) {
            mBall = new Ball();
            mBall.pX = RandomUtil.getPositiveInt(0, getWidth() - mBallBitmap.getWidth());
            mBall.pY = RandomUtil.getPositiveInt(0, getHeight() - mBallBitmap.getHeight());
            mBall.vX = 0.0f;
            mBall.vY = 0.0f;
            mInitialized = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = resolveAdjustedSize(mBallBitmap.getWidth() << 1, widthMeasureSpec);
        final int h = resolveAdjustedSize(mBallBitmap.getHeight() << 1, heightMeasureSpec);

        setMeasuredDimension(w, h);
    }

    private int resolveAdjustedSize(int desiredSize, int measureSpec) {

        /*
         * Cette méthode permet de réconcilier la taille désirée par la vue avec
         * les contraintes imposées par le parent.
         */
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // On dessine la balle
        canvas.drawBitmap(mBallBitmap, mBall.pX, mBall.pY, null);
    }

}
