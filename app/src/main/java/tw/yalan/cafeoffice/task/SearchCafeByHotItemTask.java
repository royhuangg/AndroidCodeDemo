package tw.yalan.cafeoffice.task;

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

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.List;

import tw.yalan.cafeoffice.model.Cafe;
import tw.yalan.cafeoffice.model.firebase.CafeSearchHistory;

/**
 * Created by Alan Ding on 2017/2/9.
 */
public abstract class SearchCafeByHotItemTask extends AsyncTask<List<Cafe>, Void, Cafe> {
    CafeSearchHistory cafeSearchHistory;

    public SearchCafeByHotItemTask(CafeSearchHistory cafeSearchHistory) {
        this.cafeSearchHistory = cafeSearchHistory;
    }

    @Override
    protected Cafe doInBackground(List<Cafe>... params) {
        List<Cafe> cafes = params[0];
        if (cafes != null) {
            if (cafeSearchHistory == null) {
                return null;
            }
            if (TextUtils.isEmpty(cafeSearchHistory.getName())) {
                return null;
            }
            for (Cafe cafe : cafes) {
                if (cafe.getId().equals(cafeSearchHistory.getId())) {
                    return cafe;
                }
            }
        }
        return null;
    }

    @Override
    protected abstract void onPostExecute(Cafe cafes);

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Cafe cafe) {
        super.onCancelled(cafe);
    }

}
