package util

class DatabaseConnectionManager {
    private static DatabaseConnectionManager instancia
    private final IConnectionFactory factory

    private DatabaseConnectionManager(IConnectionFactory factory) {
        this.factory = factory
    }

    static void inicializa(String tipoDb) {
        if (instancia == null) {
            instancia = new DatabaseConnectionManager(DatabaseFactory.getFactory(tipoDb))
        }
    }

    static DatabaseConnectionManager getInstancia() {
        if (instancia == null) {
            throw new IllegalArgumentException("DatabaseConnectionManager não inicializado")
        }
        return instancia
    }

    IConnectionFactory getFactory() {
        return factory
    }
}
