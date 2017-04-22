package com.flx5.sepdbpool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection implements AutoCloseable {
	private static final Duration TIMEOUT = Duration.ofMinutes(1);
	
	private static final Logger LOG = Logger.getLogger( DbConnection.class.getName() );
	
	private boolean isAvailable;
	private final DbPool pool;
	private final Connection connection;
	private LocalDateTime lastUsed;
	
	
	DbConnection(final DbPool pool, final Connection connection) {
		
		try {
			if(pool == null || connection == null || connection.isClosed()) {
				throw new IllegalArgumentException("pool or connection is null or closed.");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
		
		this.isAvailable = true;
		this.pool = pool;
		this.connection = connection;
	}
	
	synchronized boolean isIdle() {
		return isAvailable && Duration.between(lastUsed, LocalDateTime.now()).compareTo(TIMEOUT) > 0;
	}

	synchronized boolean tryObtain() {
		try {
			if(!this.isAvailable || this.connection.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		this.isAvailable = false;
		return true;
	}
	
	void destroy() {
		try {
			if(!this.connection.isClosed()) {
				this.connection.close();
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	@Override
	public synchronized void close() {
		this.isAvailable = true;
		this.pool.connectionReleased();
		this.lastUsed = LocalDateTime.now();
	}
}
