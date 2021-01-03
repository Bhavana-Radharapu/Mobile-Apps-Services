package com.example.cs478_project5_app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.cs478_project5_common.IMyAidl;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView playlist;
    ListView imglist;
    ImageButton pause;
    ImageButton resume;
    ImageButton stop;
    Button startService;
    Button stopService;
    ImageView image;
    TextView messages;
    Intent intent;

    private boolean mIsBound = false;
    private IMyAidl mKeyGeneratorService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        playlist = (ListView) findViewById( R.id.playlist );
        imglist = (ListView) findViewById( R.id.image_view );
        pause = (ImageButton) findViewById( R.id.pause_button );
        resume = (ImageButton) findViewById( R.id.play_button );
        stop = (ImageButton) findViewById( R.id.stop_button );
        startService = (Button) findViewById( R.id.startService );
        stopService = (Button) findViewById( R.id.stopService );
        image = (ImageView) findViewById( R.id.imgView );
        messages = (TextView) findViewById( R.id.comments ) ;

        pause.setEnabled( false );
        resume.setEnabled( false );
        stop.setEnabled( false );
        startService.setEnabled( true );
        stopService.setEnabled( false );


        //Choose the song from playlist
        playlist.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println( "playlist" + i );

                if (mIsBound) {
                    pause.setEnabled( true );
                    resume.setEnabled( true );
                    stop.setEnabled( true );
                    startService.setEnabled( false );
                    stopService.setEnabled( true );
                    messages.setText( "Service is bound !" );
                    try {
                        mKeyGeneratorService.play( i + 1);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    pause.setEnabled( false );
                    resume.setEnabled( false );
                    stop.setEnabled( false );
                    startService.setEnabled( true );
                    stopService.setEnabled( false );
                    messages.setText( "Service not bound !" );
                    Log.i( TAG, "Ugo says that the service was not bound!" );
                }
            }
        } );


        // chooses the image
        imglist.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println( "image" + i );

                if (mIsBound) {

                    startService.setEnabled( false );
                    stopService.setEnabled( true );
                    messages.setText( "Service is bound !" );
                    try {
                        Bitmap img = mKeyGeneratorService.sendImage( i + 1);
                        image.setImageBitmap(img);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    pause.setEnabled( false );
                    resume.setEnabled( false );
                    stop.setEnabled( false );
                    startService.setEnabled( true );
                    stopService.setEnabled( false );
                    messages.setText( "Service not bound !" );
                    Log.i( TAG, "Ugo says that the service was not bound!" );
                }
            }
        } );

        //pauses the music
        pause.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsBound) {
                    pause.setEnabled( false );
                    resume.setEnabled( true );
                    stop.setEnabled( true );
                    startService.setEnabled( false );
                    stopService.setEnabled( true );
                    messages.setText( "Service is bound !" );
                    if (mKeyGeneratorService != null) {
                        try {
                            mKeyGeneratorService.pause();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    pause.setEnabled( false );
                    resume.setEnabled( false );
                    stop.setEnabled( false );
                    startService.setEnabled( true );
                    stopService.setEnabled( false );
                    messages.setText( "Service not bound !" );
                    Log.i( TAG, "Ugo says that the service was not bound! " );
                }
            }
        } );

        // resumes the music
        resume.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsBound) {
                    pause.setEnabled( true );
                    resume.setEnabled( false );
                    stop.setEnabled( true );
                    startService.setEnabled( false );
                    stopService.setEnabled( true );
                    messages.setText( "Service is bound !" );
                    if (mKeyGeneratorService != null) {
                        try {
                            mKeyGeneratorService.resume();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    pause.setEnabled( false );
                    resume.setEnabled( false );
                    stop.setEnabled( false );
                    startService.setEnabled( true );
                    stopService.setEnabled( false );
                    messages.setText( "Service not bound !" );
                    Log.i( TAG, "Ugo says that the service was not bound!" );
                }
            }
        } );

        //The stop button stops the playback
        stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsBound) {

                    unbindService( mConnection ); //playback stops automatically upon call
                    mKeyGeneratorService = null;
                    mIsBound = false;
                    if (mIsBound) {
                        pause.setEnabled( false );
                        resume.setEnabled( false );
                        stop.setEnabled( false );
                        startService.setEnabled( false );
                        stopService.setEnabled( true );
                        messages.setText( "Service is bound !" );
                        Log.i( "BINDING", "M is Bounded after unbind" );
                    } else {
                        pause.setEnabled( false );
                        resume.setEnabled( false );
                        stop.setEnabled( false );
                        startService.setEnabled( true );
                        stopService.setEnabled( false );
                        messages.setText( "Service not bound !" );
                        Log.i( "BINDING", "M is NOT Bounded after unbind" );
                    }
                }
            }
        } );
    }


    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {

            mKeyGeneratorService = IMyAidl.Stub.asInterface(iservice);

            mIsBound = true;

        }

        public void onServiceDisconnected(ComponentName className) {

            mKeyGeneratorService = null;

            mIsBound = false;

        }
    };

    // start service starts the bounds service
    public void startService(View view) {
        if (!mIsBound) {

            boolean b = false;
            intent = new Intent("com.example.cs478_project5_common.IMyAldi");

            // UB:  Stoooopid Android API-21 no longer supports implicit intents
            // to bind to a service #@%^!@..&**!@
            // Must make intent explicit or lower target API level to 20.

            ResolveInfo info = getPackageManager().resolveService(intent, 0);
            intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(intent);
            }

            if (b) {
                startService.setEnabled( false );
                stopService.setEnabled( true );
                messages.setText( "Service is bound" );
                Log.i(TAG, "Ugo says bindService() succeeded!");
            } else {
                pause.setEnabled( false );
                resume.setEnabled( false );
                stop.setEnabled( false );
                startService.setEnabled( true );
                stopService.setEnabled( false );

                messages.setText( "Service not bound" );
                Log.i(TAG, "Ugo says bindService() failed!");
            }
        }
    }

    // Unbounds the service
    public void stopService(View view) {

        pause.setEnabled( false );
        resume.setEnabled( false );
        stop.setEnabled( false );
        startService.setEnabled( true );
        stopService.setEnabled( false );

        stopService(intent);
        unbindService(mConnection);
        mKeyGeneratorService = null;
        mIsBound = false;
        messages.setText( "Service is not bound" );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Destroying the service
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mKeyGeneratorService = null;
        mIsBound = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Unbound the service when closed
    @Override
    protected void onPause() {
        mIsBound = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}