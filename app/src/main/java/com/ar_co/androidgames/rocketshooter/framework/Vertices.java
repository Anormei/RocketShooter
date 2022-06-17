package com.ar_co.androidgames.rocketshooter.framework;

import android.opengl.GLES20;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

public class Vertices{
    final GLGraphics glGraphics;
    final boolean hasColor;
    final boolean hasTexCoords;
    final int vertexSize;
    int maxVertices;
    int maxIndices;
    FloatBuffer vertices;
    ShortBuffer indices;

    private int positionHandle;
    private int colorHandle;
    private int texCoordHandle;
    private int mvpHandle;

    private final String vertexShaderCode = "attribute vec4 position;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}";

    public Vertices(GLGame game, GLGraphics glGraphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords) {
        this.glGraphics = glGraphics;
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (2 + (hasColor ? 4 : 0) + (hasTexCoords ? 2 : 0)) * 4;
        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        if (maxIndices > 0) {
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();

        } else {
            indices = null;
        }

        /*int vertexShader = Assets.getInstance(game).getShaderCode(GLES20.GL_VERTEX_SHADER, "shaders/DefaultVertShader");
       // int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader = Assets.getInstance(game).getShaderCode(GLES20.GL_FRAGMENT_SHADER, "shaders/DefaultFragShader");

        program = GLES20.glCreateProgram();
        Log.i("Vertices", "program = " + program + ", vertex shader = " + vertexShader +
                ", fragment shader = " + fragmentShader + ", shader source = " + GLES20.glGetShaderSource(vertexShader) +
                "\n" + GLES20.glGetShaderSource(fragmentShader));

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);

        GLES20.glDetachShader(program, vertexShader);
        GLES20.glDetachShader(program, fragmentShader);

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);*/

        /*final int[] attSize = new int[1];

        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_ATTRIBUTES, attSize, 0);

            Log.i("Vertices", "attribute size = " + attSize[0]);*/
    }

    public void extendSize(int maxVertices, int maxIndices){
        this.maxVertices += maxVertices;
        this.maxIndices += maxIndices;

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        if (maxIndices > 0) {
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();

        } else {
            indices = null;
        }
    }

    public void setVertices(float[] vertices, int offset, int length) {
        this.vertices.clear();
        this.vertices.put(vertices, offset, length);
        this.vertices.flip();
    }

    public void setIndices(short[] indices, int offset, int length) {
        this.indices.clear();
        this.indices.put(indices, offset, length);
        this.indices.flip();
    }

    public void bind(ShaderProgram shaderProgram) {

        int program = shaderProgram.programID;
        GLES20.glUseProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "position");
        texCoordHandle = GLES20.glGetAttribLocation(program, "texCoordIn");
        colorHandle = GLES20.glGetAttribLocation(program, "inColor");

        mvpHandle = GLES20.glGetUniformLocation(program, "mvp");

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, shaderProgram.mvp, 0);


        GLES20.glEnableVertexAttribArray(positionHandle);
        //Log.i("Vertices", String.format("program = %d, positionHandle = %d, colorHandle = %d, texCoordHandle = %d, mvpHandle = %d", program, positionHandle, colorHandle, texCoordHandle, mvpHandle));
        vertices.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, vertexSize, vertices);
        //Log.i("Vertices", "inputting positions");
        if(hasColor){
            GLES20.glEnableVertexAttribArray(colorHandle);
            vertices.position(2);
            //gl.glColorPointer(4, GL11.GL_FLOAT, vertexSize, vertices);
            GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, vertexSize, vertices);
        }

        if(hasTexCoords){
            GLES20.glEnableVertexAttribArray(texCoordHandle);
            vertices.position(hasColor?6:2);
            //gl.glTexCoordPointer(2, GL11.GL_FLOAT, vertexSize, vertices);
            GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, vertexSize, vertices);
        }

    }

    public void draw(int primitiveType, int offset, int numVertices) {

        if (indices != null) {
            //gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
            indices.position(offset);
            GLES20.glDrawElements(primitiveType, numVertices, GL11.GL_UNSIGNED_SHORT, indices);
        } else {
            GLES20.glDrawArrays(primitiveType, offset, numVertices);
        }
    }

    public void unbind() {

        GLES20.glDisableVertexAttribArray(positionHandle);

        if (hasTexCoords) {
            //gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GLES20.glDisableVertexAttribArray(texCoordHandle);
        }

        if (hasColor) {
            //gl.glDisableClientState(GL11.GL_COLOR_ARRAY);..
            GLES20.glDisableVertexAttribArray(colorHandle);
        }
        //gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
