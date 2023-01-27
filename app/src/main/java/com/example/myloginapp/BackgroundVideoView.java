package com.example.myloginapp;



import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.myloginapp.R;

import java.io.IOException;

public class BackgroundVideoView extends SurfaceView implements SurfaceHolder.Callback {

    private MediaPlayer mp;
    private Boolean isStarted = false;


    public BackgroundVideoView(Context context) {
        super(context);
        init();
    }

    public BackgroundVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BackgroundVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mp = new MediaPlayer();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.backgroundone);
        try {
               if(!isStarted){
                        isStarted =true;

                       mp.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor
                               .getStartOffset(), assetFileDescriptor.getLength());
                   }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
                           layoutParams.height = getHeight();
                           layoutParams.width = getWidth();
                           setLayoutParams(layoutParams);

            mp.prepare();
            mp.setDisplay(getHolder());
            mp.setLooping(true);
            mp.start();
        }catch (IOException e) {
                       throw new RuntimeException(e);
                   }

               }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mp.stop();

    }
}
