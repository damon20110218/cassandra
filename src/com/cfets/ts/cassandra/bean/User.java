package com.cfets.ts.cassandra.bean;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cfets.ts.cassandra.CassandraFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;


@Table(keyspace="damon_space",name="users",caseSensitiveTable = false)
public class User {
	@PartitionKey(0)
    @Column(name = "user_id")
    private int userId;

    private String name;
    private String email;
    @Column // not strictly required, but we want to check that the annotation works without a name
    private int year;
    @Column
    private String gender;

    public User() {
    }

    public User(int userId,String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        User that = (User) other;
        return Objects.equal(userId, that.userId)
                && Objects.equal(name, that.name)
                && Objects.equal(email, that.email)
                && Objects.equal(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, name, email, year,gender);
    }
    
    @Override
    public String toString(){
    	return this.userId+this.name+this.email;
    }
    public static void main(String[] args){
    	List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
    	 Mapper<User> m = new MappingManager(session).mapper(User.class);

         User u1 = new User(1,"Paul", "paul@yahoo.com");
         u1.setYear(2014);
         m.save(u1);
    }

}
