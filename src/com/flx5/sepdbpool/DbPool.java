package com.flx5.sepdbpool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class DbPool implements AutoCloseable {
	private final int minConnections;
	private final int maxConnections;
	private final List<DbConnection> connections;
	private final Timer timer;
	private final Supplier<Connection> connectionSupply;
	private static final long CLEANUP_PERIOD = 60*1000;

	public DbPool(final int minConnections, final int maxConnections, final Supplier<Connection> connectionSupply) {
		if(maxConnections < minConnections) {
			throw new IllegalArgumentException("maxConnections can't be smaller than minConnections!");
		}
		
		if(connectionSupply == null) {
			throw new IllegalArgumentException("connectionSupply is null");
		}
		
		this.minConnections = minConnections;
		this.maxConnections = maxConnections;
		this.connections = new ArrayList<>(this.minConnections);
		this.connectionSupply = connectionSupply;

		for(int i = 0; i < this.minConnections; ++i) {
			tryBuildConnection();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::close));
		
		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {
			@Override
			public void run() {
				cleanup();
			}
		}, CLEANUP_PERIOD, CLEANUP_PERIOD);
	}

	private synchronized void cleanup() {
		Iterator<DbConnection> iterator = this.connections
		.stream()
		.filter(x -> x.isIdle())
		.limit(this.connections.size() - minConnections)
		.iterator();
		
		while(iterator.hasNext()) {
			DbConnection conn = iterator.next();
			connections.remove(conn);
			conn.destroy();
		}
	}
	
	private synchronized DbConnection tryBuildConnection() {
		if(connections.size() < maxConnections) {
			DbConnection conn = new DbConnection(this, connectionSupply.get());
			this.connections.add(conn);
			return conn;
		}

		return null;
	}
	
	private synchronized DbConnection getNextAvail() {
		return this.connections
				.stream()
				.filter(x -> x.tryObtain())
				.findFirst()
				.orElseGet(this::tryBuildConnection);
	}
	
	public synchronized DbConnection getConnection() {
		DbConnection conn;
		
		while((conn = getNextAvail()) == null) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		return conn;
	}
	
	synchronized void connectionReleased() {
		// notifyAll must be called in synchronized method of class!
		this.notifyAll();
	}

	@Override
	public synchronized void close() {
		this.timer.cancel();
		
		for(DbConnection conn : this.connections) {
			conn.destroy();
		}
		
		this.connections.clear();
	}

}
