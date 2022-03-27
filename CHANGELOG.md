## Synopsis

**Fanap's POD** Chat service

# Changelog
All notable changes to this project will be documented here.

## [0.9.17.0] -2021-**-**

### Added

 You could use following events and methods to be aware of camera is on, the device is near ( to the ear ) or the device got far ( from the ear ) : 

- add ChatListener.onDeviceIsNear
- add ChatListener.onDeviceGotFar
- add Chat.isCameraOn
  
### Changed

- update podcall version to 3.7.2-SNAPSHOT



## [0.9.16.0] -2021-**-**

 When you join a started call, you will inform that if a screen is sharing or call is recording


## [0.9.15.0] -2021-2-23

### Added

 It's possible now to set title, description, metadata, uniqueName and image for a group call:

- CallRequest.title
- CallRequest.description
- CallRequest.metadata
- CallRequest.image
- CallRequest.uniqueName

 Get a list of active calls you can join:

  ```Chat.getActiveCalls(GetActiveCallsRequest)``` method
  ```Chat.onReceiveActiveCalls(ChatResponse<GetActiveCallsResult>)``` callback


### Changed

 Update podCall version to 3.5.6

 The initial delay when starting a call is now reduced

 Chat.handleOnCallParticipantCanceledCall policy. This event is now also sent to the client who has canceled ( rejected ) incoming group call.

### Fixed

 Contacts... [Write here]
## [0.9.**.**] -20**-**-**

### Added

 ```Chat.useCallPartnerViewManager()```

 ```addView(@NonNull CallPartnerView... partnerView);```

 ```setAutoGenerate(boolean isAutoGenerate);```

 ```setAsScreenShareView(@NonNull CallPartnerView screenShareView);```

 ```setAsCameraPreview(@NonNull CallPartnerView cameraPreview);```

 ```getScreenShareView();```

 ```getPartnerAssignedView(partnerUserId);```

 ```getPartnerUnAssignedView(partnerUserId);```

 ```setAutoGenerateCallback(CallPartnerViewManager.IAutoGenerate);```

 ```CallPartnerViewManager.addView(List<CallPartnerView>)```

 ```CallPartnerViewManager.showMuteIcon(partnerUserId) ```

 ```CallPartnerViewManager.hideMuteIcon(partnerUserId) ```

``` CallPartnerViewManager.showCameraIsOff(partnerUserId) ```

 ```CallPartnerViewManager.hideCameraIsOff(partnerUserId)``` 

 ```CallPartnerViewManager.releasePartnerView(partnerUserId)```

 ```CallPartnerViewManager.releaseScreenShareView()```

 ```CallPartnerViewManager.showPartnerName(userId, name)```

 ```MainThreadExecutor```




## [0.9.14.0] -2021-12-26

### Added

 ```startShareScreen``` callback

 ```onShareScreenStarted``` method

 ```endShareScreen``` callback

 ```onShareScreenEnded``` method

 ```startCallRecord``` method

 ```onCallRecordStarted``` callback

 ```endCallRecord``` method

 ```onCallRecordEnded``` callback

 ```ChatListeners.onCallClientErrors(ChatResponse<CallClientErrorsResult>)``` callback
 
 ```SearchSystemMetadataRequest```

 ```searchHistory(SearchSystemMetadataRequest, ChatHandler)```


### Changed

 Improved bluetooth headset detection
 
 Update podcall to 3.5.2


### Deprecated
 
 NosqlListMessageCriteriaVO

 ```searchHistory(NosqlListMessageCriteriaVO,ChatHandler)```



## [0.9.13.0] -2021-10-18

### Added

```Chat.addPartnerView(CallPartnerView,int)```

```onAnotherDeviceAcceptedCall``` callback

```VideoCallParam.CameraId```

```Chat.switchToFrontCamera```

```Chat.switchToBackCamera```

```Chat.isBackCamera```

