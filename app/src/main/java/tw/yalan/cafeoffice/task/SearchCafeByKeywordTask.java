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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tw.yalan.cafeoffice.model.Cafe;

/**
 * Created by Alan Ding on 2017/2/9.
 */
public abstract class SearchCafeByKeywordTask extends AsyncTask<ArrayList<Cafe>, Void, ArrayList<Cafe>> {
    String newText;

    public SearchCafeByKeywordTask(String newText) {
        this.newText = newText;
    }

    @Override
    protected abstract void onPostExecute(ArrayList<Cafe> cafes);

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(ArrayList<Cafe> cafes) {
        super.onCancelled(cafes);
    }

    @Override
    protected ArrayList<Cafe> doInBackground(ArrayList<Cafe>... params) {
        ArrayList<Cafe> cafes = params[0];
        if (cafes != null) {
            if (newText.isEmpty()) {
                cafes.clear();
                return cafes;
            }
            ArrayList<Cafe> cafeList = ModelManager.Companion.get().getUserModel().getCafeList();
            if (cafeList == null) {
                return cafes;
            }
            for (Cafe cafe : cafeList) {
                if (cafe.getName().toLowerCase().contains(newText.toLowerCase())) {
                    if (cafes.contains(cafe)) {
                        continue;
                    } else {
                        cafes.add(cafe);
                    }
                } else {
                    if (cafes.contains(cafe)) {
                        cafes.remove(cafe);
                    }
                }
            }
        }
        Collections.sort(cafes, new Comparator<Cafe>() {
            public Double parseDistance(Cafe cafe) {
                Double result;
                try {
                    String[] distances = cafe.getDisplayDistance().split(" ");
                    if (distances[1].equals("KM")) {
                        result = Double.valueOf(distances[0]) * 1000;
                    } else {
                        result = Double.valueOf(distances[0]);
                    }
                } catch (Exception e) {
                    return 0d;
                }
                return result;
            }

            @Override
            public int compare(Cafe o1, Cafe o2) {
                return parseDistance(o1).compareTo(parseDistance(o2));
            }
        });
        return cafes;
    }
}
