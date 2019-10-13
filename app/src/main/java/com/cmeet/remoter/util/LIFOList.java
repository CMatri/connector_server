package com.cmeet.remoter.util;

import java.util.ArrayList;

public class LIFOList<T> extends ArrayList<T> {
    public void push(T obj) {
        this.add(obj);
    }
    public T pop() {
        T el = get(size() - 1);
        remove(size() - 1);
        return el;
    }
}
