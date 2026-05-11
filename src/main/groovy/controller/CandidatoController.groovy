package controller

import service.CandidatoService
import view.CandidatoView

class CandidatoController {
    private final CandidatoService service
    private final CandidatoView view

    CandidatoController(CandidatoService service, CandidatoView view) {
        this.service = service
        this.view = view
    }

    void listar() {
        view.exibirCandidatos(service.listarCandidatos())
    }

    void cadastrar() {
        def candidato = view.lerCandidato()
        try {
            service.cadastrarCandidato(candidato)
            view.exibirSucesso(candidato.nome)
        } catch (Exception e) {
            view.exibirErro(e.message)
        }
    }
}
