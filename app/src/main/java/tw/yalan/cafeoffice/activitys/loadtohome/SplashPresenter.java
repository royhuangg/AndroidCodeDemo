package tw.yalan.cafeoffice.activitys.loadtohome;

/**
 * Copyright (C) 2016 Alan Ding
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;

import com.grasea.grandroid.api.Callback;
import com.grasea.grandroid.api.RemoteProxy;
import com.grasea.grandroid.api.RequestFail;

import retrofit2.Call;
import retrofit2.Response;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.api.SelfAPI;
import tw.yalan.cafeoffice.api.model.ContainsUserArrayResult;
import tw.yalan.cafeoffice.api.model.ContainsUserResult;
import tw.yalan.cafeoffice.api.model.GetTagListResponse;
import tw.yalan.cafeoffice.api.model.LoginResponse;
import tw.yalan.cafeoffice.common.BasePresenter;

/**
 * Created by Alan Ding on 2017/2/26.
 */
public class SplashPresenter extends BasePresenter<SplashActivity> {
    SelfAPI api;

    @Override
    public void onActivityCreate(Bundle extras) {
        api = RemoteProxy.reflect(SelfAPI.class, this);
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();

    }

    @Callback("getProfile")
    public void onGetProfileResponse(ContainsUserResult result) {
        if (result == null) {
            getContract().onRequestFailed("getProfile");
            return;
        }
        if (result.isSuccess()) {
            ModelManager.Companion.get().updateUser(result.getUser());
        }
        getContract().goToMap();


    }

    @RequestFail
    public void onError(String name, Throwable e) {
        Config.loge(e);
        getContract().onRequestFailed("getProfile");
    }

    public void startLoading(boolean signedIn) {
        Call<GetTagListResponse> responseCall = api.getTagList(ModelManager.Companion.get().getUserModel().getToken());
        responseCall.enqueue(new retrofit2.Callback<GetTagListResponse>() {
            @Override
            public void onResponse(Call<GetTagListResponse> call, Response<GetTagListResponse> response) {
                if (response.body() != null) {
                    if (response.body().isSuccess()) {
                        ModelManager.Companion.get().saveTagList(response.body().getTags());
                    }


                }
                if (signedIn) {
                    api.getProfile(ModelManager.get().getUserModel().getToken(), ModelManager.get().getUserModel().getUserId());
                } else {
                    getContract().goToSignIn();
                }
            }

            @Override
            public void onFailure(Call<GetTagListResponse> call, Throwable t) {
                Config.loge(t);
                if (signedIn) {
                    api.getProfile(ModelManager.get().getUserModel().getToken(), ModelManager.get().getUserModel().getUserId());
                } else {
                    getContract().goToSignIn();
                }
            }
        });
    }
}
