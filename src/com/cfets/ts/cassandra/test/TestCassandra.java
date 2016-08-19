package com.cfets.ts.cassandra.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cfets.ts.cassandra.CassandraFactory;
import com.cfets.ts.cassandra.CassandraHelper;
import com.cfets.ts.cassandra.util.CqlBuilder;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class TestCassandra {
	
	private static Logger logger = LoggerFactory.getLogger(CqlBuilder.class);
	public static void main(String[] args){
//		TSocket so = new TSocket("127.0.0.1", 9160);
//		TTransport trans = new TFramedTransport(so); 
//		trans.open(); 
//		TProtocol protocol = new TBinaryProtocol(trans); 
//		Cassandra.Iface client = new Cassandra.Client(protocol);
		 
		
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		//Session session = CassandraFactory.getSyncSession(sockets, "damon_space");
		Cluster cluster = CassandraFactory.getClusterInstance(sockets);
		Session session = CassandraFactory.getSyncSession(cluster, "damon_space");
		//create table
		logger.debug("Start create table!!!!!");
		String createTableCQL = "CREATE TABLE log ( " +
			     "id uuid, " +
			     "message varchar, " +
			     "email set<text>,"
			     + "scores list<varchar>,"
			     + "todo map<timestamp,text>,"
			     + "PRIMARY KEY(id,message)"
			     + ");";
		createTableCQL = "CREATE TABLE testCompact ( " +
			     "id int, " +
			     "column1 varchar, "
			     + "cloumn2 text,"
			     + "cloumn3 varint," 
			     + "PRIMARY KEY(id,column1)" 
			     + ");";
		CassandraHelper.executeCQL(createTableCQL, session);
		//insert
		String insertCQL ="insert into user(userId,userName,password) values(4,'鱼子蛋','root');";
		//CassandraHelper.PrintResultSet(CassandraHelper.executeCQL(insertCQL, session));
		//update
		String updateCQL = "update user set password='1234' where userId =2";
		//CassandraHelper.PrintResultSet(CassandraHelper.executeCQL(updateCQL, session));
		//query
		String queryCQL = "select userId, userName, password from user";
		//CassandraHelper.PrintResultSet(CassandraHelper.executeCQL(queryCQL, session));
		//delete
		String deleteCQL = "delete from user where userId=2";
		//CassandraHelper.PrintResultSet(CassandraHelper.executeCQL(deleteCQL, session));
		CassandraFactory.closeSession(session);
		CassandraFactory.closeCluster(cluster);
	}

}
