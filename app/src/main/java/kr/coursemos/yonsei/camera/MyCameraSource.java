package kr.coursemos.yonsei.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import androidx.annotation.RequiresPermission;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class MyCameraSource  {
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_BACK = 0;
    @SuppressLint({"InlinedApi"})
    public static final int CAMERA_FACING_FRONT = 1;
    private Context zze;
    private final Object zzf;
    @GuardedBy("cameraLock")
    private Camera camera;
    private int facing;
    private int rotation;
    private Size zzh;
    private float zzi;
    private int zzj;
    private int zzk;
    private boolean zzl;
    private String zzm;
    private SurfaceTexture zzn;
    private boolean zzo;
    private Thread zzp;
    private zzb zzq;
    private Map<byte[], ByteBuffer> zzr;


    public void release() {
        synchronized(this.zzf) {
            this.stop();
            this.zzq.release();
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public MyCameraSource start() throws IOException {
        synchronized(this.zzf) {
            if (this.camera != null) {
                return this;
            } else {
                this.camera = this.zza();
                this.zzn = new SurfaceTexture(100);
                this.camera.setPreviewTexture(this.zzn);
                this.zzo = true;
                this.camera.startPreview();
                this.zzp = new Thread(this.zzq);
                this.zzp.setName("gms.vision.MyCameraSource");
                this.zzq.setActive(true);
                this.zzp.start();
                return this;
            }
        }
    }

    @RequiresPermission("android.permission.CAMERA")
    public MyCameraSource start(SurfaceHolder var1) throws IOException {
        synchronized(this.zzf) {
            if (this.camera != null) {
                return this;
            } else {
                this.camera = this.zza();
                this.camera.setPreviewDisplay(var1);
                this.camera.startPreview();
                this.zzp = new Thread(this.zzq);
                this.zzq.setActive(true);
                this.zzp.start();
                this.zzo = false;
                return this;
            }
        }
    }

    public void stop() {
        synchronized(this.zzf) {
            this.zzq.setActive(false);
            if (this.zzp != null) {
                try {
                    this.zzp.join();
                } catch (InterruptedException var6) {
                    Log.d("MyCameraSource", "Frame processing thread interrupted on release.");
                }

                this.zzp = null;
            }

            if (this.camera != null) {
                this.camera.stopPreview();
                this.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)null);

                try {
                    if (this.zzo) {
                        this.camera.setPreviewTexture((SurfaceTexture)null);
                    } else {
                        this.camera.setPreviewDisplay((SurfaceHolder)null);
                    }
                } catch (Exception var5) {
                    String var3 = String.valueOf(var5);
                    Log.e("MyCameraSource", (new StringBuilder(32 + String.valueOf(var3).length())).append("Failed to clear camera preview: ").append(var3).toString());
                }

                this.camera.release();
                this.camera = null;
            }

            this.zzr.clear();
        }
    }

    public Size getPreviewSize() {
        return this.zzh;
    }

    public int getCameraFacing() {
        return this.facing;
    }

    public void takePicture(ShutterCallback var1, PictureCallback var2) {
        synchronized(this.zzf) {
            if (this.camera != null) {
                zzd var5;
                (var5 = new zzd()).zzab = var2;
                this.camera.enableShutterSound(false);
                this.camera.takePicture(null, (Camera.PictureCallback)null, (Camera.PictureCallback)null, var5);
            }

        }
    }

    private MyCameraSource() {
        this.zzf = new Object();
        this.facing = 0;
        this.zzi = 30.0F;
        this.zzj = 1024;
        this.zzk = 768;
        this.zzl = false;
        this.zzr = new HashMap();
    }

    @SuppressLint({"InlinedApi"})
    private final Camera zza() throws IOException {
        int var8 = this.facing;
        Camera.CameraInfo var9 = new Camera.CameraInfo();
        int var10 = 0;

        int var10000;
        while(true) {
            if (var10 >= Camera.getNumberOfCameras()) {
                var10000 = -1;
                break;
            }

            Camera.getCameraInfo(var10, var9);
            if (var9.facing == var8) {
                var10000 = var10;
                break;
            }

            ++var10;
        }

        int var1 = var10000;
        if (var10000 == -1) {
            throw new IOException("Could not find requested camera.");
        } else {
            Camera var2;
            Camera var40 = var2 = Camera.open(var1);
            var10 = this.zzk;
            int var32 = this.zzj;
            Camera.Parameters var19;
            List var20 = (var19 = var40.getParameters()).getSupportedPreviewSizes();
            List var21 = var19.getSupportedPictureSizes();
            ArrayList var22 = new ArrayList();
            Iterator var23 = var20.iterator();

            while(true) {
                Camera.Size var24;
                while(var23.hasNext()) {
                    float var25 = (float)(var24 = (Camera.Size)var23.next()).width / (float)var24.height;
                    Iterator var26 = var21.iterator();

                    while(var26.hasNext()) {
                        Camera.Size var27;
                        float var28 = (float)(var27 = (Camera.Size)var26.next()).width / (float)var27.height;
                        if (Math.abs(var25 - var28) < 0.01F) {
                            var22.add(new zze(var24, var27));
                            break;
                        }
                    }
                }

                if (var22.size() == 0) {
                    Log.w("MyCameraSource", "No preview sizes have a corresponding same-aspect-ratio picture size");
                    var23 = var20.iterator();

                    while(var23.hasNext()) {
                        var24 = (Camera.Size)var23.next();
                        var22.add(new zze(var24, (Camera.Size)null));
                    }
                }

                MyCameraSource.zze var12 = null;
                int var13 = 2147483647;
                ArrayList var29;
                int var30 = (var29 = (ArrayList)var22).size();
                int var31 = 0;
                Iterator var14 = null;

                int var17;
                while(var31 < var30) {
                    Object var41 = var29.get(var31);
                    ++var31;
                    MyCameraSource.zze var15;
                    Size var16;
                    if ((var17 = Math.abs((var16 = (var15 = (MyCameraSource.zze)var41).zzb()).getWidth() - var32) + Math.abs(var16.getHeight() - var10)) < var13) {
                        var12 = var15;
                        var13 = var17;
                    }
                }

                if (var12 == null) {
                    throw new IOException("Could not find suitable preview size.");
                }

                Size var4 = var12.zzc();
                this.zzh = var12.zzb();
                float var33 = this.zzi;
                var10 = (int)(var33 * 1000.0F);
                int[] var11 = null;
                int var34 = 2147483647;
                var14 = var2.getParameters().getSupportedPreviewFpsRange().iterator();

                int var39;
                while(var14.hasNext()) {
                    int[] var37 = (int[])var14.next();
                    var39 = var10 - var37[0];
                    var17 = var10 - var37[1];
                    int var18;
                    if ((var18 = Math.abs(var39) + Math.abs(var17)) < var34) {
                        var11 = var37;
                        var34 = var18;
                    }
                }

                if (var11 == null) {
                    throw new IOException("Could not find suitable preview frames per second range.");
                }

                Camera.Parameters var6 = var2.getParameters();
                if (var4 != null) {
                    var6.setPictureSize(var4.getWidth(), var4.getHeight());
                }

                var6.setPreviewSize(this.zzh.getWidth(), this.zzh.getHeight());
                var6.setPreviewFpsRange(var11[0], var11[1]);
                var6.setPreviewFormat(17);
                int var7 = ((WindowManager)this.zze.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                short var35 = 0;
                switch(var7) {
                    case 0:
                        var35 = 0;
                        break;
                    case 1:
                        var35 = 90;
                        break;
                    case 2:
                        var35 = 180;
                        break;
                    case 3:
                        var35 = 270;
                        break;
                    default:
                        Log.e("MyCameraSource", (new StringBuilder(31)).append("Bad rotation value: ").append(var7).toString());
                }

                Camera.CameraInfo var36 = new Camera.CameraInfo();
                Camera.getCameraInfo(var1, var36);
                int var38;
                if (var36.facing == 1) {
                    var38 = (var36.orientation + var35) % 360;
                    var39 = (360 - var38) % 360;
                } else {
                    var39 = var38 = (var36.orientation - var35 + 360) % 360;
                }

                this.rotation = var38 / 90;
                var2.setDisplayOrientation(var39);
                var6.setRotation(var38);
                if (this.zzm != null) {
                    if (var6.getSupportedFocusModes().contains(this.zzm)) {
                        var6.setFocusMode(this.zzm);
                    } else {
                        Log.w("MyCameraSource", String.format("FocusMode %s is not supported on this device.", this.zzm));
                        this.zzm = null;
                    }
                }

                if (this.zzm == null && this.zzl) {
                    if (var6.getSupportedFocusModes().contains("continuous-video")) {
                        var6.setFocusMode("continuous-video");
                        this.zzm = "continuous-video";
                    } else {
                        Log.i("MyCameraSource", "Camera auto focus is not supported on this device.");
                    }
                }

                var2.setParameters(var6);
                var2.setPreviewCallbackWithBuffer(new zza());
                var2.addCallbackBuffer(this.zza(this.zzh));
                var2.addCallbackBuffer(this.zza(this.zzh));
                var2.addCallbackBuffer(this.zza(this.zzh));
                var2.addCallbackBuffer(this.zza(this.zzh));
                return var2;
            }
        }
    }

    @SuppressLint({"InlinedApi"})
    private final byte[] zza(Size var1) {
        int var2 = ImageFormat.getBitsPerPixel(17);
        byte[] var3;
        ByteBuffer var4;
        if ((var4 = ByteBuffer.wrap(var3 = new byte[(int)Math.ceil((double)((long)(var1.getHeight() * var1.getWidth() * var2)) / 8.0D) + 1])).hasArray() && var4.array() == var3) {
            this.zzr.put(var3, var4);
            return var3;
        } else {
            throw new IllegalStateException("Failed to create valid buffer for camera source.");
        }
    }

    class zzb implements Runnable {
        private Detector<?> zzt;
        private long zzv = SystemClock.elapsedRealtime();
        private final Object lock = new Object();
        private boolean zzw = true;
        private long zzx;
        private int zzy = 0;
        private ByteBuffer zzz;

        zzb(Detector<?> var2) {
            this.zzt = var2;
        }

        @SuppressLint({"Assert"})
        final void release() {
            this.zzt.release();
            this.zzt = null;
        }

        final void setActive(boolean var1) {
            synchronized(this.lock) {
                this.zzw = var1;
                this.lock.notifyAll();
            }
        }

        final void zza(byte[] var1, Camera var2) {
            synchronized(this.lock) {
                if (this.zzz != null) {
                    var2.addCallbackBuffer(this.zzz.array());
                    this.zzz = null;
                }

                if (!MyCameraSource.this.zzr.containsKey(var1)) {
                    Log.d("MyCameraSource", "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.");
                } else {
                    this.zzx = SystemClock.elapsedRealtime() - this.zzv;
                    ++this.zzy;
                    this.zzz = (ByteBuffer) MyCameraSource.this.zzr.get(var1);
                    this.lock.notifyAll();
                }
            }
        }

        @SuppressLint({"InlinedApi"})
        public final void run() {
            while(true) {
                Frame var1;
                ByteBuffer var2;
                synchronized(this.lock) {
                    while(this.zzw && this.zzz == null) {
                        try {
                            this.lock.wait();
                        } catch (InterruptedException var13) {
                            Log.d("MyCameraSource", "Frame processing loop terminated.", var13);
                            return;
                        }
                    }

                    if (!this.zzw) {
                        return;
                    }

                    var1 = (new Frame.Builder()).setImageData(this.zzz, MyCameraSource.this.zzh.getWidth(), MyCameraSource.this.zzh.getHeight(), 17).setId(this.zzy).setTimestampMillis(this.zzx).setRotation(MyCameraSource.this.rotation).build();
                    var2 = this.zzz;
                    this.zzz = null;
                }

                try {
                    this.zzt.receiveFrame(var1);
                } catch (Exception var11) {
                    Log.e("MyCameraSource", "Exception thrown from receiver.", var11);
                } finally {
                    MyCameraSource.this.camera.addCallbackBuffer(var2.array());
                }
            }
        }
    }
    public byte[] data=null;
    public Camera selectCamera=null;
    class zza implements Camera.PreviewCallback {
        private zza() {
        }

        public final void onPreviewFrame(byte[] data, Camera camera) {
            selectCamera=camera;
            MyCameraSource.this.data=data;
            MyCameraSource.this.zzq.zza(data, camera);
        }
    }

    @VisibleForTesting
    static class zze {
        private Size zzac;
        private Size zzad;

        public zze(Camera.Size var1, @Nullable Camera.Size var2) {
            this.zzac = new Size(var1.width, var1.height);
            if (var2 != null) {
                this.zzad = new Size(var2.width, var2.height);
            }

        }

        public final Size zzb() {
            return this.zzac;
        }

        @Nullable
        public final Size zzc() {
            return this.zzad;
        }
    }

    class zzd implements Camera.PictureCallback {
        private PictureCallback zzab;

        private zzd() {
        }

        public final void onPictureTaken(byte[] var1, Camera var2) {
            if (this.zzab != null) {
                this.zzab.onPictureTaken(var1);
            }

            synchronized(MyCameraSource.this.zzf) {
                if (MyCameraSource.this.camera != null) {
                    MyCameraSource.this.camera.startPreview();
                }

            }
        }
    }

    static class zzc implements Camera.ShutterCallback {
        private ShutterCallback zzaa;

        private zzc() {
        }

        public final void onShutter() {
            if (this.zzaa != null) {
                this.zzaa.onShutter();
            }

        }
    }

    public interface PictureCallback {
        void onPictureTaken(byte[] var1);
    }

    public interface ShutterCallback {
        void onShutter();
    }

    public static class Builder {
        private final Detector<?> zzt;
        private MyCameraSource zzu = new MyCameraSource();

        public Builder(Context var1, Detector<?> var2) {
            if (var1 == null) {
                throw new IllegalArgumentException("No context supplied.");
            } else if (var2 == null) {
                throw new IllegalArgumentException("No detector supplied.");
            } else {
                this.zzt = var2;
                this.zzu.zze = var1;
            }
        }

        public Builder setRequestedFps(float var1) {
            if (var1 <= 0.0F) {
                throw new IllegalArgumentException((new StringBuilder(28)).append("Invalid fps: ").append(var1).toString());
            } else {
                this.zzu.zzi = var1;
                return this;
            }
        }

        public Builder setRequestedPreviewSize(int var1, int var2) {
            if (var1 > 0 && var1 <= 1000000 && var2 > 0 && var2 <= 1000000) {
                this.zzu.zzj = var1;
                this.zzu.zzk = var2;
                return this;
            } else {
                throw new IllegalArgumentException((new StringBuilder(45)).append("Invalid preview size: ").append(var1).append("x").append(var2).toString());
            }
        }

        public Builder setFacing(int var1) {
            if (var1 != 0 && var1 != 1) {
                throw new IllegalArgumentException((new StringBuilder(27)).append("Invalid camera: ").append(var1).toString());
            } else {
                this.zzu.facing = var1;
                return this;
            }
        }

        public Builder setAutoFocusEnabled(boolean var1) {
            this.zzu.zzl = var1;
            return this;
        }

        public Builder setFocusMode(String var1) {
            if (!var1.equals("continuous-video") && !var1.equals("continuous-picture")) {
                Log.w("MyCameraSource", String.format("FocusMode %s is not supported for now.", var1));
                var1 = null;
            }

            this.zzu.zzm = var1;
            return this;
        }

        public MyCameraSource build() {
            this.zzu.zzq = this.zzu.new zzb(this.zzt);
            return this.zzu;
        }
    }



}
