package cradle.rancune.learningandroid.opengl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import cradle.rancune.commons.logging.Logger;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/16.
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public abstract class Filter {
    private static final String TAG = "Filter";

    protected static final float[] sVertices = {
            -1.0f, 1.0f, // 左上
            -1.0f, -1.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, -1.0f, // 右下
    };
    protected FloatBuffer mVertexBuffer;

    protected static final float[] sTextureCoords = {
            0.0f, 0.0f, // 左上
            0.0f, 1.0f, // 左下
            1.0f, 0.0f, // 右上
            1.0f, 1.0f // 右下
    };
    protected static final float[] sCameraCoords = {
            0.0f, 1.0f, // 左上
            0.0f, 0.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, 0.0f // 右下
    };
    protected FloatBuffer mTextureCoordBuffer;

    protected int mProgram;
    protected int mTextureId;

    protected float[] mMatrix = new float[16];
    protected float[] mTextureMatrix = new float[16];

    protected Context mContext;

    public Filter(Context context) {
        mContext = context;
        mVertexBuffer = GLHelper.createFloatBuffer(sVertices);
        mTextureCoordBuffer = GLHelper.createFloatBuffer(sTextureCoords);
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.setIdentityM(mTextureMatrix, 0);
    }

    public final void performCreate() {
        onCreate();
    }

    public abstract void onCreate();

    public final void performDraw() {
        GLES20.glUseProgram(mProgram);
        onDraw();
    }

    public abstract void onDraw();

    public final void performDestroy() {
        onDestroy();
    }

    public void onDestroy() {

    }

    public abstract void onSizeChanged(int width, int height);

    public void setTextureMatrix(float[] matrix) {
        mTextureMatrix = matrix;
    }

    public void setTextureId(int id) {
        mTextureId = id;
    }

    public float[] getMatrix() {
        return mMatrix;
    }

    protected void createProgram(String vertexCode, String fragmentCode) {
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
        mProgram = linkProgram(vertexShader, fragmentShader);
    }

    protected void createFromAssets(String vertexPath, String fragmentPath) {
        createProgram(GLHelper.readFromAssets(mContext, vertexPath),
                GLHelper.readFromAssets(mContext, fragmentPath));
    }

    protected int getAttributeLocation(String name) {
        int location = GLES20.glGetAttribLocation(mProgram, name);
        if (location == -1) {
            Logger.d(TAG, "Attribute :" + name + " not found");
        }
        return location;
    }

    protected int getUniformLocation(String name) {
        int location = GLES20.glGetUniformLocation(mProgram, name);
        if (location == -1) {
            Logger.d(TAG, "uniform :" + name + " not found");
        }
        return location;
    }

    private static int compileShader(int type, String code) {
        if (code == null || code.isEmpty()) {
            Logger.d(TAG, "Shader code is empty");
            return -1;
        }
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            Logger.d(TAG, "Can not create shader");
            return -1;
        }
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Logger.d(TAG, "Can not compile shader");
            GLES20.glDeleteShader(shader);
            return -1;
        }
        return shader;
    }

    private static int linkProgram(int vertexShader, int fragmentShader) {
        if (vertexShader <= 0 && fragmentShader <= 0) {
            return -1;
        }
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Logger.d(TAG, "Can not create program");
            return -1;
        }
        if (vertexShader > 0) {
            GLES20.glAttachShader(program, vertexShader);
        }
        if (fragmentShader > 0) {
            GLES20.glAttachShader(program, fragmentShader);
        }
        GLES20.glLinkProgram(program);
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Logger.d(TAG, "Can not link program");
            GLES20.glDeleteProgram(program);
            return -1;
        }
        return program;
    }

}
