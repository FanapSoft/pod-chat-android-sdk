package com.fanap.podchat.util;

public class ChatConstant {

    public static final String POD_CALL_INFO = "CALL_INFO";
    public static final String POD_PUSH_THREAD_ID = "threadId";
    public static final String POD_PUSH_MESSAGE_ID = "messageId";
    public static final String POD_PUSH_SENDER_USER_NAME = "senderUserName";

    public static final int ERROR_CODE_INVALID_PARAMETER = 6000;
    public static final int ERROR_CODE_INVALID_TOKEN = 6001;
    public static final int ERROR_CODE_USER_NOT_FOUND = 6002;
    public static final int ERROR_CODE_CHAT_READY = 6003;
    public static final int ERROR_CODE_CURRENT_DEVICE = 6004;
    public static final int ERROR_CODE_CHECK_URL = 6005;
    public static final int ERROR_CODE_READ_CONTACT_PERMISSION = 6006;
    public static final int ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 6007;
    public static final int ERROR_CODE_UNKNOWN_EXCEPTION = 6008;
    public static final int ERROR_CODE_INVALID_URI = 6009;
    public static final int ERROR_CODE_ASYNC_EXCEPTION = 6010;
    public static final int ERROR_CODE_ASYNC_DISCONNECTED = 6011;
    public static final int ERROR_CODE_DOWNLOAD_FILE = 6500;
    public static final int ERROR_CODE_WRITING_FILE = 6501;
    public static final int ERROR_CODE_INVALID_FILE_URI = 6502;
    public static final int ERROR_CODE_INVALID_USER_GROUP_HASH = 6503;
    public static final int ERROR_CODE_INVALID_THREAD_ID = 6504;

    public static final int ERROR_CODE_NUMBER_MESSAGE_ID = 6011;
    public static final int ERROR_CODE_CANT_GET_USER_INFO = 6100;
    public static final int ERROR_CODE_CONNECTION_NOT_ESTABLISHED = 6101;
    public static final int ERROR_CODE_NETWORK_ERROR = 6200;
    public static final int ERROR_CODE_UPLOAD_FILE = 6300;
    public static final int ERROR_CODE_NOT_IMAGE = 6301;

    public static final long ERROR_CODE_LOW_FREE_SPACE = 6400;

    public static final int ERROR_CODE_CALL_NESHAN_API = 6600;

    public static final int ERROR_CODE_INVALID_DATA = 6700;


    public static final int ERROR_CODE_NOTIFICATION_ERROR = 6701;

    public static final int ERROR_CODE_INVALID_CONTACT_ID = 6800;

    public static final int ERROR_CODE_INVALID_BOT_NAME = 6900;

    public static final int ERROR_CODE_INVALID_BOT_COMMAND = 6901;

    public static final int ERROR_CODE_METHOD_NOT_IMPLEMENTED = 7000;

    public static final int ERROR_CODE_CALL_INITIAL_ERROR = 7100;
    public static final int ERROR_CODE_MICROPHONE_NOT_AVAILABLE = 3000;
    public static final int ERROR_CODE_CAMERA_NOT_AVAILABLE = 3001;

    public static final int ERROR_CODE_INVALID_VIEW = 7101;



    /*
    Bots
     */
    public static final String ERROR_INVALID_BOT_NAME = "Bot name must end with BOT";

    public static final String ERROR_EMPTY_BOT_COMMANDS = "Command List cannot be empty";
    public static final String ERROR_BLANK_BOT_COMMAND = "command cannot be blank";
    public static final String ERROR_INVALID_BOT_COMMAND_FORMAT_1 = "command must start with slash symbol!";
    public static final String ERROR_INVALID_BOT_COMMAND_FORMAT_2 = "command cannot contain '@'symbol!";

    /*
    Files
     */

    public static final String ERROR_WRITING_FILE = "Error in writing file to disk";
    public static final String ERROR_INVALID_TOKEN = "Invalid Token!";
    public static final String ERROR_INVALID_URI = "Invalid Uri!";
    public static final String ERROR_INVALID_DATA = "Invalid Data!";
    public static final String ERROR_INVALID_FILE_URI = "Invalid File Uri!";
    public static final String ERROR_UNKNOWN_EXCEPTION = "Unknown Exception";
    public static final String ERROR_CHECK_URL = "Url must end in /";
    public static final String ERROR_NOT_IMAGE = "Not an image!";
    public static final String ERROR_UPLOAD_FILE = "Error in uploading File!";
    public static final String ERROR_DOWNLOAD_FILE = "Error in downloading File!";



    public static final String ERROR_CURRENT_DEVICE = "There Is No Current Device!";
    public static final String ERROR_READ_CONTACT_PERMISSION = "READ_CONTACTS Permission Needed!";
    public static final String ERROR_READ_EXTERNAL_STORAGE_PERMISSION = "READ_EXTERNAL_STORAGE Permission Needed!";
    public static final String ERROR_CHAT_READY = "Chat is not ready";
    public static final String ERROR_USER_NOT_FOUND = "User not found!";
    public static final String ERROR_NETWORK_ERROR = "Network Error";
    public static final String ERROR_CANT_GET_USER_INFO = "Can't get UserInfo!";
    public static final String ERROR_CONNECTION_NOT_ESTABLISHED = "Getting User Info Retry Count exceeded 5 times; Connection Can Not Establish!";

    public static final String METHOD_REPLY_MSG = "replyMessage";
    public static final String METHOD_LOCATION_MSG = "locationMessage";
    public static final String ERROR_NUMBER_MESSAGEID = "Number of messageIds exceeded! Just add one message Id";

    public static final String ERROR_LOW_FREE_SPACE = "More free space is needed for using cache!";
    public static final String ERROR_CALL_NESHAN_API = "Call neshan api failed";
    public static final String ERROR_INVALID_USER_GROUP_HASH = "Invalid userGroupHash value";
    public static final String ERROR_INVALID_THREAD_ID = "Invalid thread id";
    public static final String ERROR_ASYNC_EXCEPTION = "Async Error";
    public static final String ERROR_METHOD_NOT_IMPLEMENTED = "This feature is not available in current version!";

    public static final String ERROR_INVALID_CAMERA_PREVIEW = "Camera Preview is not valid";
    public static final String ERROR_CAMERA_NOT_AVAILABLE = "Camera is not available!";
    public static final String ERROR_MICROPHONE_NOT_AVAILABLE = "Camera is not available!";



    /*
    Call
     */
    public static final String MUTE_USER_LIST_IS_EMPTY = "user list for mute/unmute is empty";


}
