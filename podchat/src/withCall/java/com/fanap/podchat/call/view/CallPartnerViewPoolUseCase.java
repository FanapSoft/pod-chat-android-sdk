package com.fanap.podchat.call.view;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.chat.CallPartnerViewManager;

import java.util.List;


public interface CallPartnerViewPoolUseCase {


    interface ClientUseCase {

        void addView(@NonNull CallPartnerView... partnerView);

        void setAutoGenerate(boolean isAutoGenerate);

        void setAsScreenShareView(@NonNull CallPartnerView screenShareView);

        void setAsCameraPreview(@NonNull CallPartnerView cameraPreview);

        CallPartnerView getScreenShareView();

        CallPartnerView getPartnerAssignedView(Long partnerUserId);

        @Nullable
        CallPartnerView getPartnerUnAssignedView(Long partnerUserId);

        void setAutoGenerateCallback(CallPartnerViewManager.IAutoGenerate callback);

        default void addView(List<CallPartnerView> partnerViews) {}
        @MainThread
        default void showMuteIcon(Long partnerUserId) {}
        @MainThread
        default void hideMuteIcon(Long partnerUserId) {}
        @MainThread
        default void showCameraIsOff(Long partnerUserId) {}
        @MainThread
        default void hideCameraIsOff(Long partnerUserId) {}
        @MainThread
        default void releasePartnerView(Long partnerUserId){}
        @MainThread
        default void releaseScreenShareView(){}
        @MainThread
        default void showPartnerName(Long userId, String name){}
    }

    interface ChatUseCase {

        CallPartnerView assignViewToPartnerByUserId(Long partnerUserId);

        CallPartnerView assignScreenShareView();

        boolean unAssignViewFromPartnerByUserId(Long partnerUserId);

        CallPartnerView getPartnerAssignedView(Long partnerUserId);

        CallPartnerView getScreenShareView();


        boolean unAssignAll();

        void setAsScreenShareView(@NonNull CallPartnerView screenShareView);

        void setAsCameraPreview(@NonNull CallPartnerView cameraPreview);

        void unAssignScreenShareView();

        void hideAllAssignedViews();

        void showAllAssignedViews();

    }


}
