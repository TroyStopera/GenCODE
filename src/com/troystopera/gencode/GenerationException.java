package com.troystopera.gencode;

public class GenerationException extends RuntimeException {

    public GenerationException(Throwable throwable) {
        super("Exception thrown while generating code", throwable);
    }

}
