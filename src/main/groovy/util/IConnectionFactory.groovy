package util

import java.sql.Connection

interface IConnectionFactory {
    Connection createConnection()
}