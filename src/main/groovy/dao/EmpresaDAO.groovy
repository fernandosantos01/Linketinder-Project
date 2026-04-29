package dao

import domain.Empresa
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO {

    static List<Empresa> listarEmpresas() {
        String query = "SELECT * FROM empresas"
        List<Empresa> empresas = []

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                empresas << construirEmpresaDoResultSet(resultSet)
            }
        } catch (Exception erro) {
            println "Erro ao listar empresas: ${erro.message}"
        }
        return empresas
    }

    static void salvarEmpresa(Empresa empresa) {
        validarDadosDaEmpresa(empresa)

        String query = """
            INSERT INTO empresas (nome, cnpj, email, descricao, pais, estado, cep)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conexao = DataBaseConnection.getConnection();
             PreparedStatement statement = conexao.prepareStatement(query)) {

            preencherParametrosEmpresa(statement, empresa)
            statement.executeUpdate()
            println "Empresa '${empresa.nome}' salva com sucesso!"

        } catch (Exception erro) {
            println "Erro ao salvar empresa: ${erro.message}"
        }
    }

    private static void validarDadosDaEmpresa(Empresa empresa) {
        if (!empresa.nome?.trim()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório")
        }
        if (!empresa.cnpj?.trim()) {
            throw new IllegalArgumentException("CNPJ é obrigatório")
        }
    }

    private static void preencherParametrosEmpresa(PreparedStatement statement, Empresa empresa) {
        statement.setString(1, empresa.nome)
        statement.setString(2, empresa.cnpj)
        statement.setString(3, empresa.email)
        statement.setString(4, empresa.descricao)
        statement.setString(5, empresa.pais)
        statement.setString(6, empresa.estado)
        statement.setString(7, empresa.cep)
    }

    private static Empresa construirEmpresaDoResultSet(ResultSet resultSet) {
        return new Empresa(
            id: resultSet.getInt("id"),
            nome: resultSet.getString("nome"),
            cnpj: resultSet.getString("cnpj"),
            email: resultSet.getString("email"),
            descricao: resultSet.getString("descricao"),
            pais: resultSet.getString("pais"),
            estado: resultSet.getString("estado"),
            cep: resultSet.getString("cep")
        )
    }
}