```Chat.isFrontCamera```


### Changed

- Update podcall version to 3.4.3


### Fixed

- audio call concurrent exception

- back camera rotation

- crash when making large number of calls

- bug to stop displaying CallPartnerView on Activity.onPause


### Deprecated

 ```Chat.switchCamera```

 ```Chat.endAudioCall```

 ```Chat.terminateAudioCall```



## [0.9.12.0] -2021-10-4

### Added

- partnerId to CallPartnerView

- CallPartnerView.setPartnerName(String partnerName)

- CallPartnerView.setDisplayName(boolean displayName)

- CallPartnerView.setDisplayCameraIsOffIcon(Boolean displayCameraIsOffIcon)

- CallPartnerView.setDisplayIsMuteIcon(Boolean displayIsMuteIcon)

- ArrayList<CallParticipantVO> to CallStartResult. only userId, mute and video fills in CallParticipantVO.


### Fixed

- VideoCall minor bugs

### Changed

- Improve group call

- CallActivity UI




## Version [0.9.11.0] -2021-8-21

[Added] **Video Call**

[Improved] **Audio Call**

[Added] Permission to use the camera

[َAdded] setupCall(VideoCallParam, AudioCallParam, CallConfig, List<CallPartnerView>)

[Added] requestCall(CallRequest) method

[Added] acceptVoiceCall(AcceptCallRequest) method

[Added] onCallCreated() callback

[Added] onCallDelivered(ChatResponse<CallDeliverResult>) callback

[Added] onReceiveCallRequest(ChatResponse<CallRequestResult>) callback

[Added] onVoiceCallStarted(ChatResponse<CallStartResult>) callback

[Added] rejectVoiceCall(RejectCallRequest) method

[Added] onCallRequestRejected(ChatResponse<CallRequestResult>) callback

[Added] endAudioCall(EndCallRequest) method

[Added] onVoiceCallEnded(ChatResponse<EndCallResult>) callback

[Added] onEndCallRequestFromNotification() callback

[Added] onCallReconnect(ChatResponse<CallReconnectResult>) callback

[Added] onCallConnect(ChatResponse<CallReconnectResult>) callback

[Added] MessageVO.getCallHistoryVO();

[Added] getCallsHistory(GetCallHistoryRequest) method

[Added] onReceiveCallHistory(ChatResponse<GetCallHistoryResult>) callback

[Added] switchCallMuteState(Boolean,CallId) method

[Added] onAudioCallMuted(ChatResponse<CallParticipantMuteResult>) callback

[Added] onAudioCallUnMuted(ChatResponse<CallParticipantMuteResult>) callback

[Added] requestMuteCallParticipant(MuteUnMuteCallParticipantRequest) method

[Added] onCallParticipantMuted(ChatResponse<CallParticipnatMuteResult>) callback

[Added] requestUnMuteCallParticipant(MuteUnMuteCallParticipantRequest) method

[Added] onCallParticipantUnMuted(ChatResponse<CallParticipnatMuteResult>) callback

[Added] onMutedByAdmin(ChatResponse<CallParticipantMuteResult>) callback

[Added] onUnMutedByAdmin(ChatResponse<CallParticipantMuteResult>) callback

[Added] switchCallSpeakerState(Boolean) method

[Added] openCamera() method

[Added] closeCamera() method

[Added] switchCamera() method

[Added] turnOnVideo(CallId) method

[Added] turnOffVideo(CallId) method

[Added] turnCallParticipantVideoOff(TurnCallParticipantVideoOffRequest) method

[Added] onCallParticipantStoppedVideo(ChatResponse<JoinCallParticipantResult>) callback

[Added] onCallParticipantStartedVideo(ChatResponse<JoinCallParticipantResult>) callback

[Added] getCallParticipants(GetCallParticipantsRequest)

[Added] onActiveCallParticipantsReceived(ChatResponse<GetCallParticipantResult>) callback







