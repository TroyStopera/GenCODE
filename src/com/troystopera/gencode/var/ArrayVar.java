package com.troystopera.gencode.var;

import com.troystopera.gencode.GenerationException;

/**
 * Created by troy on 7/29/17.
 */
public class ArrayVar<T extends Var> extends Var {

    private final T[] array;

    private ArrayVar(VarType type, T... array) {
        super(type);
        this.array = array;
    }

    public final T[] getArray() {
        return array;
    }

    public final int length() {
        return array.length;
    }

    @Override
    public String toString() {
        return array.toString();
    }

    public static ArrayVar empty(VarType type, int size) {
        switch (type) {
            case INT_PRIMITIVE:
            case INT_ARRAY:
                return new ArrayVar<>(type, new IntVar[size]);
            case BOOLEAN_PRIMITIVE:
            case BOOLEAN_ARRAY:
                return new ArrayVar<>(type, new BooleanVar[size]);
            case STRING_PRIMITIVE:
            case STRING_ARRAY:
                return new ArrayVar<>(type, new StringVar[size]);
            default:
                throw new GenerationException(new IllegalArgumentException("Unknown type " + type));
        }
    }

    public static ArrayVar<IntVar> of(IntVar... array) {
        return new ArrayVar<>(VarType.INT_PRIMITIVE, array);
    }

    public static ArrayVar<BooleanVar> of(BooleanVar... array) {
        return new ArrayVar<>(VarType.BOOLEAN_PRIMITIVE, array);
    }

    public static ArrayVar<StringVar> of(StringVar... array) {
        return new ArrayVar<>(VarType.STRING_PRIMITIVE, array);
    }

    public static ArrayVar<IntVar> of(int... array) {
        IntVar[] varArray = new IntVar[array.length];
        for (int i = 0; i < array.length; i++)
            varArray[i] = IntVar.of(array[i]);
        return new ArrayVar<>(VarType.INT_PRIMITIVE, varArray);
    }

    public static ArrayVar<BooleanVar> of(boolean... array) {
        BooleanVar[] varArray = new BooleanVar[array.length];
        for (int i = 0; i < array.length; i++)
            varArray[i] = BooleanVar.of(array[i]);
        return new ArrayVar<>(VarType.BOOLEAN_PRIMITIVE, varArray);
    }

    public static ArrayVar<StringVar> of(String... array) {
        StringVar[] varArray = new StringVar[array.length];
        for (int i = 0; i < array.length; i++)
            varArray[i] = StringVar.of(array[i]);
        return new ArrayVar<>(VarType.STRING_PRIMITIVE, varArray);
    }

}
