package com.fanap.podchat.call.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.camera.CameraId;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.screenshare.model.ScreenShareParam;
import com.fanap.podchat.example.R;


public class SettingFragment extends Fragment {

    public interface ISettingFragment {

        void onSettingChanged(VideoCallParam videoCallParam,
                              AudioCallParam audioCallParam,
                              ScreenShareParam screenShareParam);
    }


    private ISettingFragment mCallback;

    RadioButton rbVideoSize720,
            rbVideoSize320,
            rbVideoSize144,
            rbVideoFPS15,
            rbVideoFPS30,
            rbDefaultCameraFront,
            rbDefaultCameraBack,
            rbVideoEncodeBitrate50,
            rbVideoEncodeBitrate90,
            rbVideoEncodeBitrate150,
            rbVideoEncodeBitrate300;

    RadioButton rbAudioFrameSize5,
            rbAudioFrameSize10,
            rbAudioFrameSize20,
            rbAudioFrameSize40,
            rbAudioFrameSize60,
            rbAudioFrameRate8,
            rbAudioFrameRate12,
            rbAudioFrameRate16,
            rbAudioBitrate8,
            rbAudioBitrate12,
            rbAudioBitrate16,
            rbAudioChannels1,
            rbAudioChannels2;

    RadioButton rbScreenShareLowQuality,
            rbScreenShareHighQuality;

    Button btnUpdateSettings;

    @Override
    public void onAttach(Context context) {
        mCallback = (ISettingFragment) context;
        super.onAttach(context);
    }

