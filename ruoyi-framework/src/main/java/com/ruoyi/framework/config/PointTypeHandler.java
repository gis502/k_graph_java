package com.ruoyi.framework.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointTypeHandler extends BaseTypeHandler<Point> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Point parameter, JdbcType jdbcType) throws SQLException {
        PGgeometry pgGeometry = new PGgeometry(parameter);
        ps.setObject(i, pgGeometry); // 将 Point 转为 PGgeometry
    }

    @Override
    public Point getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getPoint(rs.getObject(columnName));
    }

    @Override
    public Point getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getPoint(rs.getObject(columnIndex));
    }

    @Override
    public Point getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getPoint(cs.getObject(columnIndex));
    }

    private Point getPoint(Object obj) {
        if (obj == null) {
            return null;
        }
        PGgeometry geometry = (PGgeometry) obj;
        return (Point) geometry.getGeometry(); // 转换为 Point
    }
}