## Version [0.9.10.0] -2021-7-17

[Added] ```getMutualGroup``` method

[Added] ```onGetMutualGroups``` callback

[Added] ```getContacts``` => add username to RequestGetContact


## Version [0.9.9.0]
[BugFixed] Failure to receive history in specific situations


## Version [0.9.8.2]

[Added] ```createTag``` method

[Added] ```onTagCreated``` callback

[Added] ```editTag``` method

[Added] ```onTagEdited``` callback

[Added] ```deleteTag``` method

[Added] ```onTagDeleted``` callback

[Added] ```addTagParticipant``` method

[Added] ```onTagParticipantAdded``` callback

[Added] ```removeTagParticipant``` method

[Added] ```onTagParticipantRemoved``` callback

[Added] ```getTagList``` method

[Added] ```onTagList``` callback



## Version [0.9.8.1] -2021-4-28

[BugFixed] NullPointerException on reconnect


## Version [0.9.8.0] -2021-4-27

[Added] ```getUserBots``` method

[Added] ```onUserBots``` callback

[Added] ```blockAssistant``` method

[Added] ```onAssistantBlocked``` callback

[Added] ```unBlockAssistant``` method

[Added] ```onAssistantUnBlocked``` callback

[Added] ```getBlocksAssistant``` method

[Added] ```onAssistantBlocks``` callback

[BugFixed] Database lock exception.

[Improvement] Prevented from additional reconnect requests at the same time. 

[Improvement] If the ```NetworkStateConfig``` is not set, the default mode uses.


## Version [0.9.7.0] -2021-2-24

[Added] ```getAssistantHistory``` method

[Added] ```onGetAssistantHistory``` callback

[Added] ```changeThreadType``` method

[Added] ```onThreadTypeChanged``` callback

[Added] ```getHashTagList``` method

[Added] ```onGetHashTagList``` callback

[Fixed] Sentry NativeLib Exception

[Fixed] Minor bugs fixed



## Version [0.9.6.0] -2021-1-26

[Deprecated] removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler)

[Added] Invitee to RemoveParticipantRequest 

[Added] ability to removeParticipants with Invitee (userId and coreUserId)

[Added] ```registerAssistant``` method

[Added] ```onRegisterAssistant``` callback

[Added] ```deactiveAssistant``` method

[Added] ```onDeActiveAssistant``` callback

[Added] ```getAssistants``` method

[Added] ```onGetAssistants``` callback

        
        

 


## Version [0.9.5.0] -2020-12-5

[Added] Accept call in mute state.

[BugFixed] Minor bugs fixed.

## Version [0.9.4.0] -2020-12-5

[Changed] ```onGetThread```, Match the content of all onGetThread events


## Version [0.9.4.0-beta] -2020-11-25

[Added] ```closeChat```, close chat socket

[Added] ```resumeChat```, open and connect chat socket


## Version [0.9.3.1] -2020-11-22

[Added] ```isSentryLogActive```

[Added] ```isSentryResponseLogActive```


## Version [0.9.3.0] -2020-11-02

[Added] ```closeThread```

[Added] ```safeLeaveThread```

[Added] ```shouldKeepHistory``` to ```RequestLeaveThread``` and ```SafeLeaveRequest```

[Added] ChatMessageType, STICKER

[Added] ```addGroupCallParticipant```

[Added] ```removeGroupCallParticipant```

[Added] ```onCallParticipantLeft``` callback

[Added] Group image to notification

[Changed] ```isInCache``` to ```isAvailableInCache```

[BugFixed] ```deleteMultipleMessage```




## Version [0.9.1.0] -2020-10-07

[Added] **Audio Call**

[Added] ```getChatState()```

[Added] New Message Type 'STICKER'



## Version [0.9.0.0] -2020-9-22

[Improved] Contacts Cache Performance

[Improved] Messages Cache Performance

