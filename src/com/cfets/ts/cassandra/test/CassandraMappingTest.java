package com.cfets.ts.cassandra.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.cfets.ts.cassandra.CassandraFactory;
import com.cfets.ts.cassandra.bean.User;
import com.cfets.ts.cassandra.bean.UserAccessor;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

public class CassandraMappingTest {

	public static void main(String[] args) {
		CassandraMappingTest c = new CassandraMappingTest();
	//	c.findByPage(0,10);
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		//Session session = CassandraFactory.getSyncSession(sockets, "damon_space");
		Cluster cluster = CassandraFactory.getClusterInstance(sockets);
		Session session = CassandraFactory.getSyncSession(cluster, "damon_space");
		MappingManager manager = new MappingManager(session);
		UserAccessor accessor = manager.createAccessor(UserAccessor.class);
		Result<User> lists = accessor.getUserByPage("Damon18",10);
		for (User user : lists) {
			System.out.println(user.toString());
		}
		Mapper<User> m = new MappingManager(session).mapper(User.class);
		for(int i = 0; i < 100;i++){
	        User u1 = new User(i+1, "Damon"+i,"paul@yahoo.com");
	        u1.setYear(2014);
	        u1.setGender("男");
	        m.save(u1);
		}
		//UUID u = UUID.fromString("f2f8e53a-cc5b-4d3e-ba7c-2b3c23dfdfeb");
       // User queryUser = m.get(u);  //查询by primary key
       // System.out.println(queryUser.getName());
     
	}
//	public <T> Result<T> findByPage(String cql, Object[] values,int offset, int pageSize){
//		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
//		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
//		sockets.add(socket);
//		//Session session = CassandraFactory.getSyncSession(sockets, "damon_space");
//		Cluster cluster = CassandraFactory.getClusterInstance(sockets);
//		Session session = CassandraFactory.getSyncSession(cluster, "damon_space");
//		Mapper<T> m = new MappingManager(session).mapper(T.class);
//	}

	public void findByPage(int offset,int pageSize){
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		//Session session = CassandraFactory.getSyncSession(sockets, "damon_space");
		Cluster cluster = CassandraFactory.getClusterInstance(sockets);
		Session session = CassandraFactory.getSyncSession(cluster, "damon_space");
		String cql ="select * from users where name >= 'Damon10' limit 10 AllOW FILTERING";
		@SuppressWarnings("unused")
		ResultSet rs = session.execute(cql);
		System.out.println();
	}
}
