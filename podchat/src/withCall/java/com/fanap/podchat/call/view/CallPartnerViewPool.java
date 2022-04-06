package com.fanap.podchat.call.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.chat.CallPartnerViewManager;
import com.fanap.podchat.chat.MainThreadExecutor;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CallPartnerViewPool implements CallPartnerViewPoolUseCase.ChatUseCase, CallPartnerViewPoolUseCase.ClientUseCase {


    public static final Long SCREEN_SHARE_ID = 10010101L;

    private CallPartnerViewManager.IAutoGenerate autoGenerateCallback;

    @Nullable
    ConcurrentLinkedQueue<CallPartnerView> partnerViewsPool;
    @Nullable
    private Map<Long, CallPartnerView> userIdToViewMap;
    @Nullable
    private Map<Long, CallPartnerView> userIdToUnAssignedViewMap;


    @Nullable
    private CallPartnerView cameraPreview;

    @Nullable
    private CallPartnerView screenShareView;

    private boolean autoGenerate = true;

    public CallPartnerViewPool() {
    }

    /*
    Client Use Cases Implementation
     */


    @Override
    public void addView(@NonNull CallPartnerView... partnerView) {
        checkIsListInitialized();
        Objects.requireNonNull(partnerViewsPool).addAll(Arrays.asList(partnerView));
    }

    @Override
    public void setAutoGenerate(boolean isAutoGenerate) {
        autoGenerate = isAutoGenerate;
    }

    public boolean isAutoGenerateEnable() {
        return autoGenerate;
    }

    /*
    Chat Use Cases Implementation
     */

    @Override
    public CallPartnerView assignViewToPartnerByUserId(Long partnerUserId) {
        checkIsMapInitialized();
        if (!partnerHasView(partnerUserId)) {
            CallPartnerView unAssignedViewFromList = getUnAssignedView();
            if (unAssignedViewFromList != null) {
                unAssignedViewFromList.setPartnerId(partnerUserId);
                getValidUserIdToViewMap().put(partnerUserId, unAssignedViewFromList);
                return unAssignedViewFromList;
            } else if (autoGenerate) {
                return generatePartnerView(partnerUserId);
            }
        }
        return null;
    }


    @Override
    public CallPartnerView assignScreenShareView() throws NullPointerException {
        if (screenShareView != null)
            return screenShareView;

        checkIsMapInitialized();
        if (viewListIsNotEmpty()) {
            CallPartnerView unAssignedViewFromList = getUnAssignedView();
            if (unAssignedViewFromList != null) {
                screenShareView = unAssignedViewFromList;
                screenShareView.setPartnerId(SCREEN_SHARE_ID);
                getValidUserIdToViewMap().put(SCREEN_SHARE_ID, screenShareView);
                return screenShareView;
            } else if (autoGenerate) {
                return autoGenerateScreenShareView();
            }
        } else if (autoGenerate) {
            return autoGenerateScreenShareView();
        }
        return null;
    }

    private CallPartnerView autoGenerateScreenShareView() {
        screenShareView = generatePartnerView();
        screenShareView.setPartnerId(SCREEN_SHARE_ID);
        getValidUserIdToViewMap().put(SCREEN_SHARE_ID, screenShareView);
        return screenShareView;
    }

    @Override
    @Nullable
    public CallPartnerView getScreenShareView() {
        return screenShareView;
    }

    @Override
    public boolean unAssignViewFromPartnerByUserId(Long partnerUserId) {
        checkIsMapInitialized();
        if (partnerHasView(partnerUserId)) {
            markPartnerViewAsUnAssigned(partnerUserId);
            getValidUserIdToViewMap().remove(partnerUserId);
            return true;
        }
        return false;
    }

    private void markPartnerViewAsUnAssigned(Long partnerUserId) {
        if (userIdToUnAssignedViewMap != null) {
            userIdToUnAssignedViewMap.put(partnerUserId, getPartnerAssignedView(partnerUserId));
        }
    }


    @Override
    public void setAsCameraPreview(@NonNull CallPartnerView cameraPreview) {
        this.cameraPreview = cameraPreview;
    }

    @Override
    public void unAssignScreenShareView() {
        checkIsMapInitialized();
        getValidUserIdToViewMap().remove(SCREEN_SHARE_ID);
    }


    @Override
    public void setAsScreenShareView(@NonNull CallPartnerView screenShareView) {
        this.screenShareView = screenShareView;
        checkIsMapInitialized();
        getValidUserIdToViewMap().put(SCREEN_SHARE_ID, screenShareView);
        checkIsListInitialized();
        Objects.requireNonNull(partnerViewsPool).remove(screenShareView);
    }

    @Override
    public boolean unAssignAll() {
        checkIsMapInitialized();
        if (userIdToUnAssignedViewMap != null) {
            userIdToUnAssignedViewMap.putAll(getValidUserIdToViewMap());
        }
        getValidUserIdToViewMap().clear();
        return true;
    }

    @Override
    public void hideAllAssignedViews() {
        checkIsMapInitialized();
        for (CallPartnerView view :
                getValidUserIdToViewMap().values()) {
            new MainThreadExecutor()
                    .execute(()->{
                        view.setVisibility(View.GONE);
                    });
        }
    }

    @Override
    public void showAllAssignedViews() {
        checkIsMapInitialized();
        for (CallPartnerView view :
                getValidUserIdToViewMap().values()) {
            new MainThreadExecutor()
                    .execute(()->{
                        if(view.getPartnerId()!=null && view.getPartnerId()>0){
                            view.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void checkIsListInitialized() {
        if (Util.isNullOrEmpty(partnerViewsPool)) {
            partnerViewsPool = new ConcurrentLinkedQueue<>();
        }
    }

    private void checkIsMapInitialized() {
        if (userIdToViewMap == null) {
            userIdToViewMap = new HashMap<>();
            userIdToUnAssignedViewMap = new HashMap<>();
        }
    }

    private boolean viewListIsNotEmpty() {
        return Util.isNotNullOrEmpty(partnerViewsPool);
    }

    private boolean partnerHasView(Long partnerUserId) {
        checkIsMapInitialized();
        return getValidUserIdToViewMap().containsKey(partnerUserId);
    }

    @Nullable
    private CallPartnerView getUnAssignedView() {
        Collection<CallPartnerView> assignedViewIds = getValidUserIdToViewMap().values();
        List<CallPartnerView> unAssigned = new ArrayList<>(partnerViewsPool);
        unAssigned.removeAll(assignedViewIds);
        if (Util.isNotNullOrEmpty(unAssigned))
            return unAssigned.get(0);

        return null;
    }


    @Nullable
    public CallPartnerView getPartnerAssignedView(Long partnerUserId) {
        checkIsMapInitialized();
        if (getValidUserIdToViewMap().containsKey(partnerUserId)) {
            return getValidUserIdToViewMap().get(partnerUserId);
        }
        return null;
    }

    @Nullable
    @Override
    public CallPartnerView getPartnerUnAssignedView(Long partnerUserId) {
        if (userIdToUnAssignedViewMap != null) {
            return userIdToUnAssignedViewMap.get(partnerUserId);
        }
        return null;
    }

    @Override
    public void setAutoGenerateCallback(CallPartnerViewManager.IAutoGenerate callback) {
        this.autoGenerateCallback = callback;
    }

    private CallPartnerView generatePartnerView(long partnerUserId) {
        CallPartnerView newPartnerView = null;
        try {
            newPartnerView = new CallPartnerView(getContextFromPartnerView());
            newPartnerView.setPartnerId(partnerUserId);
            checkIsListInitialized();
            addView(newPartnerView);
            checkIsMapInitialized();
            getValidUserIdToViewMap().put(partnerUserId, newPartnerView);
            callOnNewViewGenerated(newPartnerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPartnerView;
    }


    private CallPartnerView generatePartnerView() {
        CallPartnerView newPartnerView = null;
        try {
            newPartnerView = new CallPartnerView(getContextFromPartnerView());
            checkIsListInitialized();
            addView(newPartnerView);
            callOnNewViewGenerated(newPartnerView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPartnerView;
    }

    private Map<Long, CallPartnerView> getValidUserIdToViewMap() {
        if (userIdToViewMap == null) {
            throw new NullPointerException("userIdToViewMap==null");
        }
        return userIdToViewMap;
    }

    private Context getContextFromPartnerView() {
        if (Util.isNotNullOrEmpty(partnerViewsPool)) {
            CallPartnerView partnerView = partnerViewsPool.peek();
            if (partnerView != null)
                return partnerView.getContext();
        }
        if (screenShareView != null)
            return screenShareView.getContext();

        throw new NullPointerException("at list 1 view required in partner view list");
    }

    private void callOnNewViewGenerated(CallPartnerView newPartnerView) {
        if (autoGenerateCallback != null) {
            autoGenerateCallback.onNewViewGenerated(newPartnerView);
        }
    }
}