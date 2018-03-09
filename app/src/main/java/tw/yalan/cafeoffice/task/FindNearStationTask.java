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
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.utils.MapUtils;

/**
 * Created by Alan Ding on 2017/1/31.
 */
public abstract class FindNearStationTask extends AsyncTask<Void, Void, MRTData.MRTStation> {
    private LatLng currentLatLng;
    private String name;

    /**
     * 擇一填入
     *
     * @param currentLatLng
     * @param name
     */
    public FindNearStationTask(LatLng currentLatLng, @Nullable String name) {
        this.currentLatLng = currentLatLng;
        this.name = name;
    }

    @Override
    protected void onCancelled() {
        currentLatLng = null;
        name = null;
        super.onCancelled();
    }

    @Override
    protected abstract void onPostExecute(MRTData.MRTStation mrtStation);

    @Override
    protected MRTData.MRTStation doInBackground(Void... voids) {
        ArrayList<MRTData.MRTStation> mrtStationList = MRTData.getMrtStationList();
        MRTData.MRTStation locationResult = null, nameResult = null;
        Integer neaiestDistance = -1;
        if (mrtStationList != null) {
            for (MRTData.MRTStation mrtStation : mrtStationList) {
                if (currentLatLng != null) {
                    String[] distance = MapUtils.getDistance(currentLatLng, new LatLng(Double.valueOf(mrtStation.getLatitude()), Double.valueOf(mrtStation.getLongitude())));
                    Integer distanceM = Integer.valueOf(distance[0]);
                    if (neaiestDistance == -1) {
                        locationResult = mrtStation;
                        neaiestDistance = distanceM;
                    } else {
                        neaiestDistance = Math.min(distanceM, neaiestDistance);
                        if (distanceM.intValue() == neaiestDistance.intValue()) {
                            locationResult = mrtStation;
                        }
                    }
                }
                if (nameResult == null && name != null && !name.isEmpty()) {
                    if (name.contains(mrtStation.getName())) {
                        nameResult = mrtStation;
                        Config.loge("API欄位比對後捷運名稱:" + mrtStation.getName());
                    }
                }
            }
        }
        return locationResult != null ? locationResult : nameResult;
    }
}
