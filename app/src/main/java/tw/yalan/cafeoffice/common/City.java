package tw.yalan.cafeoffice.common;

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

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Alan Ding on 2016/12/31.
 */
public enum City implements Serializable {
    KEELUNG(0, 25.1316055, 121.7361893, "基隆"), TAIPEI(1, 25.0437847, 121.526903, "台北"),
    TAOYUAN(2, 24.9892108, 121.3113563, "桃園"), HSINCHU(3, 24.8015844, 120.9694678, "新竹"), TAICHUNG(4, 24.1375006, 120.6844106, "台中"), TAINAN(5, 22.9971206, 120.210412, "台南"), KAOHSIUNG(6, 22.6384266, 120.3038419, "高雄"), PINGTUNG(7, 22.6688619, 120.4837746, "屏東"),
    YILAN(8, 24.8220017, 121.7540856, "宜蘭"), TAITUNG(9, 22.7937159, 121.1209723, "台東"), MIAOLI(10, 24.587888, 120.823844, "苗栗"), PENGHU(11, 23.5723966, 119.5764035, "澎湖"),
    HUALIEN(12, 23.993486, 121.618424, "花蓮"), NANTOU(13, 23.9087529, 120.684668, "南投"), CHANGHUA(14, 23.9498345, 120.4370479, "彰化"), YUNLIN(15, 23.7116631, 120.5408036, "雲林"),
    CHIAYI(16, 23.4791236, 120.4389442, "嘉義"), JP(17, 35.6693077, 139.6713414, "日本"), US(18, 40.6976637, -74.1197619, "美國"), GB(19, 51.5287718, -0.2416787, "英國"),
    UNKNOW(-1, 0, 0, "");

    int cityIndex;
    double lat;
    double lng;
    String cityName;

    City(int cityIndex, double lat, double lng, String cityName) {
        this.cityIndex = cityIndex;
        this.lng = lng;
        this.lat = lat;
        this.cityName = cityName;
    }

    public int getCityIndex() {
        return cityIndex;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getCityName() {
        return cityName;
    }

    public static City valueOfByIndex(int cityIndex) {
        City[] values = values();
        for (int i = 0; i < values.length; i++) {
            City value = values[i];
            if (cityIndex == value.getCityIndex()) {
                return value;
            }
        }
        return UNKNOW;
    }

    public static City valueOfByEnglishName(String name) {
        if (name == null) {
            return UNKNOW;
        }
        City[] values = values();
        for (int i = 0; i < values.length; i++) {
            City value = values[i];
            if (value.name().equals(name.toUpperCase())) {
                return value;
            }
        }
        return UNKNOW;
    }

    public static City valueOfByShortName(String currentCityShortName) {
        if (currentCityShortName == null) {
            return UNKNOW;
        } else {
            City[] values = values();
            for (int i = 0; i < values.length; i++) {
                City value = values[i];
                if (currentCityShortName.contains(value.getCityName())) {
                    return value;
                } else if (currentCityShortName.contains("新北")) {
                    return City.TAIPEI;
                }
            }
            return UNKNOW;
        }
    }
}
