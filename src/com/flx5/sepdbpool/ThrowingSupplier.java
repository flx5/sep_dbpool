package com.flx5.sepdbpool;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
	T get() throws E;
}
