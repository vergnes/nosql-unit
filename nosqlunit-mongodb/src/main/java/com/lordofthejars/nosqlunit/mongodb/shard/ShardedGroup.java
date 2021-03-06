package com.lordofthejars.nosqlunit.mongodb.shard;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;

import com.lordofthejars.nosqlunit.core.AbstractLifecycleManager;
import com.lordofthejars.nosqlunit.mongodb.ManagedMongoDbLifecycleManager;

public class ShardedGroup {

	private List<ManagedMongoDbLifecycleManager> shards = new ArrayList<ManagedMongoDbLifecycleManager>();
	private List<ManagedMongoDbLifecycleManager> configs = new ArrayList<ManagedMongoDbLifecycleManager>();
	private List<ManagedMongosLifecycleManager> mongos = new ArrayList<ManagedMongosLifecycleManager>();

	private String username;
	private String password;
	
	public void addShard(ManagedMongoDbLifecycleManager managedMongoDbLifecycleManager) {
		this.shards.add(managedMongoDbLifecycleManager);
	}
	
	public void addConfig(ManagedMongoDbLifecycleManager managedMongoDbLifecycleManager) {
		this.configs.add(managedMongoDbLifecycleManager);
	}
	
	public void addMongos(ManagedMongosLifecycleManager managedMongosLifecycleManager) {
		this.mongos.add(managedMongosLifecycleManager);
	}
	
	public ManagedMongosLifecycleManager getFirstMongosServer() {
		
		if(this.mongos.size() > 0) {
			return this.mongos.get(0);
		} else {
			throw new IllegalArgumentException("At least one Mongos server is required for sharding");
		}
		
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isAuthenticationSet() {
		return this.username != null && this.password != null;
	}
	
	public AbstractLifecycleManager getStoppingServer(int port) {
		return getServerByPortAndState(port, true);
	}
	
	public AbstractLifecycleManager getStartingServer(int port) {
		return getServerByPortAndState(port, false);
	}
	
	private AbstractLifecycleManager getServerByPortAndState(int port, boolean state) {
		AbstractLifecycleManager abstractLifecycleManager = selectFirst(
				this.shards,
				having(on(ManagedMongoDbLifecycleManager.class).getPort(),
						is(port)).and(having(on(ManagedMongoDbLifecycleManager.class).isReady(), is(state))));
		
		if(abstractLifecycleManager == null) {
			abstractLifecycleManager = selectFirst(
					this.configs,
					having(on(ManagedMongoDbLifecycleManager.class).getPort(),
							is(port)).and(having(on(ManagedMongoDbLifecycleManager.class).isReady(), is(state))));
			
			if(abstractLifecycleManager == null) {
				abstractLifecycleManager = selectFirst(
						this.mongos,
						having(on(ManagedMongoDbLifecycleManager.class).getPort(),
								is(port)).and(having(on(ManagedMongoDbLifecycleManager.class).isReady(), is(state))));
			}
			
		}
		
		return abstractLifecycleManager;
	}
	
	public List<ManagedMongoDbLifecycleManager> getShards() {
		return shards;
	}
	
	public List<ManagedMongoDbLifecycleManager> getConfigs() {
		return configs;
	}
	
	public List<ManagedMongosLifecycleManager> getMongos() {
		return mongos;
	}
	
}
