package kr.coursemos.yonsei.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceView;

import kr.coursemos.yonsei.a99util.Env;

public class cameraSurfaceView extends SurfaceView {
    public cameraSurfaceView(Context context) {
        super(context);
    }

    public Bitmap drawBitmap() throws Exception {



        Env.debug(null,getWidth()+"!!"+getHeight());
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        setZOrderOnTop(true);
        draw(canvas);
        setZOrderOnTop(false);

//         onDraw(canvas);
//         draw(canvas);
        return bitmap;
    }

}