[Added] ```isInCache(RequestGetPodSpaceFile)``` or ```isInCache(RequestGetPodSpaceImage)``` to check if a file (or image) exists in cache or not.

[Added] ```sendStatusPing(StatusPingRequest)``` to locate status in chat. eg. in thread list, in a thread or in contacts list.








## Version [0.8.3.0] -2020-8-26

[Improved] Threads Cache Performance 

[Fixed] Threads Cache Issues




## Version [0.8.2.0] -2020-8-15

[Added] ```contacSynced``` to ```UserInfo```

[Removed] Saving log to text file

[BugFixed] Cache New Message



## Version [0.8.1.0] -2020-7-28

- Minor bugs fixed




## Version [0.8.0.0] -2020-7-14

[Added] ```createBot(CreateBotRequest)```

[Added] ```addBotCommand(DefineBotCommandRequest)```

[Added] ```startBot(StartAndStopBotRequest)```

[Added] ```stopBot(StartAndStopBotRequest)```



## Version [0.7.4.1] -2020-7-8

[Added] Sentry Logger

[Improved] Connection Performance. Disconnection is detected faster in api level 21 and below



## Version [0.7.4.0] -2020-7-4

[Improved] Push Notification Grouping, all notifications are grouped by thread.

[Added] ```deliverNotification(threadId)```, marks thread notifications as read.

[Added] ```clearAllNotifications()```, removes all notifications from notification bar.

[Added] Upload private files by ```setPublic(false)``` in RequestUploadFile and RequestUploadImage.




## Version [0.7.3.0] -2020-6-16

[Improved] Handle Thread Info Update Events

[BugFixed] Connection problem on Android 5 and below



## Version [0.7.2.0] -2020-6-2

[Improved] Upload Thread Image

[Improved] Search Contacts 



## Version [0.6.6.2] -2020-5-21

[Added] FCM Push Notification Support

[Removed] retryUploadPodSpace



## Version [0.6.6.0] -2020-5-19

[Add] Crop picture parameters before upload

[Add] Download picture option size, quality and crop

[Improve] Upload file and pictures

[Improve] Sync large numbers of contacts

[BugFix] minor bugs fixed





## Version [0.6.5.4] -2020-5-10

[Added] getHistory by messageType added

[Removed] Unused permissions have been removed

[Improved] All Exceptions have been handled





## Version [0.6.5.0] -2020-5-8

[Added] podSpaceServer to RequestConnect

[Added] userGroupHash to Thread

[Added] New TextMessageTypes

[Changed] following functions needs userGroupHash:
        
        sendFileMessage
        
        createThreadWithFile
        
        replyFileMessage
        
        sendLocationMessage
        
[Added] retryUploadPodSpace

[Added] getFile(RequestPodSpaceFile,ProgressHandler)

[Removed] Notification removed

[Changed] Remove Permissions from sdk manifest

[BugFixed] minor bug fixed


## Version [0.6.4.8] -2020-5-3
[BugFixed] Room integrity exception handled

[BugFixed] minor bug fixed 



## Version [0.6.4.7] -2020-5-1
[Added] logger and shareLogs function

[BugFixed] leaveThread cache bug fixed




## Version [0.6.4.6] -2020-4-28
[BugFixed] syncContact Contact Version Problem fixed


## Version [0.6.4.5] -2020-4-27

[Changed] cache is enabled offline now



## Version [0.6.4.4] -2020-4-26

[Added] Customizable Notification

[BugFixed] minor bug fixed




## Version [0.6.4.2] -2020-4-22

[Improved] cache I/O operations part 2

[Improved] optimize syncContact



## Version [0.6.4.0] -2020-4-21

[Added] messageType is required in replyMessage and replyFileMessage

[Added] setUploadTimeoutConfig(TimeoutConfig) and setDownloadTimeoutConfig(TimeoutConfig)

[Improved] cache I/O operations

[BugFixed] uniqueId added to isNameAvailable response

