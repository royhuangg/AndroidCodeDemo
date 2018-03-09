package tw.yalan.cafeoffice.filter;

import java.io.Serializable;

/**
 * Created by Alan Ding on 2017/3/25.
 */

public class FilterRule implements Serializable {
    public enum Type implements Serializable {
        WIFI(1), SUPER_WIFI(12), QUIET(2), PRICE(3), SEAT(4),
        TASTY(5), TASTY_FOOD(15), MUSIC(6), SOCKET(7), SUPER_SOCKET(13), LIMITED_TIME(8), SUPER_LIMITED_TIME(14),
        STANDING_DESK(9), OPENING(10), HIGH_RATING(11), CUSTOM_TIME_OPENING(16);
        private int id;

        Type(int i) {
            this.id = i;
        }

        public int getId() {
            return id;
        }
    }

    Type type;
    Double value;

    public FilterRule(Type type, Double value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public FilterRule setType(Type type) {
        this.type = type;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public FilterRule setValue(Double value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilterRule) {
            FilterRule obj1 = (FilterRule) obj;
            return type == obj1.getType();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return type.getId() * 40 * 4;
    }
}
