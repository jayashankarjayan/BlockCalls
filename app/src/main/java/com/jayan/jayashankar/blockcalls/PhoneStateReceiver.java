package com.jayan.jayashankar.blockcalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.jayan.jayashankar.blockcalls.telephony.LocalDatabase;

import java.util.List;

/**
 * Created by Jayashankar Jayan on 5/27/2018.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    LocalDatabase localDatabase;
    List blockednumbers;
    @Override
    public void onReceive(Context context, Intent intent) {

        localDatabase = new LocalDatabase(context);
        blockednumbers = localDatabase.getBlockedNumbers();

        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            Toast.makeText(context,"Ringing number is -"+incomingNumber,Toast.LENGTH_SHORT).show();

            for (int i=0;i<blockednumbers.size();i++)
            {
                if(blockednumbers.get(i).equals(incomingNumber))
                {
                    /* Mute the phone*/
                    AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager != null) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            }
        }
        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
            /*Toast.makeText(context,"Received State",Toast.LENGTH_SHORT).show();*/
        }
        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_IDLE)
                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_OFFHOOK)) {

            /* Unmute the phone*/
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = 0;
            if (audioManager != null) {
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            }

            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        }
    }
}
