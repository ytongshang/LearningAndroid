package cradle.rancune.learningandroid.opengl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import cradle.rancune.learningandroid.opengl.util.GLHelper;
import cradle.rancune.learningandroid.opengl.util.MatrixUtils;

/**
 * Created by Rancune@126.com 2018/7/18.
 */
public class SaturationFilter extends Filter {
    private int mVertexPosition;
    private int mTextureCoordPosition;
    private int mMatrixPosition;
    private int mCoordMatrixPosition;
    private int mTexturePosition;
    private int mSaturationPosition;

    private int mTexture;

    private float mSaturation;
    private Bitmap mBitmap;

    private int mViewWidth;
    private int mViewHeight;

    public SaturationFilter(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        createFromAssets("filter/saturation.vert", "filter/saturation.frag");
        mVertexPosition = getAttributeLocation("a_Position");
        mTextureCoordPosition = getAttributeLocation("a_TextureCoordinate");
        mMatrixPosition = getUniformLocation("u_Matrix");
        mCoordMatrixPosition = getUniformLocation("u_CoordMatrix");
        mTexturePosition = getUniformLocation("u_Texture");
        mSaturationPosition = getUniformLocation("u_Saturation");

        mTexture = GLHelper.load2DTexture();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        if (mBitmap != null) {
            MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_CROP,
                    mBitmap.getWidth(), mBitmap.getHeight(),
                    mViewWidth, mViewHeight);
        }
    }

    @Override
    public void onDraw() {
        if (mBitmap == null) {
            return;
        }
        GLES20.glUniform1f(mSaturationPosition, mSaturation);
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(mCoordMatrixPosition, 1, false, mTextureMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        GLES20.glUniform1i(mTexturePosition, 0);

        GLES20.glEnableVertexAttribArray(mVertexPosition);
        GLES20.glVertexAttribPointer(mVertexPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordPosition);
        GLES20.glVertexAttribPointer(mTextureCoordPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVertexPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordPosition);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_CROP,
                mBitmap.getWidth(), mBitmap.getHeight(),
                mViewWidth, mViewHeight);
    }

    public void setSaturation(float saturation) {
        mSaturation = saturation;
    }
}