[BugFixed] sending repetitive file message

[BugFixed] cacheReplyInfo updated in getHistory and getThreads







## Version [0.6.3.0] -2020-4-7

[Added] Add Participant with coreUserId

[Improvement] VPN Connection management

[Improvement] Reconnect

[Added] ChatProfile to Participant

[Added] Cache for `getCurrentRoles`

[Added] Gif files sends as file in `sendFileMessage`, `replyFileMessage` and `createThreadWithFile`

[Added] OTP login example project




## Version [0.6.2.0] -2020-3-17

[Added] Notification Service

[Added] mute parameter to `getUnreadMessagesCount`

[BugFixed] `syncContacts` repetitive numbers




## Version [0.6.1.1] -2020-3-14

[Added] Ability to disable cache per function

[Added] `isNameAvailable`

[Added] `createPublicThread`

[Added] `joinPublicThread`

[Added] `getUnreadMessagesCount`





#Version [0.6.1.0] -2020-3-4


[Added] addParticipants with username

[Changed] getBlockList,block and unBlock result from Contact to BlockedContact

[Added] coreUserId, userId and contactId to ResultBlockList

[Changed] messageType is required in `sendFileMessage` , `createThreadWithFile` and `createThreadWithMessage`

[Added] `setCacheDirectory(File)`

[Added] uploaded file url to uploadImage, uploadFile, uploadFileProgress and uploadImageProgress response

[Added] `setMaxReconnectTime(long)`







#Version [0.5.5.0] -2020-2-19

[Added] `addContact` with username

[Added] `newMessages` Builder function to `RequestThread` model

[Added] `updateChatProfile` function

[Added] `onContactsLastSeenUpdated` event

[Added] `setFreeSpaceThreshold` function





## Version [0.5.4.0] -2020-2-4

[Added] `stopTyping()`

[Removed] `setSignalIntervalTime()`

[Added] `getFile(RequestGetFile, ProgressHandler.IDownloadFile)`

[Added] `getImage(RequestGetImage, ProgressHandler.IDownloadFile)`

[Added] `getCacheSize()`

[Added] `getStorageSize()`

[Added] `getCachedFilesFolderSize()`

[Added] `getCachedPicturesFolderSize()`

[Added] `clearCacheDatabase(IClearMessageCache)`

[Added] `clearCachedFiles()`

[Added] `clearCachedPictures()`

[Added] `cancelDownload()`

[Deprecated] `stopTyping(String)`







## Version [0.5.3.0] -2020-1-25

[Changed]  Invitee
	      - long id => + String id
	       int idType


[Added] Thread
	 +PinMessageVO

[Added] RequestPinMessage
	 + messageId
	 + notifyAll

[Added] pinMessage(RequestPinMessage)

[Added] unPinMessage(RequestPinMessage)

[BugFixed] OnSignalMessageReceive(OutputSignalMessage) subjectId value changed

[Added] onSignalMessageReceived(OutputSignalMessage)

[Added] onSignalMessageReceived(ChatResponse<ResultSignalMessage>)

[Added] getCurrentUserRoles(RequestGetUserRoles)

[Added] getMentionList(RequestGetMentionList)





## Version [0.5.2.0] -2020-1-12
- Cache Structure Improved
- [ADDED] pinThread() and unPinThread functions and onPinThread onUnPinThread events
- [CHANGED] RequestAddAdmin => RequestSetAdmin
- [CHANGED] addAdminRoles => addAdmin(RequestSetAdmin)
- [CHANGED] removeAdminRoles => removeAdmin(RequestSetAdmin)
- [REMOVED] roleOperation from RequestRole
-
- [ADDED] RequestSetAuditor request = new RequestSetAuditor
                .Builder(Long, ArrayList<RequestRole>)
                .build();

	chat.addAuditor(request);

	chat.removeAuditor(request);



