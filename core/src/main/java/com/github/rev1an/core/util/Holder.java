package com.github.rev1an.core.util;

public final class Holder<T> {

    private T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static <T> Holder<T> empty() {
        return new Holder<>();
    }

}
