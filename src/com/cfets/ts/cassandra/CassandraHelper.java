package com.cfets.ts.cassandra;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cfets.cwap.s.util.BeanUtil;
import com.cfets.ts.cassandra.annotation.Cassandra;
import com.cfets.ts.cassandra.annotation.CassandraColumn;
import com.cfets.ts.cassandra.annotation.CassandraColumnModel;
import com.cfets.ts.cassandra.annotation.CassandraModel;
import com.cfets.ts.cassandra.bean.UserBean;
import com.cfets.ts.cassandra.util.CommonUtil;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class CassandraHelper {

	private static Logger logger = LoggerFactory
			.getLogger(CassandraHelper.class);

	private static Map<String, CassandraModel> models = new HashMap<String, CassandraModel>();

	/**
	 * 保存对象至Cassandra数据库中
	 * 
	 * @param object
	 * @return
	 */
	public boolean SaveOrUpdate(Object object) {
		CassandraModel cm = getCassandraModel(object.getClass(), false);
		StringBuffer sb = new StringBuffer();
		String tablename = cm.getName();
		sb.append("insert into " + tablename + " (");

		List<CassandraColumnModel> columns = cm.getColumns();
		for (CassandraColumnModel cassandraColumnModel : columns) {
			sb.append(cassandraColumnModel.getColumnName() + ",");
		}
		sb.deleteCharAt(sb.toString().length() - 1);
		sb.append(") values (");
		for (int i = 0; i < columns.size(); i++) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.toString().length() - 1);
		sb.append(")");
		String cql = sb.toString().toUpperCase();
		Object[] values = getValues(cm, object, false);
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
		logger.debug(cql + values.toString());
		executePrepareCQL(cql, session, values);
		logger.debug("save successfully!!!!");
		return true;
	}

	// insert Json insert into tablename Json
	// '{"userId":122,"userName":"zhangsan","password":"1234"}'
	public void saveJsonToDB() {

	}

	/**
	 * 获取对象各个域对应的值
	 * 
	 * @param cm
	 * @param instance
	 * @return
	 */
	private Object[] getValues(CassandraModel cm, Object instance, boolean isDel) {
		List<CassandraColumnModel> columns = cm.getColumns();
		List values = new ArrayList();
		for (CassandraColumnModel column : columns) {
			if (isDel) {
				if (column.isPrimaryKey()) {
					Object value = BeanUtil.getFieldValue(instance,
							column.getFieldName());
					values.add(value);
				}
			} else {
				Object value = BeanUtil.getFieldValue(instance,
						column.getFieldName());
				values.add(value);
			}

		}
		return values.toArray();
	}

	/***
	 * 获取类对象的注解信息，及类对象中域（Field）
	 * 
	 * @param clz
	 * @return
	 */
	public CassandraModel getCassandraModel(Class<?> clz, boolean isDel) {
		CassandraModel cm = (CassandraModel) models.get(clz.getName());
		if (cm == null) {
			Cassandra cassandra = clz.getAnnotation(Cassandra.class);
			if (null == cassandra) {
				logger.error(clz.getName() + " 不存在注解@Cassandra...");
				return null;
			}
			cm = new CassandraModel();
			cm.setClzName(clz.getName());
			cm.setCassandra(cassandra);
			List<CassandraColumnModel> ccms = new ArrayList<CassandraColumnModel>();
			List<Field> fields = getClassSimpleFields(clz, true);
			for (Field field : fields) {
				CassandraColumn cc = field.getAnnotation(CassandraColumn.class);
				if (null != cc) {
					CassandraColumnModel ccm = new CassandraColumnModel();
					String fieldName = field.getName();
					String colName = cc.name();
					if (CommonUtil.isNullOrSpace(colName)) {
						colName = fieldName;
					}
					ccm.setColumnName(colName);
					ccm.setFieldName(fieldName);
					ccm.setCc(cc);
					ccm.setPrimaryKey(cc.isPrimaryKey());
					if (isDel) {
						if (cc.isPrimaryKey())
							ccms.add(ccm);
					} else
						ccms.add(ccm);
				}
			}
			cm.setColumns(ccms);
		}
		return cm;
	}

	/**
	 * 获取类对象中简单域如int,string,data... ancestor标志是否获取父类的域
	 * 
	 * @param clz
	 * @param ancestor
	 * @return
	 */
	public static List<Field> getClassSimpleFields(Class<?> clz,
			boolean ancestor) {
		List<Field> fields = new ArrayList<Field>();
		return getClassSimpleFields(fields, clz, ancestor);
	}

	/**
	 * 递归调用
	 * 
	 * @param fields
	 * @param clz
	 * @param ancestor
	 * @return
	 */
	public static List<Field> getClassSimpleFields(List<Field> fields,
			Class<?> clz, boolean ancestor) {
		for (Field field : clz.getDeclaredFields())
			fields.add(field);
		if (ancestor) {
			Class p = clz.getSuperclass();
			if (p != null)
				getClassSimpleFields(fields, p, ancestor);
		}
		return fields;
	}

	/**
	 * 删除对象
	 * 
	 * @param object
	 */
	public void delete(Object object) {
		CassandraModel cm = getCassandraModel(object.getClass(), true);
		StringBuffer sb = new StringBuffer();
		String tablename = cm.getName();
		sb.append("delete from " + tablename + " where ");
		List<CassandraColumnModel> columns = cm.getColumns();
		for (CassandraColumnModel cassandraColumnModel : columns) {
			sb.append(cassandraColumnModel.getColumnName() + "=? and ");
		}
		sb.delete(sb.toString().length() - 4, sb.toString().length() - 1);
		String cql = sb.toString().toUpperCase();
		Object[] values = getValues(cm, object, true);
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
		logger.debug(cql + values.toString());
		executePrepareCQL(cql, session, values);
		session.close();
	}

	/**
	 * 按照主键删除
	 * 
	 * @param primaryKey
	 */
	public void delete(String tablename, String[] primaryKeys, Object[] values) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from " + tablename + " where ");
		if (primaryKeys == null || primaryKeys.length == 0)
			throw new RuntimeException("请指定主键域");
		for (String tmp : primaryKeys)
			sb.append(tmp + "=? and ");
		sb.delete(sb.toString().length() - 4, sb.toString().length() - 1);
		String cql = sb.toString().toUpperCase();
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
		logger.debug(cql + values.toString());
		executePrepareCQL(cql, session, values);
		session.close();
	}

	/**
	 * 忽略某些特殊域不用保存
	 * 
	 * @param field
	 * @return
	 */
	public boolean ignoreFilterField(Field field) {
		return true;
	}

	/**
	 * 执行cql语句
	 * 
	 * @param cql
	 * @param session
	 * @return
	 */
	public static ResultSet executeCQL(String cql, Session session) {
		return session.execute(cql);
	}

	/**
	 * 采用预编译占位符方式
	 * 
	 * @param cql
	 * @param session
	 * @param values
	 * @return
	 */
	public ResultSet executePrepareCQL(String cql, Session session,
			Object... values) {
		PreparedStatement prepareStatement = session.prepare(cql);
		BoundStatement bindStatement = new BoundStatement(prepareStatement)
				.bind(values);
		return session.execute(bindStatement);
	}

	public void queryByKey(Object key, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
	}

	// 首先制定你想查看的列名，其次是你想要查询的表，再就是你的查询条件，你排序的字段，是否为分页查询，每页大小，及offset
	public void query(String[] columns, String tableName) {
		List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
		PagingState pagingState = PagingState.fromString("");
		Statement st = new SimpleStatement("your query");
		ResultSet rs = session.execute(QueryBuilder.select(columns)
				.from(tableName).where(QueryBuilder.eq("userId", 1))
				.and(QueryBuilder.lt("userName", "f"))
				.and(QueryBuilder.gt("userName", "a")).and(QueryBuilder.gt(QueryBuilder.token("a","b"), QueryBuilder.fcall("token",10, 12)))
				.orderBy(QueryBuilder.asc("a"), QueryBuilder.desc("b"))
				.allowFiltering().setFetchSize(10));
		PrintResultSet(rs, columns);
	}

	public void queryPaging(int pageSize, int offset) {

	}

	/**
	 * 打印结果对象
	 * 
	 * @param rs
	 */
	public static void PrintResultSet(ResultSet rs, String[] columns) {
		List<Object[]> res = new ArrayList<Object[]>();
		Iterator<Row> iterator = rs.iterator();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Object[] o = new Object[columns.length];
			for (int i = 0; i < columns.length; i++) {
				o[i] = row.getObject(columns[i]);
				System.out.println(columns[i] + ":" + o[i].toString());
			}
			res.add(o);
		}
	}

	public static void main(String[] args) {
		UserBean bean = new UserBean(123, "zhangsi", "2345");
		CassandraHelper h = new CassandraHelper();
		h.SaveOrUpdate(bean);
		// h.delete(bean);
		// h.delete("user", new String[] { "userId" }, new Integer[] { 120 });
		h.query(new String[] { "userId", "userName", "password" }, "user");
	}
}
