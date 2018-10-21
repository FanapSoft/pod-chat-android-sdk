# Chat-Pod
A Chat helepr that works with Fanap's POD Chat service.

### Change Log
All notable changes to this project will be documented [here](https://github.com/Sinarahimi/Chat-Pod/blob/develop/CHANGELOG.md).

### Prerequisites

You need to Add this module to your project and after that set the `internet` permission in the manifest.
```java
<uses-permission android:name="android.permission.INTERNET" />
```
There is two another permission you need to add to your manifest 
```java
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
The first step is to initialize the Chat module.

```java
chat.init(context);
```
Then you need to connect.

```java
connect(String socketServerAddress,String appId,String  serverName,String token, String ssoHost, String platformHost, String fileServer)
chat.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080","http://172.16.106.26:8080")

```
And now it's ready for chat .

|Num|ThreadVo & Base Method           | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1|`createThread(int threadType, Invitee[] invitee, String threadTitle)`                |  Create the threadVo.                |
|2|`getHistory(int count, int offset, String order, long threadId)`         | get the history of the specific threadVo       |
|3|`getThreads(int count, int offset, ArrayList<Integer> threadIds, String threadName)`         | gets the list of threadVo       |
|4|`muteThread(int threadId)`         | Mute the threadVo      |
|5|`unmuteThread(int threadId)`         | Un Mute the threadVo      |
|6|`getThreadParticipants(int count, int offset, long threadId)`         | Get the participant list      |
|7|`addParticipants(long threadId, List<Long> contactIds)`         |  add participant of the group    |
|8|`removeParticipants(long threadId, List<Long> participantIds)`         |  remove participant of the group    |
|9|`leaveThread(long threadId)`         | leave any threadVo you want     |
|10|`logOutSocket()`    | log out of the socket.      |
|11|`renameThread(long threadId, String title)`                |  Rename the threadVo if you are the owner.                |
|12|`getUserInfo()`         | Get information about the current user        |     

### getUserInfo
```java
chat.getUserInfo();
```

### createThread
Id types :
```java
*int TO_BE_USER_SSO_ID = 1;
* int TO_BE_USER_CONTACT_ID = 2;
* int TO_BE_USER_CELLPHONE_NUMBER = 3;
* int TO_BE_USER_USERNAME = 4;
*/
```
Id can be :
USER_SSO_ID,CONTACT_ID,CELLPHONE_NUMBER or USERNAME.
```java
Invitee[] invite = new Invitee[]{new Invitee(id, idType)};
chat.createThread(0, invite, "");
```
or
```java
 Invitee[] invite = new Invitee[]{new Invitee(822, 2)
, new Invitee(577, 2)
, new Invitee(578, 2)
, new Invitee(824, 2)
                };
```

### getThreads
```java
chat.getThread(10, 0, [235,589]);
chat.getThread(10, 0, null);
```

### getHistory
```java
presenter.getHistory(50, 0, null, 312);
presenter.getHistory(50, 0, "desc", 312);
```
### getThreadParticipant
```java
chat.getThreadParticipants(50, 5, 235);
```

### renameThread
```java
chat.renameThread(379, "new group name");
```

### muteThread
```java
chat.muteThread(232);
```

### unmuteThread
```java
chat.unmuteThread(232);
```


⭐️

|Num|Message Method           | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1|`sendTextMessage(String textMessage, long threadId, String metaData,SendTextMessageHandler handler)`       | Send text message to threadVo.           |
|2|`forwardMessage(long threadId, ArrayList<Long> messageIds)`                 | Forward the message or messages.        |
|3|`replyMessage(String messageContent, long threadId, long messageId)`         | Reply the message in the threadVo       |
|4|`editMessage(int messageId, String messageContent)`         | Edit the message      |
|5|`sendFileMessage(Context context, String description, long threadId, Uri fileUri, String metadata)`         | Send file      |
|6|`deleteMessage(long messageId, Boolean deleteForAll)`         | delete the message     |
|7|`uploadFile(Context context, Activity activity, String fileUri, Uri uri)`         | Upload file      |
|8|`uploadImage(Context context, Activity activity, Uri fileUri)`         | Upload image      |
|9|`getFile(int fileId, String hashCode, boolean downloadable)`         | Get file with return url     |
|10|`getImage(int imageId, String hashCode, boolean downloadable)`         | Get image with return url     |

### replyMessage
```java
chat.sendReplyMessage("Reply to the text", 235, 532);
```

### editMessage
```java
chat.editMessage(533, "edited_at" + new Date().getTime());
```
### sendTextMessage
```java
chat.sendTextMessage(TEXT MESSAGE, THREAD_ID, META_DATA, new Chat.SendTextMessageHandler() {
 @Override
 public void onSent(String uniqueId, long threadId) {
 
 }
 });
```

### forwardMessage
```java
ArrayList<Long> messageIds = new ArrayList<>();
messageIds.add(11956L);
chat.forwardMessage(312, messageIds);
```

### replyMessage
```java
chat.replyMessage("Reply to the text", 235, 532);
```

### editMessage
```java
chat.editMessage(533, "edited_at" + new Date().getTime());
```


⭐️

|Num|Contact Method           | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1|`getContacts(int count, int offset)`         | get contact list      |
|2|`removeContact(long userId)`         | remove user in contact list      |
|3|`updateContact(String userId,String firstName, String lastName, String cellphoneNumber, String email)`| update user info in contact 
|4|`addContact(String firstName, String lastName, String cellphoneNumber, String email)`         | Add contact      |
|5|`syncContact(Context context, Activity activity)`         | Sync mobile's Contact with server contact      |
|6|`getBlockList(Integer count, Integer offset)`         |  get your block list    |
|7|`block(Long contactId)`         | block the contact     |
|8|`unblock(long blockId)`         | unblock the contact     |
|9|`searchContact(SearchContact searchContact)`         |  search through the contacts    |

### getContacts
```java
chat.getContact(50,0);
```

### removeContact
```java
chat.removeContact(long userId)
```

### addContact
```java
chat.addContact("Sina", "Rahimi", "0912131", "Develop.rahimi95@gmail.com");
```
### syncContact
```java
chat.syncContact(this, this);
```
### getContacts
```java
chat.getContact(50,0);
```

⭐️

|Num|Map Method           | Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1|`mapSearch(String searchTerm, Double latitude, Double longitude)`         | search in the map     |
|2|`mapRouting(String origin, String destination)`         | give you the direction     |




### Register Listener
After creating a Chat instance, you should call addListener method to register a ChatListener that receives Chat events.
ChatAdapter is an empty implementation of ChatListener interface.
For getting call back you should extend your class from `ChatAdapter`. 

###The table below is the list of callback methods defined in ChatListener interface.

|Num|ThreadVo & Base Method           | Listener Description       |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1| `onGetHistory()`| Called when history of the threadVo is return.      |
|2| `onGetThread()`       |  Called when get threadVos is return.          |
|3| `onMuteThread()`                 |Called when threadVo is muted.         |
|4| `onUnmuteThread()`         | Called when message is un muted.      |
|5| `onUserInfo()`         | Called when information of the user is return.     |
|6| `onCreateThread()`         |Called when threadVo is created.         |
|7| `onGetThreadParticipant()`         |Called when you want participants of the specific threadVo.         |
|8| `onRenameThread()`         |Called when you rename of the threadVo that you are admin of that       |
|9| `onChatState()`         | Return the last state of the chat      |
|10| `onThreadInfoUpdated()`         | Called when something change is the threadVo      |
|11| `onThreadRemoveParticipant()`         |Called when remove the participant of the group      |
|12| `onThreadLeaveParticipant()`         | Called when participant of the group leaves the threadVo      |
|13| `onThreadAddParticipant()`         |Called when contact added as a participant in the group       |
|14| `onError()`         |Called when something error happend       |

⭐️

|Num|Contact Method           | Listener Description       |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1| `onGetContacts()`| Called when get contacts is return.      |
|2| `onContactAdded()` |Called when contact added to your contact       |
|3| `onGetBlockList()`         |Called when list of block contacts are received        |
|4| `onBlock()`         |Called when contact was blocked       |
|5| `onUnBlock()`         |Called when contact was unblocked      |
|6| `onSearchContact()`         |Called when        |
|7| `onRemoveContact()`         |Called when you want to remove contact       |
|8| `onSyncContact()`         |Called your phone contact sync to the server contact       |
|9| `onUpdateContact()`         |Called when update contact received       |

⭐️

|Num|Map Method           | Listener Description       |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1| `onMapSearch()`         |       |
|2| `onMapRouting()`         |       |

⭐️

|Num|Message Method                 | Listener Description                                                                            |
|:--|:------------------------------|:---------------------------------------------------------------------------------------|
|1| `onDeliver()`   | Called when message is delivered.       |
|2| `onSeen()`                |  Called when message is seen.  |
|3| `onSent()`         | Called when message is sent.       |
|4| `onEditedMessage()`         |Called when message edited       |
|5| `onDeleteMessage()`         | Called when message delited      |
|6| `onNewMessage()`         |Called when new message recived      |
|7| `onUploadFile()`         |Called when file uploaded       |
|8| `onUploadImageFile()`         | Called when image uploaded      |


## Built With :heart:

* [moshi](https://github.com/square/moshi) - Moshi
* [websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) - Websocket
* [lifecycle](https://developer.android.com/reference/android/arch/lifecycle/LiveData) - LiveData
* [Retrofit2](https://square.github.io/retrofit/) - Retrofit2
* [Rxjava](https://github.com/ReactiveX/RxAndroid) - Rxjava

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Contact Me

If you have ideas or feedback, feel free to open up issues, put up pull reqeusts, and  [contact me directly](mailto:develop.rahimi95@gmail.com).