## Version [0.5.1.0] -2019-12-20
- [BugFixed] editMessage() and sendFileMessage() metadata bug fixed
- [ADDED] Network Connection State Manager


## Version [0.4.2.402] -2019-12-1
- [BugFixed] Duplicated Messages
- [Added] Set Auditor for p2p conversationVO
- [Added] getHistory with uniqueId
- [Added] getHistory from cache
- Waiting queue update before getHistory()

## Version [0.4.2.2] -2019-6-8
- [Added] Admin services cache (update cache on getAdminList, addAdminRoles and removeAdminRoles)
- [Added] startTyping() and stopTyping()
- [Added] getNotSeenDuration()
- [Added] clearHistory()
- [Added] onGetThreadAdmin(String, OutPutParticipant) in ChatListener
- [Added] delete multiple message ability to deleteMessage()
- Separate addAdminRoles() and removeAdminRoles()
- getAdminList() output resolved.
- requestSearchContact() problem resolved.
- upload progress bug resolved. 




## Deprecation Methods
|Num|Deprecated Methods          | Description                                                            |
|:--|:------------------------------|:-----------------------------------------------------------------------------------|
|1|`createThread(int threadType, Invitee[] invitee, String threadTitle)` |  Create the threadVo.|
|2|`getHistory(int count, int offset, String order, long threadId)` | get the history of the specific threadVo|
|3|`getThreads(int count, int offset, ArrayList<Integer> threadIds, String threadName)`| gets the list of threadVo|
|4|`muteThread(int threadId)` | Mute the threadVo   |
|5|`unmuteThread(int threadId)` | Un Mute the threadVo  |
|6|`getThreadParticipants(int count, int offset, long threadId)`  | Gets the participantVO list      |
|7|`addParticipants(long threadId, List<Long> contactIds)`  |  adds participantVO of the group    |
|8|`removeParticipants(long threadId, List<Long> participantIds)` |  removes participantVO of the group    |
|9|`leaveThread(long threadId)` |  removes participantVO of the group  |
|10|`logOutSocket()`    | log out of the socket.  |
|11|`renameThread(long threadId, String title)` |  Rename the threadVo if you are the owner. |
|4|`muteThread(int threadId)`         | Mute the threadVo      |
|5|`unmuteThread(int threadId)`         | Un Mute the threadVo      |
|6|`getThreadParticipants(int count, int offset, long threadId)`         | Gets the participantVO list      |
|7|`addParticipants(long threadId, List<Long> contactIds)`         |  adds participantVO of the group    |
|8|`removeParticipants(long threadId, List<Long> participantIds)`         |  removes participantVO of the group    |
|9|`leaveThread(long threadId)`         | leave any threadVo you want     |
|11|`renameThread(long threadId, String title)`  |  Rename the threadVo if you are the owner. |
|12|`getUserInfo()`         | Get information about the current user        |
|7|`@deprecated uploadFile(Context context, Activity activity, String fileUri, Uri uri)`         | Upload file      |
|8|`@deprecated uploadImage(Context context, Activity activity, Uri fileUri)`         | Upload image      |
|5|`@Deprecated sendFileMessage(Context context, String description, long threadId, Uri fileUri, String metadata)`| Send file with message|
|2|`forwardMessage(long threadId, ArrayList<Long> messageIds)`                 | Forward the message or messages.        |
|3|`replyMessage(String messageContent, long threadId, long messageId)`         | Reply the message in the threadVo       |
|4|`editMessage(int messageId, String messageContent)`         | Edit the message      |
|6|`deleteMessage(long messageId, Boolean deleteForAll)`         | delete the message     |
|1|`@Deprecated  sendTextMessage(String textMessage, long threadId, String systemMetaData,SendTextMessageHandler handler)`| Send text message to threadVo.|
|1|`mapSearch(String searchTerm, Double latitude, Double longitude)`         | search in the map     |
|2|`mapRouting(String origin, String destination)`         | give you the direction     |



