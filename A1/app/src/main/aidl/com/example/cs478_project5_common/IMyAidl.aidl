// IMyAidl.aidl
package com.example.cs478_project5_common;

// Declare any non-default types here with import statements

interface IMyAidl {
   void play(int i);
   void pause();
   void stop();
   void resume();
   void stopService();
   Bitmap sendImage(int i);
}
