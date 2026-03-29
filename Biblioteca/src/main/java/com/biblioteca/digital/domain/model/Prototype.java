package com.biblioteca.digital.domain.model;

public interface Prototype<T> {
    T copy();
}