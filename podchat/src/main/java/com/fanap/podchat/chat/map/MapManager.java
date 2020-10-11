package com.fanap.podchat.chat.map;

import com.fanap.podchat.mainmodel.MapNeshan;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.ResultMap;
import com.fanap.podchat.networking.api.MapApi;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperMap;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.PodChatException;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MapManager {


    public static Observable<OutPutMapNeshan> searchMap(String apiKey,String api,
                                                        String searchTerm,
                                                        double latitude,
                                                        double longitude){


        return Observable.create(subscriber -> {


              RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap(api);

              MapApi mapApi = retrofitHelperMap.getService(MapApi.class);

              Observable<Response<MapNeshan>> observable = mapApi
                      .mapSearch(apiKey,
                              searchTerm,
                              latitude,
                              longitude);


              observable.subscribeOn(Schedulers.io()).
                      observeOn(AndroidSchedulers.mainThread())
                      .subscribe(mapNeshanResponse -> {

                          OutPutMapNeshan outPutMapNeshan = new OutPutMapNeshan();

                          if (mapNeshanResponse.isSuccessful()) {
                              MapNeshan mapNeshan = mapNeshanResponse.body();

                              if (mapNeshan == null) {
                                  subscriber.onError(new PodChatException(mapNeshanResponse.message(), mapNeshanResponse.code()));
                                  return;
                              }
                              outPutMapNeshan = new OutPutMapNeshan();
                              outPutMapNeshan.setCount(mapNeshan.getCount());
                              ResultMap resultMap = new ResultMap();
                              resultMap.setMaps(mapNeshan.getItems());
                              outPutMapNeshan.setResult(resultMap);

                              subscriber.onNext(outPutMapNeshan);
                          } else {
                              subscriber.onError(new PodChatException(mapNeshanResponse.message(), mapNeshanResponse.code()));
                          }
                      }, subscriber::onError);
          });

    }




}
