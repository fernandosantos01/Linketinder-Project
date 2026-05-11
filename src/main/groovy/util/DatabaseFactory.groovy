package util

class DatabaseFactory {
    static IConnectionFactory getFactory(String tipoDb) {
        if (tipoDb == null) {
            throw new IllegalArgumentException("Tipo de banco de dados não suportado.")
        }
        switch (tipoDb.toUpperCase()) {
            case "POSTGRES":
                return new PostgresConnectionFactory()
            case "TEST":
                return new H2TestConnectionFactory()
            default:
                throw new IllegalArgumentException("Tipo de banco de dados não suportado.")
        }
    }
}
