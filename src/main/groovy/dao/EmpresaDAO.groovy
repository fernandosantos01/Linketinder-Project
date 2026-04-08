package dao

import domain.Empresa
import util.DatabaseConnection

class EmpresaDAO {
    static List<Empresa> listarEmpresas() {
        def lista = []
        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return lista
            sql.eachRow("SELECT * FROM empresas") { row ->
                def empresa = new Empresa(
                        id: row.id,
                        nome: row.nome,
                        cnpj: row.cnpj,
                        email: row.email,
                        descricao: row.descricao,
                        pais: row.pais,
                        estado: row.estado,
                        cep: row.cep
                )
            }
        } catch (Exception e) {
            print("Erro ao listar as empresas: ${e.message}")
        }
        return lista
    }

    static void salvarEmpresa(Empresa e) {
        try (def sql = DatabaseConnection.getConnection()) {
            if (!sql) return
            sql.execute("""
                    INSERT INTO empresas (nome, cnpj, email, descricao, pais, estado, cep)
                    VALUES (${e.nome}, ${e.cnpj}, ${e.email}, ${e.descricao}, ${e.pais}, ${e.estado}, ${e.cep})
""")
            println "Empresa '${e.nome}' cadastrada com sucesso!"

        } catch (Exception ex) {
            println "Erro ao salvar a empresa: ${ex}"
        }
    }
}
