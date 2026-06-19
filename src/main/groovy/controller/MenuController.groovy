package controller

import view.MenuView

class MenuController {
    private static final int OPCAO_SAIR = 0

    private final MenuView menuView
    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final VagaController vagaController
    private final CompetenciaController competenciaController
    private final CurtidaController curtidaController

    MenuController(
            MenuView menuView,
            CandidatoController candidatoController,
            EmpresaController empresaController,
            VagaController vagaController,
            CompetenciaController competenciaController,
            CurtidaController curtidaController
    ) {
        this.menuView = menuView
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.vagaController = vagaController
        this.competenciaController = competenciaController
        this.curtidaController = curtidaController
    }

    void iniciar() {
        int opcao = -1
        while (opcao != OPCAO_SAIR) {
            menuView.exibirOpcoes()
            opcao = menuView.lerOpcao()
            executar(opcao)
        }
        menuView.exibirFinalizando()
    }

    private void executar(int opcao) {
        switch (opcao) {
            case 1: empresaController.listar(); break
            case 2: candidatoController.listar(); break
            case 3: empresaController.cadastrar(); break
            case 4: candidatoController.cadastrar(); break
            case 5: vagaController.publicar(); break
            case 6: vagaController.listar(); break
            case 7: competenciaController.listar(); break
            case 8: curtidaController.candidatoCurteVaga(); break
            case 9: curtidaController.empresaCurteCandidato(); break
            case OPCAO_SAIR: break
            default: menuView.exibirOpcaoInvalida()
        }
    }
}