## Deprecation Methods
|Num|Deprecated Methods          | Description                                                            |
|:--|:------------------------------|:-----------------------------------------------------------------------------------|
|1|`createThread(int threadType, Invitee[] invitee, String threadTitle)` |  Create the threadVo.|
|2|`getHistory(int count, int offset, String order, long threadId)` | get the history of the specific threadVo|
|3|`getThreads(int count, int offset, ArrayList<Integer> threadIds, String threadName)`| gets the list of threadVo|
|4|`muteThread(int threadId)` | Mute the threadVo   |
|5|`unmuteThread(int threadId)` | Un Mute the threadVo  |
|6|`getThreadParticipants(int count, int offset, long threadId)`  | Gets the participantVO list      |
|7|`addParticipants(long threadId, List<Long> contactIds)`  |  adds participantVO of the group    |
|8|`removeParticipants(long threadId, List<Long> participantIds)` |  removes participantVO of the group    |
|9|`leaveThread(long threadId)` |  removes participantVO of the group  |
|10|`logOutSocket()`    | log out of the socket.  |
|11|`renameThread(long threadId, String title)` |  Rename the threadVo if you are the owner. |
|4|`muteThread(int threadId)`         | Mute the threadVo      |
|5|`unmuteThread(int threadId)`         | Un Mute the threadVo      |
|6|`getThreadParticipants(int count, int offset, long threadId)`         | Gets the participantVO list      |
|7|`addParticipants(long threadId, List<Long> contactIds)`         |  adds participantVO of the group    |
|8|`removeParticipants(long threadId, List<Long> participantIds)`         |  removes participantVO of the group    |
|9|`leaveThread(long threadId)`         | leave any threadVo you want     |
|11|`renameThread(long threadId, String title)`  |  Rename the threadVo if you are the owner. |
|12|`getUserInfo()`         | Get information about the current user        |
|7|`@deprecated uploadFile(Context context, Activity activity, String fileUri, Uri uri)`         | Upload file      |
|8|`@deprecated uploadImage(Context context, Activity activity, Uri fileUri)`         | Upload image      |
|5|`@Deprecated sendFileMessage(Context context, String description, long threadId, Uri fileUri, String metadata)`| Send file with message|
|2|`forwardMessage(long threadId, ArrayList<Long> messageIds)`                 | Forward the message or messages.        |
|3|`replyMessage(String messageContent, long threadId, long messageId)`         | Reply the message in the threadVo       |
|4|`editMessage(int messageId, String messageContent)`         | Edit the message      |
|6|`deleteMessage(long messageId, Boolean deleteForAll)`         | delete the message     |
|1|`@Deprecated  sendTextMessage(String textMessage, long threadId, String systemMetaData,SendTextMessageHandler handler)`| Send text message to threadVo.|
|1|`mapSearch(String searchTerm, Double latitude, Double longitude)`         | search in the map     |
|2|`mapRouting(String origin, String destination)`         | give you the direction     |




##  Version [0.4.2.1] -2019-3-5
- Added Queue Message
- Removed liveData library and Logger library in order to prevent some issue like size of the apk
- Added SetRole to Admin (Both of the Remove and Add rule to admin are have a same method )
- Added Get Admin List
- Added Signal Message like `Is_Typing` or `recording_voice`


##  Version [0.4.1.0] -2019-3-5
-  Added Message Queue to
  *  Reply message
   - send text message
   -  file message
   - reply file message
-  The methods of the UploadImageProgress's progress interface has been changed to ` default void`
- [Added]  ERROR_CODE_INVALID_FILE_URI = 6010
- [Removed] context attribute removed from `syncContact` method
- - gradle Updated
-[ Added] block method has 2 new attribute
 - [Added] unblock method has 2 new attribute , First attribute of unblock was changed to Long
 - [Deprecated]
  these fields are deprecated from `Thread` object
{
    private long lastSeenMessageId;
    private long partnerLastSeenMessageId;
    private long partnerLastDeliveredMessageId;
    private long partnerLastMessageId;
}
[Notice] You have to add `maven { url "https://s3.amazonaws.com/repo.commonsware.com" }`
to your app Module


