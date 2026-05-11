package controller

import service.EmpresaService
import view.EmpresaView

class EmpresaController {
    private final EmpresaService service
    private final EmpresaView view

    EmpresaController(EmpresaService service, EmpresaView view) {
        this.service = service
        this.view = view
    }

    void listar() {
        view.exibirEmpresas(service.listarEmpresas())
    }

    void cadastrar() {
        def empresa = view.lerEmpresa()
        try {
            service.cadastrarEmpresas(empresa)
            view.exibirSucesso(empresa.nome)
        } catch (Exception e) {
            view.exibirErro(e.message)
        }
    }
}
