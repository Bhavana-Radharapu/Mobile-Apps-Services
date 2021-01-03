package com.example.cs478_project5_app1;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.cs478_project5_common.IMyAidl;

public class MyService extends Service {
    private final static Set<UUID> mIDs = new HashSet<UUID>();
    MediaPlayer mPlayer;
    private int x = 0;

    private final IMyAidl.Stub mBinder = new IMyAidl.Stub() {

        // Implement the remote method
        public String[] getKey() {

            UUID id;

            // Acquire lock to ensure exclusive access to mIDs
            // Then examine and modify mIDs

            synchronized (mIDs) {

                do {

                    id = UUID.randomUUID();

                } while (mIDs.contains(id));

                mIDs.add(id);
            }
            String[] s;
            s = new String[]{ id.toString()};
            Log.i("Ugo says", "String is: " + s[0]) ;
            return s;
        }

        @Override
        public void play(int i) {
            if (null != mPlayer) {
                mPlayer.stop();
            }
            //plays different songs
            switch (i){
                case 1: mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song3 ); break;
                case 2: mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song2); break;
                case 3: mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song1 ); break;
                default: return;
            }

            if (null != mPlayer) {

                if (mPlayer.isPlaying()) {

                    // Rewind to beginning of song
                    mPlayer.seekTo(0);

                } else {

                    // Start playing song
                    mPlayer.start();

                }

            }
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    mPlayer.stop();
                    x = mPlayer.getDuration();
                }
            });
        }

        @Override
        public void pause() {
            if(mPlayer.isPlaying()){
                x = mPlayer.getCurrentPosition();
                mPlayer.pause();
            }
        }

        // stops complete playback
        @Override
        public void stop()  {
            mPlayer.stop();
        }

        @Override
        public void resume()  {
            if (x == mPlayer.getDuration()){
                return;
            }
            if(!mPlayer.isPlaying()){
                mPlayer.seekTo(x);
                mPlayer.start();
            }
        }

        @Override
        public void stopService() {
            stopSelf();
        }

        @Override
        public Bitmap sendImage(int i)  {
            Bitmap bm = null;
            //view the images
            switch (i){
                case 1: bm = BitmapFactory.decodeResource(getResources(), R.drawable.img1); break;
                case 2: bm = BitmapFactory.decodeResource(getResources(), R.drawable.img2); break;
                case 3: bm = BitmapFactory.decodeResource(getResources(), R.drawable.img3); break;
                default:
                    return bm;
            }
            return bm;
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
