package controller

import service.EmpresaService
import service.VagaService
import view.VagaView

class VagaController {
    private final VagaService vagaService
    private final EmpresaService empresaService
    private final VagaView view

    VagaController(VagaService vagaService, EmpresaService empresaService, VagaView view) {
        this.vagaService = vagaService
        this.empresaService = empresaService
        this.view = view
    }

    void listar() {
        view.exibirVagas(vagaService.listarVagas())
    }

    void publicar() {
        def empresas = empresaService.listarEmpresas()
        def vaga = view.lerVaga(empresas)
        if (vaga == null) return
        try {
            vagaService.salvarVaga(vaga)
            view.exibirSucesso(vaga.nome)
        } catch (Exception e) {
            view.exibirErro(e.message)
        }
    }
}
