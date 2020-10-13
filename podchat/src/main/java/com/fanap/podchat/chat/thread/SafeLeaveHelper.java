package com.fanap.podchat.chat.thread;

import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.model.ChatResponse;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;


public class SafeLeaveHelper {

    private Observable<ChatResponse<ResultCurrentUserRoles>> userRolesObservable;




    public Observable<ChatResponse<ResultCurrentUserRoles>> getOrCreateUserRoles(){

        if(userRolesObservable == null)
            userRolesObservable = Observable.create(getUserRolesObserver());

        return userRolesObservable;
    }

    public Observable.OnSubscribe<ChatResponse<ResultCurrentUserRoles>> getUserRolesObserver() {
        return subscriber -> getUserRolesSubscriber();
    }

    public Subscriber<ChatResponse<ResultCurrentUserRoles>> getUserRolesSubscriber() {

        return new Subscriber<ChatResponse<ResultCurrentUserRoles>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ChatResponse<ResultCurrentUserRoles> resultCurrentUserRolesChatResponse) {

            }
        };

    }


    public void callUserRolesObserver(ChatResponse<ResultCurrentUserRoles> response) {
        userRolesObservable = Observable.create(emitter-> emitter.onNext(response));
    }
}
