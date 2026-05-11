package controller

import service.CompetenciaService
import view.CompetenciaView

class CompetenciaController {
    private final CompetenciaService service
    private final CompetenciaView view

    CompetenciaController(CompetenciaService service, CompetenciaView view) {
        this.service = service
        this.view = view
    }

    void listar() {
        view.exibirCompetencias(service.listarCompetencias())
    }
}
