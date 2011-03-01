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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;

public class GLSurfaceViewInteraction extends GLSurfaceView implements GLSurfaceView.Renderer {

    public GLSurfaceViewInteraction(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(this);
        cubeBuff = createFloatBuffer(box);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, w, h);
        GLU.gluPerspective(gl, 45.0f, ((float) w) / h, 1f, 100f);
        init(gl);
    }

    protected void init(GL10 gl) {
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1);

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glClearDepthf(1.0f);
        gl.glShadeModel(GL10.GL_SMOOTH);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (moveCameraDown)
            yCamera += 0.1;

        if (moveCameraLeft)
            xCamera += 0.1;

        if (moveCameraRight)
            xCamera -= 0.1;

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0, 0, 3, xCamera, yCamera, 0, 0, 1, 0);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glRotatef(xrot, 1, 0, 0);
        gl.glRotatef(yrot, 0, 1, 0);

        gl.glColor4f(0.94f, 0.58f, 0.07f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

        gl.glColor4f(0.58f, 0.42f, 0.86f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

        gl.glColor4f(0.58F, 0.72f, 0.01f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

        xrot += 1.0f;
        yrot += 0.5f;
    }

    float box[] = new float[] {
            // FRONT
            -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
            // BACK
            -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
            // LEFT
            -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
            // RIGHT
            0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
            // TOP
            -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
            // BOTTOM
            -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
    };

    FloatBuffer cubeBuff;

    float xrot = 0.0f;
    float yrot = 0.0f;

    public float xCamera = 0.0f;
    public float yCamera = 0.0f;

    boolean moveCameraDown;
    boolean moveCameraLeft;
    boolean moveCameraRight;

    protected static FloatBuffer createFloatBuffer(float[] array) {
        ByteBuffer bytes = ByteBuffer.allocateDirect(array.length * 4);
        bytes.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bytes.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    public void moveCameraDown(boolean b) {
        moveCameraDown = b;
    }

    public void moveCameraLeft(boolean b) {
        moveCameraLeft = b;
    }

    public void moveCameraRight(boolean b) {
        moveCameraRight = b;
    }

}
