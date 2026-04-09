package dao

import domain.Empresa
import util.DataBaseConnection
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO {

    List<Empresa> listarEmpresas() {
        def lista = []
        String query = "SELECT * FROM empresas"

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                def emp = new Empresa()
                emp.id = rs.getInt("id")
                emp.nome = rs.getString("nome")
                emp.cnpj = rs.getString("cnpj")
                emp.email = rs.getString("email")
                emp.descricao = rs.getString("descricao")
                emp.pais = rs.getString("pais")
                emp.estado = rs.getString("estado")
                emp.cep = rs.getString("cep")

                lista << emp
            }
        } catch (Exception e) {
            println "\n🚨 ERRO FATAL NO LISTAR EMPRESAS: ${e.message}"
            e.printStackTrace()
        }
        return lista
    }

    void salvarEmpresa(Empresa e) {
        String query = """
            INSERT INTO empresas (nome, cnpj, email, descricao, pais, estado, cep)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, e.nome)
            stmt.setString(2, e.cnpj)
            stmt.setString(3, e.email)
            stmt.setString(4, e.descricao)
            stmt.setString(5, e.pais)
            stmt.setString(6, e.estado)
            stmt.setString(7, e.cep)

            stmt.executeUpdate()
            println "Empresa '${e.nome}' salva com sucesso!"

        } catch (Exception ex) {
            println "Erro ao salvar empresa: ${ex.message}"
        }
    }
}