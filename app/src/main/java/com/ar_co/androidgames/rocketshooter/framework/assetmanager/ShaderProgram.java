package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;

public class ShaderProgram {


    public int programID;
    public float[] mvp;
    public List<Shader> shaders = new ArrayList<>();

    public ShaderProgram(){

    }

    public ShaderProgram(Shader... shaders){
        for(int i = 0; i < shaders.length; i++){
            this.shaders.add(shaders[i]);
        }
    }

    public void setMVP(float[] mvp){
        this.mvp = mvp;
    }

    public void compile(){
        programID = glCreateProgram();

        for(int i = 0; i < shaders.size(); i++){
            Shader shader = shaders.get(i);
            int shaderID = glCreateShader(shader.shaderType);
            shader.shaderID = shaderID;
            glShaderSource(shaderID, shader.shaderCode);
            glCompileShader(shaderID);
            glAttachShader(programID, shaderID);
        }

        glLinkProgram(programID);

        for(int i = 0; i < shaders.size(); i++){
            Shader shader = shaders.get(i);
            glDetachShader(programID, shader.shaderID);
            glDeleteShader(shader.shaderID);
        }
    }
}
