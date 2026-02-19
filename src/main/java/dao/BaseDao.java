package dao;


import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;              // ★これが正しい DataSource

public abstract class BaseDao {

    private static DataSource ds;

    static {
        try {
            InitialContext ic = new InitialContext(); // ★var をやめて明示型に
            // Tomcat の <Resource name="jdbc/ipad" ...> と一致させる
            ds = (DataSource) ic.lookup("java:comp/env/jdbc/ipad");
            System.out.println("[BaseDao] DataSource lookup OK: java:comp/env/jdbc/ipad");
        } catch (NamingException e) {
            // ここで止めて原因を見やすくする（暫定で DriverManager にフォールバックも可）
            throw new RuntimeException("DataSource 取得に失敗しました: java:comp/env/jdbc/ipad", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        // javax.sql.DataSource の getConnection() を呼ぶ（正しい import であればコンパイル通ります）
        return ds.getConnection();
    }
}
