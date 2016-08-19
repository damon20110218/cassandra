package com.cfets.ts.cassandra.annotation;

public class CassandraColumnModel {
	
	private String columnName;
	private String fieldName;
	private boolean isPrimaryKey;
	private String type;
	private CassandraColumn cc;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CassandraColumn getCc() {
		return cc;
	}
	public void setCc(CassandraColumn cc) {
		this.cc = cc;
	}
	
	
}
