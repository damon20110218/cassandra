package com.cfets.ts.cassandra;

import java.net.InetSocketAddress;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
//import com.google.common.util.concurrent.ListenableFuture;

public class CassandraFactory {
	/**
	 * 根据集群IP与Port获取集群
	 * @param sockets
	 * @return
	 */
	public static Cluster getClusterInstance(List<InetSocketAddress> sockets){
		Cluster cluster = Cluster.builder().addContactPointsWithPorts(sockets).build();
		return cluster;
	}
	/**
	 * 为keyspace获取同步session，建议单例模式，一个keyspace只有一个session
	 * @param sockets
	 * @param keyspace
	 * @return
	 */
	public static Session getSyncSession(List<InetSocketAddress> sockets,String keyspace){
		return getClusterInstance(sockets).connect(keyspace);
	}
	public static Session getSyncSession(Cluster cluster,String keyspace){
		return cluster.connect(keyspace);
	}
	/**
	 * 
	 * @param sockets
	 * @param keyspace
	 * @return
	 */
//	public ListenableFuture<Session> getAsyncSession(List<InetSocketAddress> sockets,String keyspace){
//		return getClusterInstance(sockets).connectAsync(keyspace);
//	QueryOptions options = new QueryOptions();
//    options.setConsistencyLevel(ConsistencyLevel.QUORUM);
//
//    Cluster cluster = Cluster.builder()
//            .addContactPoint(IP)
//            .withCredentials("cassandra", "cassandra")
//            .withQueryOptions(options)
//            .build();

//	}
	
	public static void closeSession(Session session) {
		session.close();
	}
	public static void closeCluster(Cluster cluster){
		cluster.close();
	}
}
