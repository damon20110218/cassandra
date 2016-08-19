package com.cfets.ts.cassandra.bean;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface UserAccessor {
	@Query("select * from users where name >= ? limit ? ALLOW FILTERING")
	Result<User> getUserByPage(String name,int fetchSize);
}
