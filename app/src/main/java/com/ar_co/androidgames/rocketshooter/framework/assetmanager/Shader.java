package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

public class Shader {

    public int shaderID;
    public int shaderType;
    public String shaderCode;

    public Shader(){

    }

    public Shader(String code, int type){
        this.shaderCode = code;
        this.shaderType = type;
    }

    public void setShaderCode(String code, int type){
        this.shaderCode = code;
        this.shaderType = type;
    }

}
