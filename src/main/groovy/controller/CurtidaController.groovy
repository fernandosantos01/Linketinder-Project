package controller

import domain.Match
import service.CurtidaService
import view.CurtidaView

class CurtidaController {
    private final CurtidaService curtidaService
    private final CurtidaView view

    CurtidaController(CurtidaService curtidaService, CurtidaView view) {
        this.curtidaService = curtidaService
        this.view = view
    }

    void candidatoCurteVaga() {
        def ids = view.lerCurtidaCandidato()
        try {
            Match match = curtidaService.candidatoCurteVaga(ids[0], ids[1])
            if (match) {
                view.exibirMatch(match)
            } else {
                view.exibirCurtidaRegistrada()
            }
        } catch (Exception e) {
            view.exibirErro(e.message)
        }
    }

    void empresaCurteCandidato() {
        def ids = view.lerCurtidaEmpresa()
        try {
            Match match = curtidaService.empresaCurteCandidato(ids[0], ids[1])
            if (match) {
                view.exibirMatch(match)
            } else {
                view.exibirCurtidaRegistrada()
            }
        } catch (Exception e) {
            view.exibirErro(e.message)
        }
    }
}
