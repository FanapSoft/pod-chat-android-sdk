package com.fanap.podchat.chat;

import com.fanap.podcall.IPodCall;
import com.fanap.podcall.PartnerType;
import com.fanap.podcall.PodCall;
import com.fanap.podcall.PodCallBuilder;
import com.fanap.podcall.PodCallV2;
import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.kafka.KafkaConfig;
import com.fanap.podcall.model.CallPartner;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.CallAsyncRequestsManager;
import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.audio_call.CallServiceManager;
import com.fanap.podchat.call.audio_call.ICallState;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.call.request_model.GetCallParticipantsRequest;
import com.fanap.podchat.call.request_model.MuteUnMuteCallParticipantRequest;
import com.fanap.podchat.call.request_model.RejectCallRequest;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.request_model.TurnCallParticipantVideoOffRequest;
import com.fanap.podchat.call.result_model.CallCancelResult;
import com.fanap.podchat.call.result_model.CallCreatedResult;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.GetCallParticipantResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.call.result_model.StartedCallModel;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chat extends ChatCore {

    private List<CallPartnerView> videoCallPartnerViews;
    private CallPartnerView localPartnerView;
    private PodCallV2 podVideoCall;
    private CallServiceManager callServiceManager;

    public void setupCall(VideoCallParam videoCallParam, AudioCallParam audioCallParam, CallConfig callConfig, List<CallPartnerView> remoteViews) {

        this.localPartnerView = videoCallParam.getCameraPreview();

        this.videoCallPartnerViews = new ArrayList<>(remoteViews);

        callServiceManager = new CallServiceManager(context, callConfig);

        podVideoCall = new PodCallBuilder(context, new IPodCall() {
            @Override
            public void onError(String s) {
                captureError(new PodChatException(s, 6012));
            }

            @Override
            public void onEvent(String s) {
                showLog(s);
            }

            @Override
            public void onCameraReady(PodCall podCall) {
                showLog("Camera Call is ready");
            }

            @Override
            public void onCameraReady(PodCallV2 podCallV2) {
                showLog("Camera Call is ready");
            }
        })
                .setVideoCallParam(videoCallParam)
                .setAudioCallParam(audioCallParam)
                .buildV2();

        podVideoCall.initial();


    }

    public String requestCall(CallRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createCallRequestMessage(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.CALL_REQUEST, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_NEW_CALL");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String getCallParticipants(GetCallParticipantsRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            try {
                String message = CallAsyncRequestsManager.createGetActiveCallParticipantsMessage(request, uniqueId);
                setCallBacks(false, false, false, true, ChatMessageType.Constants.GET_ACTIVE_CALL_PARTICIPANTS, null, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_GET_ACTIVE_CALL_PARTICIPANTS");
            } catch (PodChatException e) {
                captureError(e);
            }
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String requestGroupCall(CallRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createGroupCallRequestMessage(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.GROUP_CALL_REQUEST, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_NEW_GROUP_CALL");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String addGroupCallParticipant(RequestAddParticipants request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createAddCallParticipantMessage(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_ADD_CALL_PARTICIPANT");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String requestMuteCallParticipant(MuteUnMuteCallParticipantRequest request) {

        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                String message = CallAsyncRequestsManager.createMuteCallParticipantMessage(request, uniqueId);
                setCallBacks(false, false, false, true, ChatMessageType.Constants.MUTE_CALL_PARTICIPANT, null, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_MUTE_CALL_PARTICIPANT");
            } else {
                onChatNotReady(uniqueId);
            }
        } catch (PodChatException e) {
            captureError(e);
        }

        return uniqueId;
    }

    public String requestUnMuteCallParticipant(MuteUnMuteCallParticipantRequest request) {

        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                String message = CallAsyncRequestsManager.createUnMuteCallParticipantMessage(request, uniqueId);
                setCallBacks(false, false, false, true, ChatMessageType.Constants.UN_MUTE_CALL_PARTICIPANT, null, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_UN_MUTE_CALL_PARTICIPANT");
            } else {
                onChatNotReady(uniqueId);
            }
        } catch (PodChatException e) {
            captureError(e);
        }

        return uniqueId;
    }

    public String removeGroupCallParticipant(RemoveParticipantRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createRemoveCallParticipantMessage(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_REMOVE_CALL_PARTICIPANT");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String terminateAudioCall(TerminateCallRequest terminateCallRequest) {

//        if (audioCallManager != null)
//            audioCallManager.endStream(false);

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createTerminateCallMessage(terminateCallRequest, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_TERMINATE_CALL");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }

    public String getCallsHistory(GetCallHistoryRequest request) {

        String uniqueId = generateUniqueId();

        if (cache) {
            try {
                getCallHistoryFromCache(request, uniqueId);
            } catch (RoomIntegrityException e) {
                disableCache(() -> {
                    try {
                        getCallHistoryFromCache(request, uniqueId);
                    } catch (RoomIntegrityException ignored) {
                    }
                });
            }
        }


        if (chatReady) {
            String message = CallAsyncRequestsManager.createGetCallHistoryRequest(request, uniqueId);

            setCallBacks(false, false, false, false, ChatMessageType.Constants.GET_CALLS, request.getOffset(), uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_GET_CALL_HISTORIES");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    private void getCallHistoryFromCache(GetCallHistoryRequest request, String uniqueId) throws RoomIntegrityException {

        messageDatabaseHelper.getCallHistory(request, (contentCount, callVoList) -> {

            ChatResponse<GetCallHistoryResult> cacheResponse = CallAsyncRequestsManager.handleOnGetCallHistoryFromCache(uniqueId, new ArrayList<>((Collection<? extends CallVO>) callVoList), (Long) contentCount, request.getOffset());

            listenerManager.callOnGetCallHistory(cacheResponse);

            if (sentryResponseLog) {
                showLog("RECEIVED_CACHED_CALL_HISTORY", cacheResponse.getJson());
            } else {
                showLog("RECEIVED_CACHED_CALL_HISTORY");
            }

        });
    }


    private void startVideoCall(ChatResponse<StartedCallModel> info) {

        if (podVideoCall != null) {
            KafkaConfig kafkaConfig = new KafkaConfig.Builder(info.getResult().getClientDTO().getBrokerAddress())
                    .setSsl(info.getResult().getCert_file())
                    .build();

            podVideoCall.setKafkaConfig(kafkaConfig);


            String sendVideoTopic = info.getResult().getClientDTO().getTopicSendVideo();
            String sendAudioTopic = info.getResult().getClientDTO().getTopicSendAudio();
            String receiveVideoTopic = info.getResult().getClientDTO().getTopicReceiveVideo();
            String receiveAudioTopic = info.getResult().getClientDTO().getTopicReceiveAudio();


            if (Util.isNotNullOrEmpty(sendVideoTopic)) {
                visibleView(localPartnerView);
                localPartnerView.getSurfaceView().setZOrderOnTop(true);
                CallPartner lPartner = new CallPartner.Builder()
                        .setPartnerType(PartnerType.LOCAL)
                        .setName(info.getResult().getClientDTO().getSendKey() + "" + sendVideoTopic)
                        .setVideoTopic(sendVideoTopic)
                        .setAudioTopic(sendAudioTopic)
                        .setVideoView(localPartnerView)
                        .build();

                podVideoCall.addPartner(lPartner);
            }

            if (Util.isNotNullOrEmpty(receiveVideoTopic)) {
                if (hasRemotePartnerView()) {

                    visibleView(videoCallPartnerViews.get(0));

                    CallPartner rPartner = new CallPartner.Builder()
                            .setPartnerType(PartnerType.REMOTE)
                            .setName(info.getResult().getClientDTO().getSendKey() + "" + receiveVideoTopic)
                            .setVideoTopic(receiveVideoTopic)
                            .setAudioTopic(receiveAudioTopic)
                            .setVideoView(videoCallPartnerViews.remove(0))
                            .build();

                    podVideoCall.addPartner(rPartner);
                }
            }

            podVideoCall.startCall();

            if (callServiceManager != null)
                callServiceManager.startCallService(info, new ICallState() {
                    @Override
                    public void onInfoEvent(String info) {

                    }

                    @Override
                    public void onErrorEvent(String cause) {

                    }

                    @Override
                    public void onEndCallRequested() {
                        podVideoCall.endCall();

                        endAudioCall(CallAsyncRequestsManager.createEndCallRequest(info.getSubjectId()));

                        listenerManager.callOnEndCallRequestFromNotification();
                    }
                });
        }


    }

    public String acceptVoiceCall(AcceptCallRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createAcceptCallRequest(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.ACCEPT_CALL, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "ACCEPT_VOICE_CALL_REQUEST");
            if (request.isMute()) {
                podVideoCall.muteAudio();
            }
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String rejectVoiceCall(RejectCallRequest request) {

        String uniqueId = generateUniqueId();
        try {
            if (podVideoCall != null)
                podVideoCall.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (chatReady) {
            String message = CallAsyncRequestsManager.createRejectCallRequest(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REJECT_VOICE_CALL_REQUEST");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public void endAudioStream() {
        podVideoCall.endAudio();
    }

    public String endAudioCall(EndCallRequest endCallRequest) {

        if (callServiceManager != null)
            callServiceManager.stopCallService();

        if (podVideoCall != null)
            podVideoCall.endCall();

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createEndCallRequestMessage(endCallRequest, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_END_CALL");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }

    public void addPartnerView(CallPartnerView view) {
        if (Util.isNullOrEmpty(videoCallPartnerViews)) {
            videoCallPartnerViews = new ArrayList<>();
        }
        videoCallPartnerViews.add(view);
    }

    public void addAllPartnerView(List<CallPartnerView> views) {
        if (Util.isNullOrEmpty(videoCallPartnerViews)) {
            videoCallPartnerViews = new ArrayList<>();
        }
        videoCallPartnerViews.addAll(views);
    }

    public void setPartnerViews(List<CallPartnerView> views) {
        videoCallPartnerViews = new ArrayList<>(views);
    }

    public void updatePartnerViews(List<CallPartnerView> views) {
        if (videoCallPartnerViews == null || videoCallPartnerViews.size() == 0)
            videoCallPartnerViews = new ArrayList<>(views);
    }

    private void deliverCallRequest(ChatMessage chatMessage) {

        if (chatReady) {
            String message = CallAsyncRequestsManager.createDeliverCallRequestMessage(chatMessage);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELIVER_CALL_REQUEST");
        } else {
            onChatNotReady(chatMessage.getUniqueId());
        }

    }

    @Override
    protected void handleOnCallRequestDelivered(ChatMessage chatMessage) {
        if (sentryResponseLog) {
            showLog("CALL_REQUEST_DELIVERED", gson.toJson(chatMessage));
        } else {
            showLog("CALL_REQUEST_DELIVERED");
        }

        ChatResponse<CallDeliverResult> response
                = CallAsyncRequestsManager.handleOnCallDelivered(chatMessage);
        listenerManager.callOnCallRequestDelivered(response);
    }

    @Override
    protected void handleOnGroupCallRequestReceived(ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("RECEIVE_GROUP_CALL_REQUEST", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_GROUP_CALL_REQUEST");
        }

        ChatResponse<CallRequestResult> response
                = CallAsyncRequestsManager.handleOnGroupCallRequest(chatMessage);
        deliverCallRequest(chatMessage);
        listenerManager.callOnGroupCallRequest(response);

    }

    @Override
    protected void handleOnCallRequestReceived(ChatMessage chatMessage) {
        if (sentryResponseLog) {
            showLog("RECEIVE_CALL_REQUEST", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_CALL_REQUEST");
        }

        ChatResponse<CallRequestResult> response
                = CallAsyncRequestsManager.handleOnCallRequest(chatMessage);
        deliverCallRequest(chatMessage);
        listenerManager.callOnCallRequest(response);
    }

    @Override
    protected void handleOnCallRequestRejected(ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("CALL_REQUEST_REJECTED", gson.toJson(chatMessage));
        } else {
            showLog("CALL_REQUEST_REJECTED");
        }

        ChatResponse<CallRequestResult> response
                = CallAsyncRequestsManager.handleOnRejectCallRequest(chatMessage);
        listenerManager.callOnCallRequestRejected(response);
    }

    @Override
    protected void handleOnCallCreated(ChatMessage chatMessage) {
        ChatResponse<CallCreatedResult> response = CallAsyncRequestsManager.handleOnCallCreated(chatMessage);

        showLog("ON CALL CREATED", gson.toJson(response));

        messageCallbacks.remove(chatMessage.getUniqueId());

        listenerManager.callOnCallCreated(response);
    }

    @Override
    protected void handleOnReceiveActiveCallParticipants(Callback callback, ChatMessage chatMessage) {
        showLog("RECEIVE_ACTIVE_CALL_PARTICIPANTS", gson.toJson(chatMessage.getContent()));

        ChatResponse<GetCallParticipantResult> response = CallAsyncRequestsManager.reformatActiveCallParticipant(chatMessage);


        messageCallbacks.remove(callback.getUniqueId());

        listenerManager.callOnReceiveActiveCallParticipants(response);
    }

    @Override
    void handleOnCallStarted(Callback callback, ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("VOICE_CALL_STARTED", gson.toJson(chatMessage));
        } else {
            showLog("VOICE_CALL_STARTED");
        }


        ChatResponse<StartedCallModel> info
                = CallAsyncRequestsManager.handleOnCallStarted(chatMessage);

        ChatResponse<CallStartResult> response = CallAsyncRequestsManager.fillResult(info);


        startVideoCall(info);

        getCallParticipants(new GetCallParticipantsRequest.Builder().setCallId(info.getSubjectId()).build());

        if (callback != null)
            messageCallbacks.remove(callback.getUniqueId());

        listenerManager.callOnCallVoiceCallStarted(response);

    }

    private void handleOnCallAcceptedFromAnotherDevice(ChatMessage chatMessage) {

        showLog("RECEIVE_START_CALL_FROM_ANOTHER_DEVICE", gson.toJson(chatMessage));

        listenerManager.callOnAnotherDeviceAcceptedCall();


    }

    @Override
    void handleOnVoiceCallEnded(ChatMessage chatMessage) {


        if (sentryResponseLog) {
            showLog("RECEIVE_VOICE_CALL_ENDED", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_VOICE_CALL_ENDED");
        }

        if (podVideoCall != null)
            podVideoCall.endCall();

        if (callServiceManager != null)
            callServiceManager.stopCallService();

        ChatResponse<EndCallResult> response = CallAsyncRequestsManager.handleOnCallEnded(chatMessage);

        listenerManager.callOnVoiceCallEnded(response);


    }

    @Override
    void handleOnNewCallParticipantJoined(ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("RECEIVE_PARTICIPANT_JOINED", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_PARTICIPANT_JOINED");
        }

        ChatResponse<JoinCallParticipantResult> response = CallAsyncRequestsManager.handleOnParticipantJoined(chatMessage);

        if (podVideoCall != null)
            addVideoCallPartner(response, false);

        listenerManager.callOnCallParticipantJoined(response);


    }

    @Override
    protected void handleOnCallParticipantCanceledCall(ChatMessage chatMessage) {
        showLog("RECEIVE_CANCEL_GROUP_CALL", gson.toJson(chatMessage));

        ChatResponse<CallCancelResult> response = CallAsyncRequestsManager.handleOnCallCanceled(chatMessage);

        listenerManager.callOnCallCanceled(response);
    }

    @Override
    protected void handleOnCallParticipantLeft(ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("RECEIVE_LEAVE_CALL", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_LEAVE_CALL");
        }

        ChatResponse<LeaveCallResult> response = CallAsyncRequestsManager.handleOnParticipantLeft(chatMessage);

        removeCallPartner(response.getResult().getCallParticipants().get(0));

        listenerManager.callOnCallParticipantLeft(response);

    }

    @Override
    protected void handleOnCallParticipantRemoved(ChatMessage chatMessage) {

        ChatResponse<RemoveFromCallResult> response = CallAsyncRequestsManager.handleOnParticipantRemoved(chatMessage);

        if (response.getResult().isUserRemoved()) {

            if (sentryResponseLog) {
                showLog("RECEIVE_REMOVED_FROM_CALL", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_REMOVED_FROM_CALL");
            }

            if (podVideoCall != null)
                podVideoCall.endCall();

            if (callServiceManager != null)
                callServiceManager.stopCallService();

            listenerManager.callOnRemovedFromCall(response);

        } else {

            if (sentryResponseLog) {
                showLog("RECEIVE_CALL_PARTICIPANT_REMOVED", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_CALL_PARTICIPANT_REMOVED");
            }

            if (podVideoCall != null)
                removeCallPartner(response.getResult().getCallParticipants().get(0));

            listenerManager.callOnCallParticipantRemoved(response);

        }


    }

    @Override
    void handOnCallParticipantAddedVideo(ChatMessage chatMessage) {
        if (sentryResponseLog) {
            showLog("RECEIVE_CALL_PARTICIPANT_STARTED_VIDEO", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_CALL_PARTICIPANT_STARTED_VIDEO");
        }

        ChatResponse<JoinCallParticipantResult> response = CallAsyncRequestsManager.handleOnParticipantJoined(chatMessage);

        if (podVideoCall != null)
            addVideoCallPartner(response, true);

        listenerManager.callOnCallParticipantStartedVideo(response);
    }

    @Override
    void handleOnCallParticipantRemovedVideo(ChatMessage chatMessage) {

        if (sentryResponseLog) {
            showLog("RECEIVE_CALL_PARTICIPANT_STOPPED_VIDEO", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVE_CALL_PARTICIPANT_STOPPED_VIDEO");
        }
        ChatResponse<JoinCallParticipantResult> response = CallAsyncRequestsManager.handleOnParticipantJoined(chatMessage);

        if (podVideoCall != null)
            removeVideoCallPartner(response);

        listenerManager.callOnCallParticipantStoppedVideo(response);
    }

    @Override
    protected void handleOnCallParticipantMuted(Callback callback, ChatMessage chatMessage) {
        showLog("RECEIVE_MUTE_CALL_PARTICIPANT", chatMessage.getContent());

        ChatResponse<MuteUnMuteCallParticipantResult> response =
                CallAsyncRequestsManager.handleMuteUnMuteCallParticipant(chatMessage);

        if (callback != null) {

            if (CallAsyncRequestsManager.isUserContains(response.getResult().getCallParticipants())) {
                //audio call muted onAudioCallMuted
                showLog("RECEIVE_AUDIO_CALL_MUTED");
                callOnUserIsMute(callback, response);

                if (response.getResult().getCallParticipants().size() > 1) {
                    callOnOtherCallParticipantsMuted(response);
                }

            } else {
                callOnOtherCallParticipantsMuted(response);
            }


        } else {

            if (!response.isHasError()) {

                if (CallAsyncRequestsManager.isUserContains(response.getResult().getCallParticipants())) {
                    //user is muted onMutedByAdmin
                    callOnCurrentUserMutedByAdmin(response);

                    if (response.getResult().getCallParticipants().size() > 1) {
                        callOnOtherCallParticipantsMuted(response);
                    }
                } else {
                    //call participant muted onCallParticipantMuted
                    callOnOtherCallParticipantsMuted(response);
                }
            }

        }


    }

    @Override
    protected void handleOnCallParticipantUnMuted(Callback callback, ChatMessage chatMessage) {
        showLog("RECEIVE_UN_MUTE_CALL_PARTICIPANT", chatMessage.getContent());

        ChatResponse<MuteUnMuteCallParticipantResult> response =
                CallAsyncRequestsManager.handleMuteUnMuteCallParticipant(chatMessage);


        if (callback != null) {

            if (CallAsyncRequestsManager.isUserContains(response.getResult().getCallParticipants())) {
                //audio call unmuted onAudioCallMuted
                callOnUserIsUnMute(callback, response);

                if (response.getResult().getCallParticipants().size() > 1) {
                    callOnOtherCallParticipantsUnMuted(response);
                }

            } else {
                //call participant unmuted onCallParticipantUnMuted
                callOnOtherCallParticipantsUnMuted(response);
            }


        } else {

            if (!response.isHasError()) {

                if (CallAsyncRequestsManager.isUserContains(response.getResult().getCallParticipants())) {
                    //user is unmuted onMutedByAdmin
                    callOnCurrentUserUnMutedByAdmin(response);

                    if (response.getResult().getCallParticipants().size() > 1) {
                        //call participant unmuted onCallParticipantUnMuted
                        callOnOtherCallParticipantsUnMuted(response);
                    }

                } else {
                    //call participant unmuted onCallParticipantUnMuted
                    callOnOtherCallParticipantsUnMuted(response);
                }
            }

        }
    }


    @Override
    protected void handleOnGetCallsHistory(ChatMessage chatMessage, Callback callback) {
        if (sentryResponseLog) {
            showLog("RECEIVED_CALL_HISTORY", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVED_CALL_HISTORY");
        }

        ChatResponse<GetCallHistoryResult> response = CallAsyncRequestsManager.handleOnGetCallHistory(chatMessage, callback);

        if (cache)
            messageDatabaseHelper.saveCallsHistory(response.getResult().getCallsList());

        listenerManager.callOnGetCallHistory(response);

    }

    @Override
    protected void handleOnReceivedCallReconnect(ChatMessage chatMessage) {
        if (sentryResponseLog) {
            showLog("RECEIVED_CALL_RECONNECT", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVED_CALL_RECONNECT");
        }

        ChatResponse<CallReconnectResult> response = CallAsyncRequestsManager.handleOnCallReconnectReceived(chatMessage);

        listenerManager.callOnCallReconnectReceived(response);
    }

    @Override
    protected void handleOnReceivedCallConnect(ChatMessage chatMessage) {
        if (sentryResponseLog) {
            showLog("RECEIVED_CALL_CONNECT", gson.toJson(chatMessage));
        } else {
            showLog("RECEIVED_CALL_CONNECT");
        }


        ChatResponse<CallReconnectResult> response = CallAsyncRequestsManager.handleOnCallConnectReceived(chatMessage);

        listenerManager.callOnCallConnectReceived(response);
    }


    void callOnCurrentUserUnMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        showLog("RECEIVE_UN_MUTED_BY_ADMIN");
        podVideoCall.unMuteAudio();
        listenerManager.callOnUnMutedByAdmin(response);
    }

    void callOnCurrentUserMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        showLog("RECEIVE_MUTED_BY_ADMIN");
        podVideoCall.muteAudio();
        listenerManager.callOnMutedByAdmin(response);
    }

    private void callOnOtherCallParticipantsUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        showLog("RECEIVE_CALL_PARTICIPANT_UN_MUTED");
        listenerManager.callOnCallParticipantUnMuted(response);
    }

    private void callOnOtherCallParticipantsMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        showLog("RECEIVE_CALL_PARTICIPANT_MUTED");
        listenerManager.callOnCallParticipantMuted(response);
    }

    private void callOnUserIsMute(Callback callback, ChatResponse<MuteUnMuteCallParticipantResult> response) {
        messageCallbacks.remove(callback.getUniqueId());
        listenerManager.callOnAudioCallMuted(response);
    }

    private void callOnUserIsUnMute(Callback callback, ChatResponse<MuteUnMuteCallParticipantResult> response) {
        showLog("RECEIVE_AUDIO_CALL_UN_MUTED");
        messageCallbacks.remove(callback.getUniqueId());
        listenerManager.callOnAudioCallUnMuted(response);
    }

    public String turnCallParticipantVideoOff(TurnCallParticipantVideoOffRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            try {
                String message = CallAsyncRequestsManager.createTurnOffVideoMessage(request, uniqueId);
                setCallBacks(false, false, false, true, ChatMessageType.Constants.TURN_OFF_VIDEO_CALL, null, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TURN_OFF_VIDEO_CALL");
            } catch (PodChatException e) {
                captureError(e);
            }
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String switchCallMuteState(boolean isMute, long callId) {

        if (isMute) {
            podVideoCall.muteAudio();
        } else {
            podVideoCall.unMuteAudio();
        }

        String uniqueId = generateUniqueId();

        try {
            if (chatReady) {
                String message = CallAsyncRequestsManager.createMuteOrUnMuteCallMessage(isMute, callId, uniqueId);
                setCallBacks(false, false, false, true, isMute ? ChatMessageType.Constants.MUTE_CALL_PARTICIPANT : ChatMessageType.Constants.UN_MUTE_CALL_PARTICIPANT, null, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, isMute ? "SEND_MUTE_CALL" : "SEND_UN_MUTE_CALL");
            } else {
                onChatNotReady(uniqueId);
            }
        } catch (PodChatException e) {
            captureError(e);
        }

        return uniqueId;
    }

    public void switchCallSpeakerState(boolean isSpeakerOn) {

        if (isSpeakerOn)
            podVideoCall.speakerOn();

        else podVideoCall.speakerOff();
    }

    public void openCamera() {
        if (podVideoCall != null)
            podVideoCall.openCamera();
    }

    public void closeCamera() {
        if (podVideoCall != null)
            podVideoCall.closeCamera();
    }

    public void switchCamera() {
        if (podVideoCall != null) {
            podVideoCall.switchCamera();
        }
    }

    public String turnOnVideo(long callId) {
        if (podVideoCall != null) {

            podVideoCall.resumeVideo();
        }

        String uniqueId = generateUniqueId();

        if (chatReady) {
            String message = CallAsyncRequestsManager.createTurnOnVideoMessage(callId, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.TURN_ON_VIDEO_CALL, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TURN_ON_VIDEO_CALL");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    public String turnOffVideo(long callId) {
        if (podVideoCall != null)
            podVideoCall.pauseVideo();

        String uniqueId = generateUniqueId();

        if (chatReady) {
            String message = CallAsyncRequestsManager.createTurnOffVideoMessage(callId, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.TURN_OFF_VIDEO_CALL, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TURN_OFF_VIDEO_CALL");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;

    }


    private void removeCallPartner(String topic) {
        if (podVideoCall != null)
            podVideoCall.removePartnerOfTopic(topic);
    }

    private void removeCallPartner(CallParticipantVO partner) {
        // TODO: 5/30/2021 Change to a better scenario!
        if (podVideoCall != null) {
            if (partner.getSendTopicVideo() != null) {
                podVideoCall.removePartnerOfTopic(partner.getSendTopicVideo());
                return;
            }
            if (partner.getSendTopicAudio() != null)
                podVideoCall.removePartnerOfTopic(partner.getSendTopicAudio());
        }
    }

    private void addVideoCallPartner(ChatResponse<JoinCallParticipantResult> response, boolean forceVideo) {

        if (hasRemotePartnerView()) {
            for (CallParticipantVO callParticipant :
                    response.getResult().getJoinedParticipants()) {

                if (forceVideo || callParticipant.hasVideo()) {
                    String topic = callParticipant.getSendTopicVideo();

                    if (Util.isNotNullOrEmpty(topic)) {

                        visibleView(videoCallPartnerViews.get(0));

                        CallPartner rPartner = new CallPartner.Builder()
                                .setPartnerType(PartnerType.REMOTE)
                                .setName(callParticipant.getSendTopicVideo() + " ")
                                .setVideoTopic(topic)
                                .setVideoView(videoCallPartnerViews.remove(0))
                                .build();

                        podVideoCall.addPartner(rPartner);
                    }
                }
            }
        }


    }

    private void removeVideoCallPartner(ChatResponse<JoinCallParticipantResult> response) {


        for (CallParticipantVO callParticipant :
                response.getResult().getJoinedParticipants()) {
            String topic = callParticipant.getSendTopicVideo();
            if (Util.isNotNullOrEmpty(topic)) {
                CallPartner rPartner = new CallPartner.Builder()
                        .setPartnerType(PartnerType.REMOTE)
                        .setName(callParticipant.getSendTopicVideo() + " ")
                        .setVideoTopic(topic)
                        .build();
                podVideoCall.removePartner(rPartner);
            }

        }


    }

    // TODO: 1/31/2021 Create new view and send with call back
    boolean hasRemotePartnerView() {

        if (videoCallPartnerViews.size() == 0) {
            listenerManager.callOnNoViewToAddNewPartnerError();
        }
        return videoCallPartnerViews.size() > 0;
    }
}
