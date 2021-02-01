/*
 * Copyright (C) The Android Open Source Project
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
package kr.coursemos.yonsei.camera;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    public long facestarttime =0;
    public long facecurrenttime =0;
    public String text="";
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 60.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    public FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
//        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        final int selectedColor = Color.BLACK;
        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    public void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */



    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }
        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;



        if(left>0&&top>0&&canvas.getWidth()>right&&canvas.getHeight()>bottom){
            if(canvas.getWidth()/2>(right-left)){
                facestarttime=0;
                text="가까이 와주세요.";
//                canvas.drawText("가까이 와주세요.",canvas.getWidth()/6,canvas.getHeight()/2,mIdPaint);
            }else{
                text="얼굴 인식 중입니다. 잠시만 기다려 주세요.";
                if(facestarttime==0){
                    facestarttime=System.currentTimeMillis();
                }
                facecurrenttime=System.currentTimeMillis();
            }

//Env.debug(null,(right-left)+"!!"+(bottom-top)+"!!"+canvas.getWidth()+"!!"+canvas.getHeight());
//            canvas.drawText((right-left)+"!!"+(bottom-top)+"!!"+canvas.getWidth()+"!!"+canvas.getHeight(),canvas.getWidth()/2,canvas.getHeight()/2,mIdPaint);

//            Env.debug(null,facestarttime+"!!"+System.currentTimeMillis()+"!!"+(facestarttime+1000-System.currentTimeMillis()));
//            canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
//            canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
//            canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
//            canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
//            canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
//
//            canvas.drawRect(left, top, right, bottom, mBoxPaint);
//            canvas.drawText("----: " + String.format("%.0f  %.0f-%.0f  %.0f  %.0f-%.0f", left, right,canvas.getWidth(),top , bottom,canvas.getHeight()), left, (top+bottom)/2, mIdPaint);
        }else{
            facestarttime=0;
            text="얼굴을 사각틀 안에 맞춰주세요.";
//            canvas.drawText("얼굴을 사각틀 안에 맞춰주세요.",canvas.getWidth()/6,canvas.getHeight()/2,mIdPaint);


        }


    }
}
