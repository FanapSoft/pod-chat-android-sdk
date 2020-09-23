
<img src="https://avatars2.githubusercontent.com/u/25844347?s=200&v=4"/>

# **Fanap's Podchat Android SDK**

> a chat sdk for android

**Contents**

- ***Installation***

- ***Connect***

- ***Add a contact***

- ***Create a thread***

- ***Send Message to thread***

- ***FAQ***



## Installation

#### Project build.gradle

    allprojects {
     repositories {
        jcenter()
        google()
        maven { url "https://s3.amazonaws.com/repo.commonsware.com" }
     }
    }


#


#### App build.gradle ![Build Status](https://img.shields.io/bintray/v/farhad7d7/maven/chat?style=plastic)


    implementation("com.fanap.chat:podchat:$chatSdkVersion")






#


#### Initial

```
    Chat chat = Chat.init(Context);


    chat.isCacheables(true); //use caching

    chat.isLoggable(true); //log request and response

    chat.rawLog(true); //log everything

```

   
#


#### Connect

```
    RequestConnect rc = new RequestConnect.Builder(
                    socketAddress, 
                    APP_ID,
                    serverName,
                    TOKEN,
                    ssoHost,
                    platformHost,
                    fileServer,
                    podSpaceServer)
                .build();


     chat.addListener(new ChatListener() {

            @Override
            public void onChatState(String state) {

                if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY))
                {

                    chatReady = true;

                    Log.i(TAG, "Hello there!");

                    Log.d(TAG, "Chat Is Ready! :) ");

                } 
                else 
                {
                    chatReady = false;

                    Log.d(TAG, "Connecting...");

                }
            
            }

            @Override
            public void onError(String content, ErrorOutPut error) {

                Log.d(TAG, "Ops :'( " + content);
                Log.d(TAG, "Error Code: " + error.getErrorCode());
                Log.d(TAG, "Cause: " + error.getErrorMessage());
                Log.d(TAG, "UniqueID: " + error.getUniqueId());

            }
        });




    chat.connect(requestConnect);

```
#


#### Add Contacts

> Add a contact to your chat contacts list


```
RequestAddContact request = new RequestAddContact.Builder()
                    .firstName("John") 
                    .lastName("Doe")

// add with their username in chat 

//                   .username("j.doe123") 
// or
//                   .email(john.doe@example.com)
// or
//                   .cellphoneNumber(09*********)

                    .build();

chat.addContact(request);
```

>One of firstName or lastName is required.

*Callback:*

    onContactAdded(String content, ChatResponse<ResultAddContact> response)


#

#### Create a thread

```

List<Invitee> invite = new ArrayList<>();

invite.add(new Invitee("j.doe123", InviteType.Constants.TO_BE_USER_USERNAME));

```
Other inviteType:

    TO_BE_USER_SSO_ID,
    TO_BE_USER_CONTACT_ID,
    TO_BE_USER_CELLPHONE_NUMBER,
    TO_BE_USER_ID,


```

RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.Constants.NORMAL, invite)
                .title("Sample Thread")
                .withDescription("This is description")
                .withMetadata("
                                    {
                                      "sampleKey1": 1998,
                                      "sampleKey2": "some special info"
                                    }
                               ")
                .setUploadThreadImageRequest(requestUploadImage)
                .build();


chat.createThread(requestCreateThread)

```

>.setUploadThreadImageRequest(requestUploadImage) set an image for thread:

```
 RequestUploadImage requestUploadImage = new RequestUploadImage.Builder(Activity, Uri)
                        //crop parameters:
                       .sethC(140) //image height
                       .setwC(140) //image width
                       .build();

```



*Callback:*

```
onCreateThread(String content, ChatResponse<ResultThread> response)
```

#

#### Send a Message to thread

```

RequestMessage req = new RequestMessage.Builder("Hello World!", threadId)
                    .messageType(TextMessageType.Constants.TEXT)
                    .build();


chat.sendTextMessage(req, null);

```

*Callbacks:*
```
onSent(String content, ChatResponse<ResultMessage> chatResponse) //when the message is sent successfully.
 
onDeliver(String content, ChatResponse<ResultMessage> chatResponse) //when the message is seen by partner.
```