    public SettingFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initialViews(view);
        setListeners(view);
        return view;
    }

    private void setListeners(View view) {
        btnUpdateSettings.setOnClickListener(v -> {

            catchSelectedSettings();


        });
    }

    private void catchSelectedSettings() {
        //Video

        //Size
        int selectedWidth = 720;
        int selectedHeight = 480;

        if (rbVideoSize144.isChecked()) {
            selectedWidth = 144;
            selectedHeight = 176;

        }
        if (rbVideoSize320.isChecked()) {
            selectedWidth = 320;
            selectedHeight = 240;
        }

        Pair<Integer, Integer> size = new Pair<>(selectedWidth,
                selectedHeight);

        //FPS

        int selectedFPS = 30;

        if (rbVideoFPS15.isChecked()) {
            selectedFPS = 15;
        }


        //Default Camera

        int selectedCameraId = CameraId.FRONT;

        if (rbDefaultCameraBack.isChecked()) {
            selectedCameraId = CameraId.BACK;
        }

        //Encode Bitrate
        int selectedBitrate = 50;
        if (rbVideoEncodeBitrate90.isChecked()) {
            selectedBitrate = 90;
        }
        if (rbVideoEncodeBitrate150.isChecked()) {
            selectedBitrate = 150;
        }
        if (rbVideoEncodeBitrate300.isChecked()) {
            selectedBitrate = 300;
        }

        VideoCallParam videoCallParam =
                new VideoCallParam.Builder(null)
                .setCamWidth(size.first)
                .setCamHeight(size.second)
                .setBitrate(selectedBitrate * 1000)
                .setCameraId(selectedCameraId)
                .setCamFPS(selectedFPS)
                .build();


        //Audio

        //Frame Size in ms
        int selectedFrameSize = 60;
        if (rbAudioFrameSize5.isChecked()) {
            selectedFrameSize = 5;
        }
        if (rbAudioFrameSize10.isChecked()) {
            selectedFrameSize = 10;
        }
        if (rbAudioFrameSize20.isChecked()) {
            selectedFrameSize = 20;
        }
        if (rbAudioFrameSize40.isChecked()) {
            selectedFrameSize = 40;
        }
        if (rbAudioFrameSize60.isChecked()) {
            selectedFrameSize = 60;
        }

        //frame rate
        int selectedAudioFrameRate = 8;
        if (rbAudioFrameRate12.isChecked()) {
            selectedAudioFrameRate = 12;
        }
        if (rbAudioFrameRate16.isChecked()) {
            selectedAudioFrameRate = 16;
        }

        //bitrate
        int selectedAudioBitrate = 8;

        if (rbAudioBitrate12.isChecked()) {
            selectedAudioBitrate = 12;
        }
        if (rbAudioBitrate16.isChecked()) {
            selectedAudioBitrate = 16;
        }

        //num of channels
        int selectedChannels = 1;
        if (rbAudioChannels2.isChecked()) {
            selectedChannels = 2;
        }

        AudioCallParam audioCallParam =
                new AudioCallParam.Builder()
                        .setFrameSize(selectedFrameSize)
                        .setFrameRate(selectedAudioFrameRate * 1000)
                        .setBitrate(selectedAudioBitrate * 1000)
                        .setNumberOfChannels(selectedChannels)
                        .build();

        //Screen Share

        int selectedScreenShareQuality = 0;
        if (rbScreenShareHighQuality.isChecked()){
            selectedScreenShareQuality=1;
        }

        ScreenShareParam.Builder screenShareParamBuilder =
                new ScreenShareParam.Builder();

        if(selectedScreenShareQuality==0){
            screenShareParamBuilder.setLowQuality();
        }else screenShareParamBuilder.setHighQuality();


        mCallback.onSettingChanged(
                videoCallParam,
                audioCallParam,
                screenShareParamBuilder.build()
        );




    }

    private void initialViews(View view) {
        rbVideoSize720 = view.findViewById(R.id.rb720);
        rbVideoSize320 = view.findViewById(R.id.rb320);
        rbVideoSize144 = view.findViewById(R.id.rb144);
        rbVideoFPS15 = view.findViewById(R.id.rbFPS15);
        rbVideoFPS30 = view.findViewById(R.id.rbFPS30);
        rbDefaultCameraFront = view.findViewById(R.id.rbDefaultCamFront);
        rbDefaultCameraBack = view.findViewById(R.id.rbDefaultCamBack);
        rbVideoEncodeBitrate50 = view.findViewById(R.id.rbBitrate50);
        rbVideoEncodeBitrate90 = view.findViewById(R.id.rbBitrate90);
        rbVideoEncodeBitrate150 = view.findViewById(R.id.rbBitrate150);
        rbVideoEncodeBitrate300 = view.findViewById(R.id.rbBitrate300);

        rbAudioFrameSize5 = view.findViewById(R.id.rbAudioFrameSize5);
        rbAudioFrameSize10 = view.findViewById(R.id.rbAudioFrameSize10);
        rbAudioFrameSize20 = view.findViewById(R.id.rbAudioFrameSize20);
        rbAudioFrameSize40 = view.findViewById(R.id.rbAudioFrameSize40);
        rbAudioFrameSize60 = view.findViewById(R.id.rbAudioFrameSize60);
        rbAudioFrameRate8 = view.findViewById(R.id.rbAudioFrameRate8);
        rbAudioFrameRate12 = view.findViewById(R.id.rbAudioFrameRate12);
        rbAudioFrameRate16 = view.findViewById(R.id.rbAudioFrameRate16);
        rbAudioBitrate8 = view.findViewById(R.id.rbAudioBitRate8);
        rbAudioBitrate12 = view.findViewById(R.id.rbAudioBitRate12);
        rbAudioBitrate16 = view.findViewById(R.id.rbAudioBitRate16);
        rbAudioChannels1 = view.findViewById(R.id.rbAudioChannels1);
        rbAudioChannels2 = view.findViewById(R.id.rbAudioChannels2);

        rbScreenShareLowQuality = view.findViewById(R.id.rbScreenShareLowQuality);
        rbScreenShareHighQuality = view.findViewById(R.id.rbScreenShareHighQuality);

        btnUpdateSettings = view.findViewById(R.id.btnUpdateSettings);
    }
}