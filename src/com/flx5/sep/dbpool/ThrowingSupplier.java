package com.flx5.sep.dbpool;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
	T get() throws E;
}
