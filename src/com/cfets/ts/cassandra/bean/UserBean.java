package com.cfets.ts.cassandra.bean;

import com.cfets.ts.cassandra.annotation.Cassandra;
import com.cfets.ts.cassandra.annotation.CassandraColumn;

@Cassandra(tableName = "user")
public class UserBean {
	
	@CassandraColumn(name="userId",type="int",isPrimaryKey=true)
	private int userId;
	
	@CassandraColumn(name="userName",type="text")
	private String userName;
	
	@CassandraColumn(name="password",type="text")
	private String password;
	
	public UserBean(){
		
	}
	
	public UserBean(int userId,String userName,String pwd){
		this.userId = userId;
		this.userName = userName;
		this.password = pwd;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
}
