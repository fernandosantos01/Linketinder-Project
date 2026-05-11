package util

import java.sql.Connection
import java.sql.DriverManager

class H2TestConnectionFactory implements IConnectionFactory {
    @Override
    Connection createConnection() {
        String url = "jdbc:h2:mem:linketinder_test;DB_CLOSE_DELAY=-1"
        return DriverManager.getConnection(url, "sa", "")
    }
}
