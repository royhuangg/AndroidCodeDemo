package tw.yalan.cafeoffice.utils;

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


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan Ding on 2017/2/13.
 */
public class GsonUtils {

    public static <T> List<T> toList(String arrayStr, TypeToken typeToken) {
        if (TextUtils.isEmpty(arrayStr)) {
            return new ArrayList<T>();
        }
        Gson gson = new Gson();

        return gson.fromJson(arrayStr, typeToken.getType());
    }

    public static <T> String toJson(List<T> list) {

        Gson gson = new Gson();
        if (list == null) {
            return "[]";
        }
        return gson.toJson(list);
    }
}
