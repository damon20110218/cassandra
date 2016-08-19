package com.cfets.ts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class CassandraDemo {

	private Cluster cluster;

	private Session session;

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * 创建keyspace
	 * 
	 * @param keyspaceName
	 */
	public void creatKeyspace(String keyspaceName) {
		String cql = "CREATE KEYSPACE IF NOT EXISTS" + keyspaceName + " WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};";
		this.session.execute(cql);
	}
	/**
	 * 创建表
	 * @param keyspaceName
	 * @param tableName
	 */
	public void createTable(String keyspaceName,String tableName){
		String cql = "CREATE TABLE "+tableName +" ( " +
				     "userId int PRIMARY KEY, " +
				     "userName text, " +
				     "password text );";
		this.session.execute(cql);
	}
	public void createTable1(String keyspaceName,String tableName){
		String cql = "CREATE TABLE "+tableName +" ( " +
				     "name varchar PRIMARY KEY, " +
				     "age varchar, " +
				     "password varchar );";
		this.session.execute(cql);
	}
	/**
	 * 
	 */
	public void createColumnFamily(){
		String cql ="CREATE COLUMN FAMILY user with " 
					 +"comparator=UTF8Type  "
					 +"and default_validation_class=UTF8Type "
					 +"and key_validation_class=UTF8Type "
					 +"and column_metadata= "
					 +"[ "
					 +" {column_name: name, validation_class: UTF8Type,index_type: KEYS}, "
					 +" {column_name: age, validation_class: UTF8Type,index_type: KEYS}," 
					 +" {column_name: password, validation_class: UTF8Type,index_type: KEYS} "
					 +"]; ";
		this.session.execute(cql);
	}
	/**
	 * 插入数据 String cql = "insert into mykeyspace.tablename(a,b) values(1,2);"
	 * 
	 * @param keyspaceName
	 * @param tableName
	 * @param column
	 * @param values
	 */
	public void addData(String keyspaceName, String tableName, String[] column,
			String[] values) {
		this.session.execute(QueryBuilder.insertInto(keyspaceName, tableName)
				.values(column, values));
	}

	/**
	 * 
	 * @param cql
	 */
	public void addData(String cql) {
		this.session.execute(cql);
	}

	/**
	 * 删除记录String cql = "delete from mykeyspace.tablename where a=1";
	 * 
	 * @param keyspaceName
	 * @param tableName
	 * @param conditions
	 */
	public void deleteDate(String keyspaceName, String tableName,
			Map<String, String> conditions) {
		this.session.execute(QueryBuilder.delete()
				.from(keyspaceName, tableName)
				.where(QueryBuilder.eq("userId", "1")));
	}

	public void deleteData(String cql) {
		this.session.execute(cql);
	}

	public void updateData(String keyspaceName,String tableName,String updateColumn,String updateValue) {
		this.session.execute(QueryBuilder.update(keyspaceName, tableName)
			       .with(QueryBuilder.set(updateColumn, updateValue))
			       .where(QueryBuilder.eq("userId", 1)));

	}
    /**
     * String cql = “update mykeyspace.tablename set b=2 where a=1”
     * @param cql
     */
	public void updateData(String cql){
		this.session.execute(cql);
	}
	/**
	 * String cql = “select a, b from mykeyspace.tablename where a>1 and b<0”
	 * @param cql
	 * @return
	 */
	public List<Object[]> queryData(String cql){
		ResultSet result = this.session.execute(cql);
		Iterator<Row> iterator = result.iterator();
		while(iterator.hasNext())
		{
			Row row = iterator.next();
			System.out.println(row.getInt("userId"));
			System.out.println(row.getString("userName"));
		}

		return null;
	}
	public void queryDataByStatement(String[] values){
		String cql ="select * from user where userId=? and userName=?" ;
		BoundStatement bindStatement = 
				session.prepare(
				cql)
				.bind(values[0],values[1]);
		ResultSet result = session.execute(bindStatement);
		Iterator<Row> iterator = result.iterator();
		while(iterator.hasNext())
		{
			Row row = iterator.next();
			System.out.println(row.getInt("userId"));
			System.out.println(row.getString("userName"));
		}


	}
	/**
	 * 连接节点
	 * 
	 * @param node
	 */
	public void connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).withPort(9042).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}

		this.session = cluster.connect("damon_space");
	}

	public void insertData() {
		PreparedStatement insertStatement = getSession().prepare(
				"INSERT INTO pimin_net.users "
						+ "(id, first_name, last_name, age, emails,avatar) "
						+ "VALUES (?, ?, ?, ?, ?, ?);");

		BoundStatement boundStatement = new BoundStatement(insertStatement);
		Set<String> emails = new HashSet<String>();
		emails.add("zhangsan@qq.com");
		emails.add("lisi@163.com");

		java.nio.ByteBuffer avatar = null;
		try {
			avatar = toByteBuffer("f:\\user.png");
			avatar.flip();
			System.out.println("头像大小：" + avatar.capacity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		getSession()
				.execute(
						boundStatement.bind(
								UUID.fromString("756716f7-2e54-4715-9f00-91dcbea6cf50"),
								"pi", "min", 10, emails, avatar));
	}

	public void loadData() {
		ResultSet resultSet = getSession().execute(
				"SELECT first_name,last_name,age,avatar FROM pimin_net.users;");
		System.out
				.println(String
						.format("%-30s\t%-20s\t%-20s\n%s", "first_name",
								"last_name", "age",
								"-------------------------------+-----------------------+--------------------"));
		for (Row row : resultSet) {
			System.out.println(String.format("%-30s\t%-20s\t%-20s",
					row.getString("first_name"), row.getString("last_name"),
					row.getInt("age")));

			ByteBuffer byteBuffer = row.getBytes("avatar");
			System.out.println("头像大小："
					+ +(byteBuffer.limit() - byteBuffer.position()));

			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream("f:\\2.png");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileOutputStream.write(byteBuffer.array(),
						byteBuffer.position(),
						byteBuffer.limit() - byteBuffer.position());
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println();
	}

	/**
	 * 客户端关闭
	 */
	public void close() {
		cluster.close();
	}

	/**
	 * 读取文件
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static ByteBuffer toByteBuffer(String filename) throws IOException {

		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		CassandraDemo client = new CassandraDemo();
		client.connect("127.0.0.1");
		//client.creatKeyspace("damon_space");
		client.createTable1("damon_space","users");
	//	client.createColumnFamily();
	//	String cql = "insert into damon_space.user(userId,userName,password) values(1,'damon','root');";
		//client.addData(cql);
	//	String cql = "select userId, userName from damon_space.user";
	//	client.queryData(cql);
//		client.insertData();
//		client.loadData();
		client.session.close();
		client.close();
	}
}
