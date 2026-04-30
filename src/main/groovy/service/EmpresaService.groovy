package service

import domain.Empresa
import repository.EmpresaRepository

class EmpresaService {
    private final EmpresaRepository empresaRepository;

    EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    List<Empresa> listarEmpresas() {
        empresaRepository.listarEmpresas()
    }

    void cadastrarEmpresas(Empresa empresa) {
        validarDadosDaEmpresa(empresa)
        empresaRepository.salvarEmpresa(empresa)
    }

    private static void validarDadosDaEmpresa(Empresa empresa) {
        if (!empresa.nome?.trim()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório")
        }
        if (!empresa.cnpj?.trim()) {
            throw new IllegalArgumentException("CNPJ é obrigatório")
        }
    }
}
