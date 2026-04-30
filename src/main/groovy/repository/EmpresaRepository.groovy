package repository

import domain.Empresa

interface EmpresaRepository {
    List<Empresa> listarEmpresas()
    void salvarEmpresa(Empresa empresa)
}