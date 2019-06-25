package com.peihou.willgood2.utils.http;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface HttpService {

    @GET
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> getRequest(@Url String url);

    @POST
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> postQuest(@Url String url, @Body RequestBody body);

    @Multipart
    @POST("device/updateSwitchPic")
    Call<ResponseBody> uploadFile(@PartMap Map<String, Object> map, @Part MultipartBody.Part file);

}
