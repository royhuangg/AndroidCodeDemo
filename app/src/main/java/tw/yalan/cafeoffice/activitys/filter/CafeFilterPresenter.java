package tw.yalan.cafeoffice.activitys.filter;

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

import tw.yalan.cafeoffice.common.BasePresenter;

/**
 * Created by Alan Ding on 2017/2/3.
 */
public class CafeFilterPresenter extends BasePresenter<CafeFilterActivity> {
    @Override
    public void onActivityCreate(Bundle extras) {
        getContract().initViews();
        int[] values = ModelManager.Companion.get().loadDefaultValues();
        int sortBy = ModelManager.Companion.get().getFilterSettingsModel().getFilterSort();
        getContract().setupDefaultValue(values, sortBy);
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

    public void onClickResetToDefault() {
        ModelManager.Companion.get().resetFilterValuesToDefault();
        ModelManager.Companion.get().getFilterSettingsModel().putFilterSort(0);
        getContract().setupDefaultValue(ModelManager.Companion.get().loadDefaultValues(), 0);
    }

    public void onClickSearch(int[] defaultValue, int sortBy) {
        ModelManager.Companion.get().saveDefaultValues(defaultValue);
        ModelManager.Companion.get().getFilterSettingsModel().putFilterSort(sortBy);
    }
}
