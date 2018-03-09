package tw.yalan.cafeoffice.filter;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.model.Cafe;

/**
 * Created by Alan Ding on 2017/2/2.
 */
public class CafeFilter {
    HashMap<FilterRule.Type, FilterRule> mFilterRules = new HashMap<>();

    @Nullable
    Calendar startCalendar;
    @Nullable
    Calendar endCalendar;

    public CafeFilter() {
    }

    public CafeFilter setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
        return this;
    }

    public CafeFilter setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
        return this;
    }

    public boolean contains(FilterRule.Type type) {
        return mFilterRules.containsKey(type);
    }

    public FilterRule getFilterRule(FilterRule.Type type) {
        return mFilterRules.get(type);
    }

    public void addFilterRule(FilterRule... filterRules) {
        for (int i = 0; i < filterRules.length; i++) {
            FilterRule filterRule = filterRules[i];
            if (mFilterRules.containsKey(filterRule.getType())) {
                continue;
            }
            mFilterRules.put(filterRule.getType(), filterRule);
        }
    }

    public void removeFilterRule(FilterRule.Type... filterRules) {
        for (int i = 0; i < filterRules.length; i++) {
            FilterRule.Type filterRuleType = filterRules[i];
            if (!mFilterRules.containsKey(filterRuleType)) {
                continue;
            }
            mFilterRules.remove(filterRuleType);
        }
    }

    public void removeFilterRule(FilterRule... filterRules) {
        for (int i = 0; i < filterRules.length; i++) {
            FilterRule filterRule = filterRules[i];
            if (!mFilterRules.containsKey(filterRule.getType())) {
                continue;
            }
            mFilterRules.remove(filterRule.getType());
        }
    }

    public synchronized boolean doFilter(Cafe cafe) {
        return progressObject(mFilterRules, cafe);
    }

    public ArrayList<Cafe> doFilter(List<Cafe> list) {
        ArrayList<Cafe> result = new ArrayList<>();
        for (Cafe cafe : list) {
            boolean success = progressObject(mFilterRules, cafe);
            if (success) {
                result.add(cafe);
            }
        }
        return result;
    }

    public synchronized boolean progressObject(HashMap<FilterRule.Type, FilterRule> mFilterRules, Cafe obj) {
        if (mFilterRules.isEmpty()) {
            return true;
        }
        boolean result = true;
        for (FilterRule.Type type : mFilterRules.keySet()) {
            FilterRule filterRule = mFilterRules.get(type);
            Double value = filterRule.getValue();
            switch (type) {
                case WIFI:
                    if (mFilterRules.containsKey(FilterRule.Type.SUPER_WIFI)) {
                        break;
                    }
                    result = result && obj.getWifi() >= value;
                    break;
                case SUPER_WIFI:
                    result = result && obj.getWifi() >= value;
                    break;
                case QUIET:
                    result = result && obj.getQuiet() >= value;
                    break;
                case PRICE:
                    result = result && obj.getCheap() >= value;
                    break;
                case SEAT:
                    result = result && obj.getSeat() >= value;
                    break;
                case TASTY:
                    result = result && obj.getTasty() >= value;
                    break;
                case TASTY_FOOD:
                    result = result && obj.getFood() >= value;
                    break;
                case MUSIC:
                    result = result && obj.getMusic() >= value;
                    break;
                case SOCKET:
                    if (mFilterRules.containsKey(FilterRule.Type.SUPER_SOCKET)) {
                        break;
                    }
                    if (value == 0) {
                        break;
                    } else {
                        result = result && obj.getSocket().intValue() == value;
                    }
                    break;
                case SUPER_SOCKET:
                    if (value == 0) {
                        break;
                    } else {
                        result = result && obj.getSocket().intValue() == value;
                    }
                    break;
                case LIMITED_TIME:
                    if (mFilterRules.containsKey(FilterRule.Type.SUPER_LIMITED_TIME)) {
                        break;
                    }
                    if (value == 0) {
                        break;
                    } else {
                        result = result && obj.getLimited_time().intValue() == value;
                    }
                    break;
                case SUPER_LIMITED_TIME:
                    if (value == 0) {
                        break;
                    } else {
                        result = result && obj.getLimited_time().intValue() == value;
                    }
                    break;
                case HIGH_RATING:
                    result = result && obj.getRateAverage() != null && obj.getRateAverage() == 5.0;
                    break;
                case OPENING:
                    result = result && obj.isNowOpen();
                    break;
                case STANDING_DESK:
                    if (value == 0) {
                        break;
                    } else {
                        result = result && obj.getStanding_desk().intValue() == 1;
                    }
                    break;

                case CUSTOM_TIME_OPENING:
                    if (startCalendar != null && endCalendar != null) {
                        result = result && obj.isOpening(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
                    } else {
                        result = false;
                    }
                    break;
            }
        }
        Config.loge("filter result: " + result);
//        for (FilterRule.Type type : mFilterRules.keySet()) {
//
//        }
        return result;
    }

    public void clear() {
        mFilterRules.clear();
    }

    public boolean hasFilterRules() {
        return !mFilterRules.isEmpty();
    }
}
