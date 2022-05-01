package com.example.chat.application.chatexample;

import com.fanap.podchat.example.R;

public class ServerConfig {


    public static String appId = BaseApplication.getInstance().getString(R.string.integration_appId);
    public static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    public static final String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);


    public static final String serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);
    public static final String integrationName = BaseApplication.getInstance().getString(R.string.integration_serverName);
    public static final String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
    public static final String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);

    public static int TEST_THREAD_ID = 7090;


    public static String sandBoxServerName = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    public static String sandBoxSSOHost = BaseApplication.getInstance().getString(R.string.sandbox_ssoHost);
    public static String sandBoxSocketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    public static String sandBoxPlatformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    public static String sandBoxFileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);


    public static String mainServerName = BaseApplication.getInstance().getString(R.string.main_server_name);
    public static String mainSSOHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    public static String mainSocketAddress = BaseApplication.getInstance().getString(R.string.socketAddress);
    public static String mainPlatformHost = BaseApplication.getInstance().getString(R.string.platformHost);
    public static String mainFileServer = BaseApplication.getInstance().getString(R.string.fileServer);

    public static String podspaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_main);




    public static final Enum<ServerType> serverType = ServerType.SANDBOX;




}
