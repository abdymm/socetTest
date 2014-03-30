package test.demo.db;

public class DbUtil {

	public static final String mergeWhere(String where1, String where2) {
		StringBuilder builder = new StringBuilder();
		if (where1 != null && !where1.isEmpty()) {
			builder.append("(");
			builder.append(where1);
			builder.append(")");
			builder.append(" AND ");
		}
		if (where2 != null && !where2.isEmpty()) {
			builder.append("(");
			builder.append(where2);
			builder.append(")");
		}
		return builder.toString();
	}

	public static final String createSql(String selection, String where,
			String order, String group) {
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		StringBuilder sqlBuilder = new StringBuilder(selection);
		if (where != null && !where.isEmpty()) {
			sqlBuilder.append(" ");
			sqlBuilder.append(where);
		}
		if (order != null && !order.isEmpty()) {
			sqlBuilder.append(" ");
			sqlBuilder.append(order);
		}
		if (group != null && !group.isEmpty()) {
			sqlBuilder.append(" ");
			sqlBuilder.append(group);
		}
		return sqlBuilder.toString();
	}
}
