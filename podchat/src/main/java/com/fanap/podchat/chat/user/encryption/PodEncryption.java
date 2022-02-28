package com.fanap.podchat.chat.user.encryption;

import com.fanap.podchat.model.OutPutGetKey;

public class PodEncryption {

    public interface IPodPrivateKeyProvider {

        void onPrivateKeyPrepared(OutPutGetKey response);

        void onFaild(String error);

    }

    public interface IPodSecretKeyProvider {

        void onSecretKeyIdPrepared(String secretKeyId);

        void onFaild(String error);

    }

}
