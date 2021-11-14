package com.fanap.podchat.chat;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.fanap.podcall.IPodCall;
import com.fanap.podcall.PartnerType;
import com.fanap.podcall.PodCall;
import com.fanap.podcall.PodCallBuilder;
import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.kafka.KafkaConfig;
import com.fanap.podcall.model.CallPartner;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.screenshare.model.ScreenShareParam;
import com.fanap.podcall.screenshare.model.ScreenSharer;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.CallAsyncRequestsManager;
import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.audio_call.CallServiceManager;
import com.fanap.podchat.call.audio_call.ICallState;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.model.ClientDTO;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.call.request_model.GetCallParticipantsRequest;
import com.fanap.podchat.call.request_model.MuteUnMuteCallParticipantRequest;
import com.fanap.podchat.call.request_model.RejectCallRequest;
import com.fanap.podchat.call.request_model.StartOrEndCallRecordRequest;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.request_model.TurnCallParticipantVideoOffRequest;
import com.fanap.podchat.call.request_model.screen_share.EndShareScreenRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenSharePermissionRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenShareRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenShareResult;
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
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Chat extends ChatCore {

    private boolean deviceIsInCall = false;
    private List<CallPartnerView> videoCallPartnerViews;
    private CallPartnerView localPartnerView;
    private PodCall podVideoCall;
    private CallServiceManager callServiceManager;

    @Deprecated
    public void setupCall(VideoCallParam videoCallParam,
                          AudioCallParam audioCallParam
            , List<CallPartnerView> remoteViews) {

        this.localPartnerView = videoCallParam.getCameraPreview();

        this.videoCallPartnerViews = new ArrayList<>(remoteViews);

        podVideoCall = new PodCallBuilder(context, new IPodCall() {
            @Override
            public void onError(String s) {
                captureError(new PodChatException(s, ChatConstant.ERROR_CODE_CALL_INITIAL_ERROR));
            }

            @Override
            public void onEvent(String s) {
                showLog(s);
            }

            @Override
            public void onCameraReady(PodCall podCall) {
                showLog("Camera is ready");
            }

        })
                .setVideoCallParam(videoCallParam)
                .setAudioCallParam(audioCallParam)
                .build();

        podVideoCall.initial();

    }


    public void setupCall(VideoCallParam videoCallParam,
                          AudioCallParam audioCallParam,
                          ScreenShareParam screenShareParam,
                          CallConfig callConfig,
                          List<CallPartnerView> remoteViews) {

        this.localPartnerView = videoCallParam.getCameraPreview();

        this.videoCallPartnerViews = new ArrayList<>(remoteViews);

        callServiceManager = new CallServiceManager(context, callConfig);

        podVideoCall = new PodCallBuilder(context, new IPodCall() {
            @Override
            public void onError(String s) {
                captureError(new PodChatException(s, ChatConstant.ERROR_CODE_CALL_INITIAL_ERROR));
            }

            @Override
            public void onEvent(String s) {
                showLog(s);
            }

            @Override
            public void onCameraReady(PodCall podCall) {
                showLog("Call is ready");
            }


        })
                .setVideoCallParam(videoCallParam)
                .setAudioCallParam(audioCallParam)
                .setScreenShareParam(screenShareParam)
                .build();

        podVideoCall.initial();


    }

    public String requestCall(CallRequest request) {

        String uniqueId = generateUniqueId();
        deviceIsInCall = true;
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
        deviceIsInCall = true;
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

    /**
     * @param terminateCallRequest
     * @return
     * @deprecated terminateAudioCall is misleading in video calls. {@link #terminateCall(TerminateCallRequest)}
     */
    @Deprecated
    public String terminateAudioCall(TerminateCallRequest terminateCallRequest) {

        stopCallService();
        endCall();

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createTerminateCallMessage(terminateCallRequest, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_TERMINATE_CALL");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }


    public String terminateCall(TerminateCallRequest terminateCallRequest) {

        stopCallService();
        endCall();

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void requestScreenSharePermission(ScreenSharePermissionRequest request) {
        if (podVideoCall != null) {
            podVideoCall.requestScreenSharePermission(request.getActivity(), request.getPermissionCode());
        }
    }


    //Send request to start screen share screen
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String startShareScreen(ScreenShareRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            if (podVideoCall != null) {
                podVideoCall.startSharingScreen(request.getData());
            }
            String message = CallAsyncRequestsManager.createStartShareScreenMessage(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.START_SHARE_SCREEN, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_START_SHARE_SCREEN");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    //Send request to end screen share screen
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String endShareScreen(EndShareScreenRequest request) {
        String uniqueId = generateUniqueId();
        if (podVideoCall != null) {
            podVideoCall.stopSharingScreen();
        }
        if (chatReady) {
            String message = CallAsyncRequestsManager.createEndShareScreenMessage(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.END_SHARE_SCREEN, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_END_SHARE_SCREEN");
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
            KafkaConfig kafkaConfig = new KafkaConfig.Builder(info.getResult().getChatDataDTO().getBrokerAddress())
                    .setSsl(info.getResult().getCert_file())
                    .build();

            podVideoCall.setKafkaConfig(kafkaConfig);


            ClientDTO localClient = info.getResult().getClientDTO();

            String sendVideoTopic = localClient.getTopicSendVideo();

            String sendAudioTopic = localClient.getTopicSendAudio();


            CallPartner.Builder lCallPartnerBuilder = new CallPartner.Builder();
            lCallPartnerBuilder.setPartnerType(PartnerType.LOCAL);
            lCallPartnerBuilder.setName(info.getResult().getClientDTO().getSendKey() + "" + sendVideoTopic);

            if (localClient.getVideo()) {
                visibleView(localPartnerView);
                localPartnerView.setPartnerId(CoreConfig.userId);
                localPartnerView.setPartnerName("You");
                lCallPartnerBuilder.setVideoTopic(sendVideoTopic);
                lCallPartnerBuilder.setVideoView(localPartnerView);
            }
            lCallPartnerBuilder.setAudioTopic(sendAudioTopic);
            podVideoCall.addPartner(lCallPartnerBuilder.build());


            if (Util.isNotNullOrEmpty(info.getResult().getOtherClientDtoList())) {
                prepareRemotePartnersByClientDTOList(info.getResult().getOtherClientDtoList());
            } else {
                if (prepareRemotePartnersByReceiveTopic(info)) return;
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

                        endCall();

                        endCall(CallAsyncRequestsManager.createEndCallRequest(info.getSubjectId()));

                        listenerManager.callOnEndCallRequestFromNotification();
                    }
                });
        }
    }

    private void prepareRemotePartnersByClientDTOList(ArrayList<ClientDTO> callClientList) {

        for (ClientDTO client :
                callClientList) {

            if (client.getUserId() == 0) continue;

            if (!client.getUserId().equals(CoreConfig.userId)) {

                CallPartner.Builder rPartnerBuilder = new CallPartner.Builder();
                rPartnerBuilder.setPartnerType(PartnerType.REMOTE)
                        .setName("" + client.getUserId());

                if (client.getVideo() && hasRemotePartnerView()) {
                    visibleView(videoCallPartnerViews.get(0));
                    videoCallPartnerViews.get(0).setPartnerId(client.getUserId());
                    rPartnerBuilder.setVideoTopic(client.getTopicSendVideo());
                    rPartnerBuilder.setVideoView(videoCallPartnerViews.remove(0));
                }

                rPartnerBuilder.setAudioTopic(client.getTopicSendAudio());
                CallPartner rPartner = rPartnerBuilder.build();
                podVideoCall.addPartner(rPartner);

            }

        }
    }

    private void prepareRemotePartnersByCallParticipantVO(List<CallParticipantVO> callClientList) {

        for (CallParticipantVO callParticipant :
                callClientList) {

            if (callParticipant.getUserId() == 0) continue;

            if (!callParticipant.getUserId().equals(CoreConfig.userId)) {

                CallPartner.Builder rPartnerBuilder = new CallPartner.Builder();
                rPartnerBuilder.setPartnerType(PartnerType.REMOTE)
                        .setName("" + callParticipant.getUserId());

                if (callParticipant.hasVideo() && hasRemotePartnerView()) {
                    visibleView(videoCallPartnerViews.get(0));
                    videoCallPartnerViews.get(0).setPartnerId(callParticipant.getUserId());
                    videoCallPartnerViews.get(0).setPartnerName(callParticipant.getParticipantVO() != null ? callParticipant.getParticipantVO().getName() : "");
                    rPartnerBuilder
                            .setVideoTopic(callParticipant.getSendTopicVideo())
                            .setVideoView(videoCallPartnerViews.remove(0));
                }
                rPartnerBuilder.setAudioTopic(callParticipant.getSendTopicAudio());
                CallPartner rPartner = rPartnerBuilder.build();
                podVideoCall.addPartner(rPartner);

            }

        }
    }


    //deprecated and will remove soon
    @Deprecated
    private boolean prepareRemotePartnersByReceiveTopic(ChatResponse<StartedCallModel> info) {
        String receiveTopic = info.getResult().getClientDTO().getTopicReceive();
        List<String> receiveList = Arrays.asList(receiveTopic.split(","));
        if (Util.isNullOrEmpty(receiveList)) {
            captureError(new PodChatException(ChatConstant.ERROR_INVALID_DATA, ChatConstant.ERROR_CODE_INVALID_DATA));
            return true;
        }
        if (receiveList.size() == 1) {
            String receiveVideoTopic = info.getResult().getClientDTO().getTopicReceiveVideo();
            String receiveAudioTopic = info.getResult().getClientDTO().getTopicReceiveAudio();
            Boolean hasVideo = info.getResult().getClientDTO().getVideo();
            CallPartner.Builder rPartnerBuilder = new CallPartner.Builder();
            rPartnerBuilder.setPartnerType(PartnerType.REMOTE)
                    .setName(info.getResult().getClientDTO().getSendKey() + "" + receiveVideoTopic);
            if (hasVideo && hasRemotePartnerView()) {
                visibleView(videoCallPartnerViews.get(0));
                rPartnerBuilder
                        .setVideoTopic(receiveVideoTopic)
                        .setVideoView(videoCallPartnerViews.remove(0));
            }
            rPartnerBuilder.setAudioTopic(receiveAudioTopic);
            CallPartner rPartner = rPartnerBuilder.build();
            podVideoCall.addPartner(rPartner);
        } else {
            for (String topic :
                    receiveList) {
                addCallPartnerByTopicOnly(topic);
            }
        }
        return false;
    }


    private void addCallPartnerByTopicOnly(String receiveTopic) {


        String receiveVideoTopic = "Vi-" + receiveTopic;
        String receiveAudioTopic = "Vo-" + receiveTopic;
        boolean hasVideo = true; // todo fix later

        CallPartner.Builder rPartnerBuilder = new CallPartner.Builder();
        rPartnerBuilder.setPartnerType(PartnerType.REMOTE)
                .setName(receiveTopic + ":" + System.currentTimeMillis());
        if (hasVideo && hasRemotePartnerView()) {
            visibleView(videoCallPartnerViews.get(0));
            rPartnerBuilder
                    .setVideoTopic(receiveVideoTopic)
                    .setVideoView(videoCallPartnerViews.remove(0));
        }
        rPartnerBuilder.setAudioTopic(receiveAudioTopic);
        CallPartner rPartner = rPartnerBuilder.build();
        podVideoCall.addPartner(rPartner);
    }

    /**
     * @param request request to start call recording
     */
    public String startCallRecord(StartOrEndCallRecordRequest request) {

        String uniqueId = generateUniqueId();


        if (chatReady) {
            String message = CallAsyncRequestsManager.createStartRecordCall(request, uniqueId);
            setCallBacks(false, false, false, true, ChatMessageType.Constants.START_RECORD_CALL, null, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_START_CALL_RECORD_REQUEST");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;

    }

    /**
     * @param request request to end call recording
     */
    public String endCallRecord(StartOrEndCallRecordRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            setCallBacks(false, false, false, true, ChatMessageType.Constants.END_RECORD_CALL, null, uniqueId);
            String message = CallAsyncRequestsManager.createEndRecordCall(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_END_CALL_RECORD_REQUEST");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;

    }

    public String acceptVoiceCall(AcceptCallRequest request) {

        String uniqueId = generateUniqueId();
        deviceIsInCall = true;
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
            endCall();

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

    /**
     * @param endCallRequest
     * @return request uniqueId
     * @Deprecated endAudioCall is misleading in video calls. use {@link #endCall(EndCallRequest)}
     */
    @Deprecated
    public String endAudioCall(EndCallRequest endCallRequest) {

        stopCallService();

        endCall();


        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createEndCallRequestMessage(endCallRequest, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_END_CALL");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }

    public String endCall(EndCallRequest endCallRequest) {

        stopCallService();

        endCall();


        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = CallAsyncRequestsManager.createEndCallRequestMessage(endCallRequest, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "REQUEST_END_CALL");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }

    private void stopCallService() {
        if (callServiceManager != null)
            callServiceManager.stopCallService();
    }

    public void addPartnerView(CallPartnerView view) {
        if (Util.isNullOrEmpty(videoCallPartnerViews)) {
            videoCallPartnerViews = new ArrayList<>();
        }
        videoCallPartnerViews.add(view);
    }

    public void addPartnerView(CallPartnerView view, int pos) {
        if (Util.isNullOrEmpty(videoCallPartnerViews)) {
            videoCallPartnerViews = new ArrayList<>();
            videoCallPartnerViews.add(view);
        } else {
            videoCallPartnerViews.add(pos, view);
        }
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
    protected void handleOnCallRequestDelivered(ChatMessage chatMessage, Callback callback) {
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

    void handleOnCallStarted(Callback callback, ChatMessage chatMessage) {

        if (deviceIsInCall) {
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
        } else {
            handleOnCallAcceptedFromAnotherDevice(chatMessage);
        }


    }

    private void handleOnCallAcceptedFromAnotherDevice(ChatMessage chatMessage) {

        showLog("RECEIVE_START_CALL_FROM_ANOTHER_DEVICE", gson.toJson(chatMessage));

        listenerManager.callOnAnotherDeviceAcceptedCall();


    }

    @Override
    void handleOnVoiceCallEnded(ChatMessage chatMessage) {


        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_VOICE_CALL_ENDED", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_VOICE_CALL_ENDED");
            }

            stopCallService();

            endCall();


            ChatResponse<EndCallResult> response = CallAsyncRequestsManager.handleOnCallEnded(chatMessage);

            listenerManager.callOnVoiceCallEnded(response);
        }


    }

    @Override
    void handleOnNewCallParticipantJoined(ChatMessage chatMessage) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_PARTICIPANT_JOINED", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_PARTICIPANT_JOINED");
            }

            ChatResponse<JoinCallParticipantResult> response = CallAsyncRequestsManager.handleOnParticipantJoined(chatMessage);

//        audioCallManager.addCallParticipant(response);

            if (podVideoCall != null)
                prepareRemotePartnersByCallParticipantVO(response.getResult().getJoinedParticipants());

            listenerManager.callOnCallParticipantJoined(response);
        }
    }

    @Override
    protected void handleOnCallParticipantCanceledCall(ChatMessage chatMessage) {
        if (deviceIsInCall) {
            showLog("RECEIVE_CANCEL_GROUP_CALL", gson.toJson(chatMessage));

            ChatResponse<CallCancelResult> response = CallAsyncRequestsManager.handleOnCallCanceled(chatMessage);

            listenerManager.callOnCallCanceled(response);
        }
    }

    @Override
    protected void handleOnCallParticipantLeft(ChatMessage chatMessage) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_LEAVE_CALL", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_LEAVE_CALL");
            }

            ChatResponse<LeaveCallResult> response = CallAsyncRequestsManager.handleOnParticipantLeft(chatMessage);

            removeCallPartner(response.getResult().getCallParticipants().get(0));

            listenerManager.callOnCallParticipantLeft(response);
        }
    }

    @Override
    protected void handleOnCallParticipantRemoved(ChatMessage chatMessage) {

        if (deviceIsInCall) {
            ChatResponse<RemoveFromCallResult> response = CallAsyncRequestsManager.handleOnParticipantRemoved(chatMessage);

            if (response.getResult().isUserRemoved()) {

                if (sentryResponseLog) {
                    showLog("RECEIVE_REMOVED_FROM_CALL", gson.toJson(chatMessage));
                } else {
                    showLog("RECEIVE_REMOVED_FROM_CALL");
                }

                stopCallService();

                endCall();


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

    }

    private void endCall() {
        if (podVideoCall != null) {
            podVideoCall.endCall();
            deviceIsInCall = false;
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
            addVideoCallPartner(response);

    }

    @Override
    void handleOnCallParticipantRemovedVideo(ChatMessage chatMessage) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_CALL_PARTICIPANT_STOPPED_VIDEO", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_CALL_PARTICIPANT_STOPPED_VIDEO");
            }
            ChatResponse<JoinCallParticipantResult> response = CallAsyncRequestsManager.handleOnParticipantJoined(chatMessage);

            if (podVideoCall != null)
                removeVideoCallPartner(response);

            // TODO: 10/3/2021 fire an event for local partner
            listenerManager.callOnCallParticipantStoppedVideo(response);
        }

    }

    @Override
    void handleOnEndedCallRecord(ChatMessage chatMessage, Callback callback) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECORD_CALL_ENDED", gson.toJson(chatMessage));
            } else {
                showLog("RECORD_CALL_ENDED");
            }
            ChatResponse<Participant> response
                    = CallAsyncRequestsManager.handleStartedRecordCallResponse(chatMessage);

            if (callback != null) {
                removeCallback(chatMessage.getUniqueId());
                listenerManager.callOnCallRecordEnded(response);
            } else {
                listenerManager.callOnCallParticipantStopRecording(response);
            }
        }

    }

    @Override
    void handOnShareScreenStarted(ChatMessage chatMessage, Callback callback) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_SHARE_SCREEN_STARTED", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_SHARE_SCREEN_STARTED");
            }

            if (podVideoCall != null) {
                ChatResponse<ScreenShareResult> response = CallAsyncRequestsManager.handleOnScreenShareStarted(chatMessage);
                if (Util.isNotNullOrEmpty(response.getResult().getScreenShare())) {
                    if (callback != null) {
                        removeCallback(chatMessage.getUniqueId());

                        ScreenSharer screenSharer = new ScreenSharer.Builder()
                                .setPartnerType(PartnerType.LOCAL)
                                .setName("Your Screen")
                                .setVideoTopic(response.getResult().getScreenShare())
                                .build();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            podVideoCall.addScreenSharer(screenSharer);
                            listenerManager.callOnScreenShareStarted(response);
                        }
                    } else {
                        if (hasRemotePartnerView()) {
                            visibleView(videoCallPartnerViews.get(0));

                            CallPartner rPartner = new CallPartner.Builder()
                                    .setPartnerType(PartnerType.REMOTE)
                                    .setName("Screen Sharer:" + response.getResult().getScreenOwner().getId())
                                    .setVideoTopic(response.getResult().getScreenShare())
                                    .setVideoView(videoCallPartnerViews.remove(0))
                                    .build();

                            podVideoCall.addPartner(rPartner);
                            listenerManager.callOnCallParticipantSharedScreen(response);
                        }
                    }
                }else{
                    captureError(new PodChatException(ChatConstant.ERROR_METHOD_NOT_IMPLEMENTED,ChatConstant.ERROR_CODE_METHOD_NOT_IMPLEMENTED,chatMessage.getUniqueId()));
                }
            }
        }
    }

    @Override
    void handOnShareScreenEnded(ChatMessage chatMessage, Callback callback) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVE_SHARE_SCREEN_ENDED", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVE_SHARE_SCREEN_ENDED");
            }

            ChatResponse<ScreenShareResult> response = CallAsyncRequestsManager.handleOnScreenShareStarted(chatMessage);

            if (callback != null) {
                removeCallback(chatMessage.getUniqueId());
                //client stopped screen share
                listenerManager.callOnScreenShareEnded(response);
            } else {
                if (podVideoCall != null) {
                    podVideoCall.removePartnerOfTopic(response.getResult().getScreenShare());
                }
                listenerManager.callOnCallParticipantStoppedScreenSharing(response);
            }
        }

    }

    @Override
    void handleOnStartedCallRecord(ChatMessage chatMessage, Callback callback) {

        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECORD_CALL_STARTED", gson.toJson(chatMessage));
            } else {
                showLog("RECORD_CALL_STARTED");
            }

            ChatResponse<Participant> response
                    = CallAsyncRequestsManager.handleStartedRecordCallResponse(chatMessage);

            if (callback != null) {
                removeCallback(chatMessage.getUniqueId());
                listenerManager.callOnCallRecordStarted(response);
            } else {
                listenerManager.callOnCallParticipantStartRecording(response);
            }
        }

    }

    @Override
    protected void handleOnCallParticipantMuted(Callback callback, ChatMessage chatMessage) {
        if (deviceIsInCall) {
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
    }

    @Override
    protected void handleOnCallParticipantUnMuted(Callback callback, ChatMessage chatMessage) {
        if (deviceIsInCall) {
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
    }


    @Override
    protected void handleOnGetCallsHistory(ChatMessage chatMessage, Callback callback) {
        if (callback != null) {
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

    }

    @Override
    protected void handleOnReceivedCallReconnect(ChatMessage chatMessage) {
        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVED_CALL_RECONNECT", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVED_CALL_RECONNECT");
            }

            ChatResponse<CallReconnectResult> response = CallAsyncRequestsManager.handleOnCallReconnectReceived(chatMessage);

            listenerManager.callOnCallReconnectReceived(response);
        }
    }

    @Override
    protected void handleOnReceivedCallConnect(ChatMessage chatMessage) {
        if (deviceIsInCall) {
            if (sentryResponseLog) {
                showLog("RECEIVED_CALL_CONNECT", gson.toJson(chatMessage));
            } else {
                showLog("RECEIVED_CALL_CONNECT");
            }


            ChatResponse<CallReconnectResult> response = CallAsyncRequestsManager.handleOnCallConnectReceived(chatMessage);

            listenerManager.callOnCallConnectReceived(response);
        }
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

    @Deprecated
    public void switchCamera() {
        if (podVideoCall != null) {
            podVideoCall.switchCamera();
        }
    }

    public boolean isFrontCamera() {
        if (podVideoCall != null) {
            return podVideoCall.isFrontCamera();
        }
        return false;
    }

    public void switchToFrontCamera() {
        if (podVideoCall != null) {
            podVideoCall.switchToFrontCamera();
        }
    }

    public boolean isBackCamera() {
        if (podVideoCall != null) {
            return podVideoCall.isBackCamera();
        }
        return false;
    }

    public void switchToBackCamera() {
        if (podVideoCall != null) {
            podVideoCall.switchToBackCamera();
        }
    }


    public String turnOnVideo(long callId) {

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
            podVideoCall.endVideo();

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

    private void addVideoCallPartner(ChatResponse<JoinCallParticipantResult> response) {

        for (CallParticipantVO callParticipant :
                response.getResult().getJoinedParticipants()) {

            if (callParticipant.getUserId().equals(CoreConfig.userId)) {


                CallPartner.Builder lCallPartnerBuilder = new CallPartner.Builder();
                lCallPartnerBuilder.setPartnerType(PartnerType.LOCAL);
                lCallPartnerBuilder.setName("You");

                if (localPartnerView != null) {
                    visibleView(localPartnerView);
                    localPartnerView.setPartnerId(CoreConfig.userId);
                    localPartnerView.setPartnerName("You");
                    lCallPartnerBuilder.setVideoTopic(callParticipant.getSendTopicVideo());
                    lCallPartnerBuilder.setVideoView(localPartnerView);
                }
                podVideoCall.addPartner(lCallPartnerBuilder.build());

                //todo fire an event for local partner

            } else {

                String receiveVideoTopic = callParticipant.getSendTopicVideo();

                CallPartner.Builder rPartnerBuilder = new CallPartner.Builder();

                rPartnerBuilder.setPartnerType(PartnerType.REMOTE)
                        .setName(receiveVideoTopic + ":" + System.currentTimeMillis());

                if (hasRemotePartnerView()) {
                    visibleView(videoCallPartnerViews.get(0));
                    videoCallPartnerViews.get(0).setPartnerId(callParticipant.getUserId());
                    videoCallPartnerViews.get(0).setPartnerName(callParticipant.getParticipantVO() != null ? callParticipant.getParticipantVO().getName() : "");
                    rPartnerBuilder.setVideoTopic(receiveVideoTopic).setVideoView(videoCallPartnerViews.remove(0));
                }
                podVideoCall.addPartner(rPartnerBuilder.build());
                //todo fire an event for local partner
                listenerManager.callOnCallParticipantStartedVideo(response);
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
