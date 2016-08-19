package com.cfets.ts.cassandra.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CassandraModel {

	private String clzName;
	private Cassandra cassandra;
	private List<CassandraColumnModel> columns;
	private List<HashMap<String,Object>> data;
	
	public CassandraModel(){
		this.columns = new ArrayList<CassandraColumnModel>();
		this.clzName = "";
		this.cassandra = null;
	}

	public String getClzName() {
		return clzName;
	}

	public void setClzName(String clzName) {
		this.clzName = clzName;
	}

	public String getName() {
		return this.cassandra.tableName();
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	public List<CassandraColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<CassandraColumnModel> columns) {
		this.columns = columns;
	}

	public List<HashMap<String, Object>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, Object>> data) {
		this.data = data;
	}
	
}
