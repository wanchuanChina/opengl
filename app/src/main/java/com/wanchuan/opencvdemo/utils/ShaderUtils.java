package com.wanchuan.opencvdemo.utils;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

import java.io.InputStream;

public class ShaderUtils {

    private static final String TAG="ShaderUtils";

    private ShaderUtils(){
    }

    public static void checkGLError(String op){
        Log.e("wuwang",op);
    }

    public static int loadShader(int shaderType,String source){
        int shader= GLES30.glCreateShader(shaderType);
        if(0!=shader){
            GLES30.glShaderSource(shader,source);
            GLES30.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                Log.e(TAG,"Could not compile shader:"+shaderType);
                Log.e(TAG,"GLES30 Error:"+ GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }

    public static int loadShader(Resources res,int shaderType,String resName){
        return loadShader(shaderType,loadFromAssetsFile(resName,res));
    }

    public static int createProgram(String vertexSource, String fragmentSource){
        int vertex=loadShader(GLES30.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        int fragment=loadShader(GLES30.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        int program= GLES30.glCreateProgram();
        if(program!=0){
            GLES30.glAttachShader(program,vertex);
            checkGLError("Attach Vertex Shader");
            GLES30.glAttachShader(program,fragment);
            checkGLError("Attach Fragment Shader");
            GLES30.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES30.GL_TRUE){
                Log.e(TAG,"Could not link program:"+ GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    public static int createProgram(Resources res, String vertexRes, String fragmentRes){
        return createProgram(loadFromAssetsFile(vertexRes,res),loadFromAssetsFile(fragmentRes,res));
    }

    public static String loadFromAssetsFile(String fname, Resources res){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=res.getAssets().open(fname);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

}