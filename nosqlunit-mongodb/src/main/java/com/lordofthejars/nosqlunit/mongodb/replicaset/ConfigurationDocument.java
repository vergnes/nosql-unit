package com.lordofthejars.nosqlunit.mongodb.replicaset;

import com.mongodb.DBObject;

public class ConfigurationDocument {

	private DBObject configuration;
	
	public ConfigurationDocument(DBObject configuration) {
		this.configuration = configuration;
	}
	
	public DBObject getConfiguration() {
		return this.configuration;
	}
	
}
