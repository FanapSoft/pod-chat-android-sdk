## Synopsis

**Fanap's POD** Chat service

# Changelog
All notable changes to this project will be documented here.
##  Version [0.3.1.2] 28-11-2018
- In the `getHistory` function  lastMessageId and firstMessageIs is going to deprecate 
- [BugFixed]  Listener of `LeaveThread`
- [BugFixed]  NullpointerException on `ForwardMessage`
- [BugFixed]  Exception on `DeleteMessage`
- [BugFixed]  Update contact 
- [BugFixed]  Remove contact 
- The default of the `typeCode` has been set to "default".

##  Version [0.1.3.3] 28-11-2018
minor `BugFixed`
**improvement**  Connection state
-   [Changed] response model of the `onCreateThread`
-   [Changed] response model of the `OnAddContact`
-   [Added] Message Type to `Send text message`
-   [Added] Message Type to `Send file message`
-   [Added] `Type Code` in `connect`  and in the future release you will be able to set this atribute separately in all the methods too. 

-   [Added] `SeenMessageList`
-   [Added] `DeliveredMessageList`
-   [Added] `getThreadsWithCoreUserId`
-   [Added] `CreateThreadWithMessage` 



##  Version [0.1.3.2] 20-11-2018
-   [Changed] response model of `leaveThread`.
-   [Changed]  model of `ReplyInfo` :

```java{
ReplyInfoVO {
    private Participant participant;
    private long repliedToMessageId;
    private String repliedToMessage;
    private long messageType;
    private boolean deleted;
    private String systemMetadata;
    private String metadata;
    private String message;
}
```

-   [Changed] response model of `RemoveParticipant`
-   [Added] rerty for geting user info when the response is getting late
-   [Added] listener in `uploadImageFileMessage` when exception happens

##  Version [0.1.3.1] 14-11-2018
-   [Changed]  response model of the `uploadImage`
-   [Changed] response model of the `uploadFile`
-   [Add] `UploadImage` and `UploadFile` return `uniqueId`now.

##  Version [0.1.2.5] -2018-10-08
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
-   [Add]Remove participant
-   [Add]Add participant
-   [Add]Sync Contact listener
-   [Add]onChatState listener

## Version [0.0.7.0] -2018-07-22

-   Check Permission on SendFile and SyncContact 
-   UploadImage 
-   UploadFile 
-   Refactor SyncContact
-   Add Permission Class for request permission and check permission
-   Add FileServer param to Connect 

## Version [0.6.6.0] - 2018-07-18