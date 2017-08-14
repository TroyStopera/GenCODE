package com.troystopera.gencode.var;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.troystopera.gencode.var.VarType.INT_PRIMITIVE;

/**
 * Class used to virtually represent an int.
 */
public class IntVar extends PrimitiveVar<Integer> {

    private static final IntCache cache = new IntCache(100);

    private IntVar(int val) {
        super(INT_PRIMITIVE, val);
    }

    public static IntVar random(int max) {
        return new IntVar(random.nextInt(max + 1));
    }

    public static IntVar of(int i) {
        if (cache.containsKey(i))
            return cache.get(i);
        else {
            IntVar val = new IntVar(i);
            cache.put(i, val);
            return val;
        }
    }

    private static class IntCache extends LinkedHashMap<Integer, IntVar> {

        private final int capacity;

        private IntCache(int capacity) {
            super(capacity * 4 / 3, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, IntVar> entry) {
            return size() > capacity;
        }

    }

}
