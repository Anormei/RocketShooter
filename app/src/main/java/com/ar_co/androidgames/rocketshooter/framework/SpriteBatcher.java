package com.ar_co.androidgames.rocketshooter.framework;

import android.opengl.GLES20;
import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.ShaderProgram;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.TextureAtlas;

import javax.microedition.khronos.opengles.GL11;

public class SpriteBatcher extends Vertices{

    //private GLGraphics glGraphics;
    public Visual visual;

    //private short[] indicesBuffer = new short[]{0, 1, 2, 2, 3, 1};
    private short[] indicesBuffer = new short[]{0, 1, 2, 2, 3, 1};
    protected final float[] verticesBuffer;

    protected int size;
    protected int length;

    private int textureHandle;

    public abstract class Visual{
        public abstract void draw(Sprite sprite);
    }

    private Visual visualNormal = new Visual(){
        @Override
        public void draw(Sprite sprite){
            if(length > size){
                extendSize(1000, 1000);
            }

            float posX = sprite.pos.x;
            float posY = sprite.pos.y;

            /*if(sprite.width < 0) {
                posX += sprite.width;
            }

            if(sprite.height < 0) {
                posY += sprite.height;
            }*/

            posX -= sprite.rX;
            posY -= sprite.rY;

            //Up-Left corner
            verticesBuffer[0] = sprite.rotationMatrix[0] * posX + sprite.rotationMatrix[1] * posY + 0.375f + sprite.rX;
            verticesBuffer[1] = sprite.rotationMatrix[2] * posX + sprite.rotationMatrix[3] * posY + 0.375f + sprite.rY;

            //Up-Right Corner
            verticesBuffer[8] = sprite.rotationMatrix[0] * (posX + sprite.width) + sprite.rotationMatrix[1] * posY + 0.375f + sprite.rX;
            verticesBuffer[9] = sprite.rotationMatrix[2] * (posX + sprite.width) + sprite.rotationMatrix[3] * posY + 0.375f + sprite.rY;

            //Bottom-Left Corner
            verticesBuffer[16] = sprite.rotationMatrix[0] * posX + sprite.rotationMatrix[1] * (posY + sprite.height) + 0.375f + sprite.rX;
            verticesBuffer[17] = sprite.rotationMatrix[2] * posX + sprite.rotationMatrix[3] * (posY + sprite.height) + 0.375f + sprite.rY;

            //Bottom-Right Corner
            verticesBuffer[24] = sprite.rotationMatrix[0] * (posX + sprite.width) + sprite.rotationMatrix[1] * (posY + sprite.height) + 0.375f + sprite.rX;
            verticesBuffer[25] = sprite.rotationMatrix[2] * (posX + sprite.width) + sprite.rotationMatrix[3] * (posY + sprite.height) + 0.375f + sprite.rY;

            //posX += sprite.rX;
            //posY += sprite.rY;

            for(int i = 2; i < verticesBuffer.length; i += 8) {
                verticesBuffer[i] = sprite.rgb[0];
                verticesBuffer[i+1] = sprite.rgb[1];
                verticesBuffer[i+2] = sprite.rgb[2];
                verticesBuffer[i+3] = sprite.alpha;
            }

            verticesBuffer[6] = sprite.region.left;
            verticesBuffer[7] = sprite.region.top;
            verticesBuffer[14] = sprite.region.right;
            verticesBuffer[15] = sprite.region.top;
            verticesBuffer[22] = sprite.region.left;
            verticesBuffer[23] = sprite.region.bottom;
            verticesBuffer[30] = sprite.region.right;
            verticesBuffer[31] = sprite.region.bottom;

            length++;
            setVertices(verticesBuffer, 0, vertexSize);
        }
    };

