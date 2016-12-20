package cn.edu.gdmec.a07150810.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivity extends AppCompatActivity {
    private ViewFlipper viewFlipper;
    private Bitmap[] bitmaps;
    private  long startTime=0;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    public  String[] loadAlbum(){
        String pathName = android.os.Environment.getExternalStorageDirectory().getPath()+"/mycamera";
        File file = new File(pathName);
        Vector<Bitmap> fileName = new Vector<Bitmap>();
        if(file.exists()&&file.isDirectory()){
            String[] str = file.list();
            for(String s:str){
                if(new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));
                }
            }
            bitmaps=fileName.toArray(new Bitmap[]{});
        }
        return null;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        viewFlipper=(ViewFlipper)this.findViewById(R.id.viewFlipper);
        loadAlbum();
        if(bitmaps==null){
            Toast.makeText(this,"相册无照片",Toast.LENGTH_LONG).show();
            finish();
            return;
        }else{
            for(int i=0;i<=bitmaps.length-1;i++){
                viewFlipper.addView(addImage(bitmaps[i]),i,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        sensorManager= (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[SensorManager.DATA_X];
                if(x>10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_right_out));
                    viewFlipper.showPrevious();
                }else if(x<-10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_left_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,
                            R.anim.push_left_out));
                    viewFlipper.showNext();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
    }

    public Bitmap loadImage(String pathName){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName,options);
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        int screenWidth = display.getWidth();
        options.inSampleSize=options.outWidth/screenWidth;
        options.inJustDecodeBounds=false;
        bitmap = BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }

    public View addImage(Bitmap bitmap){
        ImageView img = new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
}