- [Add] The responses of this listeners were changed 
onGetContacts
onGetHistory
onGetThread
onBlock
onUnBlock
onSeen
onDeliver
onSent
onNewMessage
onDeleteMessage
onThreadAddParticipant
onUpdateThreadInfo

##  Version [0.1.2.15] -2018-10-21
-   [Add]In order to receive a response by UpdateThreadInfo,
 you can use onUpdateThreadInfo listener. 
And beaware that you just see updated fields not the entire conversationVO.
- [bug fix] `onNewmessage`
- [add] image to `inviter` model
- [add] `messageType1 to messageVo model
- Remove all `Serialized` from all of the models
- [bug fix] on `getForwardMessage`

##  Version [0.1.2.10] -2018-10-19

-   [Add] Now getHistory and getThreads have the same response model.
-   [Add] The `updateThreadInfo` function can accept title now.
-   [Add] We added `uniqueId` to [ErrorOutPut,]
-   [Add] We added instant `uniqueId` to `sendFileMessage,deleteMessage,replyMessage,forwardMessage
          seenMessage`.
-   [BugFix] Bug has been fixed in sendText message when put null in handler. 

##  Version [0.1.2.6] -2018-10-08
-   [Add] The project has been added to Maven
-   [Add] Unique id was added to Most of the functions as return. 
-   [Add] Unique id was added to Most responses.

##  Version [0.1.2.5] -09/26/2018
-   [BugFix] OnError Listener's bug has been fixed.
-   [BugFix] The bug of setting `count` parameter in `GetContact` function has been fixed.
-   [Add] Async has *ASYNC_READY* state now.
-   [Add] Add `lastMessageId` and `firstMessageId` to the *GetHistory*
-   [Replace] Replace most of the *integer* params to *long*.
-   [Removed] CHAT_READY state removed from Async and added to Chat and state changes 
     to *CHAT_READY* when response of the *getUserInfo* arrives.
-   [BugFix]  Some fields has been added to `createThread`'s response
-   [Add]  Now you can get instant unique id when you send text messages
-   [Add]  If you want to disable/hide logs for output set *isLoggable* false.
-   [Add]  If you want to disable *cache* set *cache* attribute as false.
-   [Deprecated] The `renameThread` is going to deprecated in the next version.

## Version [0.1.2.4] -2018-09-15
-   [BugFix]OnError Listener
-   [BugFix]Create ThreadVo 

## Version [0.1.2.3] -2018-09-03
-   [Add]Implement Cache for get Contact
-   [Add]Update ThreadVo Info
-   [Add]Get file
-   [Add]Get Image
-   [BugFix]You can get CHAT_READY on Live State

## Version [0.1.2.1] -2018-08-19
-   [Add]Map Routing
-   [Add]Map Search
-   [Add]Block
-   [Add]Unblock
-   [Add]GetBlockList
-   [Add]Search Contact
-   [Add]Search History

## Version [0.0.7.1] -2018-07-30

-   [Add]Delete Message
-   [Add]onThreadInfoUpdated listener
-   [Add]onLastSeenUpdated listener
-   [Add]Search in threadVos with name:
    We sdd a new param to the getThread so you can search through threadVos by their name. 
-   [Add]Remove participantVO
-   [Add]Add participantVO
-   [Add]Sync Contact listener
-   [Add]onChatState listener

## Version [0.0.7.0] -2018-07-22

-   Check Permission on SendFile and SyncContact 
-   UploadImage 
-   UploadFile 
-   Refactor SyncContact
-   Add Permission Class for request permission and check permission
-   Add FileServer param to Connect 