    private Visual visualNoTexture = new Visual(){
        @Override
        public void draw(Sprite sprite){
            if(length > size){
                extendSize(1000, 1000);
            }

            float posX = sprite.pos.x;
            float posY = sprite.pos.y;

            posX -= sprite.rX;
            posY -= sprite.rY;

            //Up-Left corner
            verticesBuffer[0] = sprite.rotationMatrix[0] * posX + sprite.rotationMatrix[1] * posY + 0.375f + sprite.rX;
            verticesBuffer[1] = sprite.rotationMatrix[2] * posX + sprite.rotationMatrix[3] * posY + 0.375f + sprite.rY;

            //Up-Right Corner
            verticesBuffer[6] = sprite.rotationMatrix[0] * (posX + sprite.width) + sprite.rotationMatrix[1] * posY + 0.375f + sprite.rX;
            verticesBuffer[7] = sprite.rotationMatrix[2] * (posX + sprite.width) + sprite.rotationMatrix[3] * posY + 0.375f + sprite.rY;

            //Bottom-Left Corner
            verticesBuffer[12] = sprite.rotationMatrix[0] * posX + sprite.rotationMatrix[1] * (posY + sprite.height) + 0.375f + sprite.rX;
            verticesBuffer[13] = sprite.rotationMatrix[2] * posX + sprite.rotationMatrix[3] * (posY + sprite.height) + 0.375f + sprite.rY;

            //Bottom-Right Corner
            verticesBuffer[18] = sprite.rotationMatrix[0] * (posX + sprite.width) + sprite.rotationMatrix[1] * (posY + sprite.height) + 0.375f + sprite.rX;
            verticesBuffer[19] = sprite.rotationMatrix[2] * (posX + sprite.width) + sprite.rotationMatrix[3] * (posY + sprite.height) + 0.375f + sprite.rY;

            posX += sprite.rX;
            posY += sprite.rY;

            for(int i = 2; i < verticesBuffer.length; i += 6) {
                verticesBuffer[i] = sprite.rgb[0];
                verticesBuffer[i+1] = sprite.rgb[1];
                verticesBuffer[i+2] = sprite.rgb[2];
                verticesBuffer[i+3] = sprite.alpha;
            }

            length++;
            setVertices(verticesBuffer, 0, 24);
        }
    };

    public SpriteBatcher(GLGame game, int size, boolean hasColor, boolean hasTexCoords){
        super(game, game.getGLGraphics(), size * 4, size * 6, hasColor, hasTexCoords);
        verticesBuffer = new float[vertexSize];
        this.size = size;

        for(int i = 0; i < size; i++){
            setIndices(indicesBuffer, 0, 6);
            for(int j = 0; j < indicesBuffer.length; j++){
                indicesBuffer[j] += 4;
            }
        }
        indices.flip();

        if(hasTexCoords){
            visual = visualNormal;
        }else{
            visual = visualNoTexture;
        }
    }

    public SpriteBatcher(GLGame game, int size){
        this(game, size, true, true);
        /*this.glGraphics = game.getGLGraphics();
        GL11 gl = glGraphics.getGL11();
        this.size = size;
        this.length = length;

        vertices = new Vertices(glGraphics, 4, 6, true, true);
        vertices.setIndices(indicesBuffer, 0, 6);

        gl.glGenBuffers(2, id, 0);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, id[0]);
        gl.glBufferData(GL11.GL_ARRAY_BUFFER, size * length * 4, null, GL11.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, id[1]);
        gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, size * 6 * 2, null, GL11.GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        VerticesReloader.getInstance().addReloader(this);*/
    }

    @Override
    public void setVertices(float[] vertices, int offset, int length){
        this.vertices.put(vertices, offset, length);
    }

    public void setIndices(short[] indices, int offset, int length){
        this.indices.put(indices, offset, length);
    }

    public void startBatch(){
        //Log.i("SpriteBatcher", "Batch Started");
        vertices.clear();
        //gl.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        length = 0;
    }

    public void endBatch(TextureAtlas atlas, ShaderProgram shaderProgram){
        if(length == 0){
            return;
        }

        //GLES20.glLoadIdentity();
        this.vertices.flip();
        bind(shaderProgram);
        //vertices.flip();
        textureHandle = GLES20.glGetUniformLocation(shaderProgram.programID, "tex");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GLES20.glUniform1i(textureHandle, 0);
        atlas.bind();
        draw(GL11.GL_TRIANGLES, 0, length * 6);
        unbind();
    }
}
