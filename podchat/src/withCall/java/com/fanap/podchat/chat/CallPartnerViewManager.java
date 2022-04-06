package com.fanap.podchat.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;


import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.view.CallPartnerViewPool;
import com.fanap.podchat.call.view.CallPartnerViewPoolUseCase;

import java.util.List;

public class CallPartnerViewManager implements CallPartnerViewPoolUseCase.ClientUseCase {

    public interface IAutoGenerate {
        void onNewViewGenerated(CallPartnerView callPartnerView);
        void onMaximumViewNumberReached();
    }


    @NonNull
    private final CallPartnerViewPool pool;

    CallPartnerViewManager(@NonNull CallPartnerViewPool pool) {
        this.pool = pool;
    }


    @Override
    public void addView(List<CallPartnerView> partnerViews) {

        for (CallPartnerView partnerView :
                partnerViews) {
            addView(partnerView);
        }
    }

    @Override
    public void addView(@NonNull CallPartnerView... partnerView) {
        pool.addView(partnerView);
    }

    @Override
    public void setAutoGenerate(boolean isAutoGenerate) {
        pool.setAutoGenerate(isAutoGenerate);
    }

    @Override
    public void setViewGenerationMax(int viewGenerationMax) {
        pool.setViewGenerationMax(viewGenerationMax);
    }

    @Override
    public void setAsScreenShareView(@NonNull CallPartnerView screenShareView) {
        pool.setAsScreenShareView(screenShareView);
    }

    @Override
    public void setAsCameraPreview(@NonNull CallPartnerView cameraPreview) {
        pool.setAsCameraPreview(cameraPreview);
    }

    @Override
    public CallPartnerView getScreenShareView() {
        return pool.getScreenShareView();
    }

    @Nullable
    @Override
    public CallPartnerView getPartnerAssignedView(Long partnerUserId) {
        return pool.getPartnerAssignedView(partnerUserId);
    }

    @Nullable
    @Override
    public CallPartnerView getPartnerUnAssignedView(Long partnerUserId) {
        return pool.getPartnerUnAssignedView(partnerUserId);
    }

    @Override
    public void setAutoGenerateCallback(IAutoGenerate callback) {
        pool.setAutoGenerateCallback(new IAutoGenerate() {
            @Override
            public void onNewViewGenerated(CallPartnerView callPartnerView) {
                new MainThreadExecutor().execute(() -> callback.onNewViewGenerated(callPartnerView));
            }

            @Override
            public void onMaximumViewNumberReached() {
                callback.onMaximumViewNumberReached();
            }
        });
    }


    @UiThread
    @Override
    public void releasePartnerView(Long partnerUserId) {
        CallPartnerView partnerView = getPartnerUnAssignedView(partnerUserId) != null?getPartnerUnAssignedView(partnerUserId) : getPartnerAssignedView(partnerUserId);
        if (partnerView != null) {
            partnerView.setVisibility(View.GONE);
            partnerView.setPartnerId(CallPartnerViewPool.NOT_ASSIGNED);
            partnerView.setPartnerName("");
            partnerView.setDisplayName(false);
            partnerView.setDisplayIsMuteIcon(false);
            partnerView.setDisplayCameraIsOffIcon(false);
            partnerView.reset();
        }
    }

    @UiThread
    @Override
    public void releaseScreenShareView() {
        CallPartnerView partnerView = getScreenShareView();
        if (partnerView != null) {
            partnerView.setVisibility(View.GONE);
            partnerView.setId(0);
            partnerView.setPartnerName("");
            partnerView.setDisplayName(false);
            partnerView.setDisplayIsMuteIcon(false);
            partnerView.setDisplayCameraIsOffIcon(false);
            partnerView.reset();
        }
    }

    @UiThread
    @Override
    public void showPartnerName(Long userId, String name) {
        CallPartnerView pw = getPartnerAssignedView(userId);
        if (pw != null) {
            pw.setDisplayName(true);
            pw.setPartnerName(name);
        }
    }

    @UiThread
    @Override
    public void showMuteIcon(Long partnerUserId) {
        CallPartnerView view = getPartnerAssignedView(partnerUserId);
        if (view != null) {
            view.setDisplayIsMuteIcon(true);
        }
    }

    @UiThread
    @Override
    public void hideMuteIcon(Long partnerUserId) {
        CallPartnerView view = getPartnerAssignedView(partnerUserId);
        if (view != null) {
            view.setDisplayIsMuteIcon(false);
        }
    }

    @UiThread
    @Override
    public void showCameraIsOff(Long partnerUserId) {
        CallPartnerView view = getPartnerAssignedView(partnerUserId);
        if (view != null) {
            view.setDisplayCameraIsOffIcon(true);
        }
    }

    @UiThread
    @Override
    public void hideCameraIsOff(Long partnerUserId) {
        CallPartnerView view = getPartnerAssignedView(partnerUserId);
        if (view != null) {
            view.setDisplayCameraIsOffIcon(false);
        }
    }


}
