package com.troystopera.gencode.val;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class used to virtually represent an int.
 */
public class IntVal extends Val<Integer> {

    private static final IntCache cache = new IntCache(100);

    private IntVal(int val) {
        super(ValType.INT, val);
    }

    public static IntVal random(int max) {
        return new IntVal(random.nextInt(max + 1));
    }

    public static IntVal of(int i) {
        if (cache.containsKey(i))
            return cache.get(i);
        else {
            IntVal val = new IntVal(i);
            cache.put(i, val);
            return val;
        }
    }

    private static class IntCache extends LinkedHashMap<Integer, IntVal> {

        private final int capacity;

        private IntCache(int capacity) {
            super(capacity * 4 / 3, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, IntVal> entry) {
            return size() > capacity;
        }

    }

}
