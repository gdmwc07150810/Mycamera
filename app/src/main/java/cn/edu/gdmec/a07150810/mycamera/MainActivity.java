package cn.edu.gdmec.a07150810.mycamera;



import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback{
    private SurfaceView surfaceView;
    private ImageView imageView;
    private SurfaceHolder surfaceHolder;
    private ImageView imageView2;
    private Camera camera = null;
    private boolean aBoolean;

    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.camera);
        imageView = (ImageView) findViewById(R.id.image);
        imageView2 = (ImageView) findViewById(R.id.shutter);
        imageView2.setOnClickListener(this);
        imageView.setVisibility(View.GONE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCameraParams();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            if(aBoolean){
                camera.stopPreview();
            }
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            aBoolean=true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera!=null){
            camera.startPreview();
            aBoolean=false;
            camera.release();;
            camera=null;
        }
    }

    @Override
    public void onClick(View v) {
        if(aBoolean){
            imageView2.setEnabled(false);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.takePicture(mShutterCallback,null,mPictureCallback);
                }
            });
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if(data!=null){
                savaAndShow(data);
            }
        }
    };

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback(){

        @Override
        public void onShutter() {
            System.out.println("1111");
        }
    };


    public void setCameraParams(){
        if(camera!=null){
            return;
        }
        camera =Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setPreviewFrameRate(3);
        params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        params.set("jpeg-quality",85);

        List<Camera.Size> list = params.getSupportedPictureSizes();
        Camera.Size size = list.get(0);
        int w = size.width;
        int h = size.height;

        params.setPictureSize(w,h);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"重拍");
        menu.add(0,2,0,"打开相册");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return true;
        }else if(item.getItemId()==2){
            Intent intent = new Intent(this,AlbumActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void  savaAndShow(byte[] data){
        try {
            String imgeID = System.currentTimeMillis()+"";
            String pathName = android.os.Environment.getExternalStorageDirectory().getPath()+"/mycamera";
            File file = new File(pathName);
            if(!file.exists()){
                file.mkdirs();
            }
            pathName+="/"+imgeID+".jpeg";
            file = new File(pathName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            AlbumActivity album = new AlbumActivity();
            bitmap = album.loadImage(pathName);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            if(aBoolean){
                camera.stopPreview();
                aBoolean=false;
            }
            imageView2.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
