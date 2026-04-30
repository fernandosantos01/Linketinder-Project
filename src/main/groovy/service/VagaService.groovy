package service

import domain.Vaga
import repository.VagaRepository

class VagaService {
    private final VagaRepository vagaRepository;

    VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository
    }

    List<Vaga> listarVagas() {
        vagaRepository.listarVagas()
    }

    void salvarVaga(Vaga vaga) {
        validarDadosDaVaga(vaga)
        vagaRepository.salvarVaga(vaga)
    }

    private static void validarDadosDaVaga(Vaga vaga) {
        if (!vaga.nome?.trim()) {
            throw new IllegalArgumentException("Nome da vaga é obrigatório")
        }
        if (!vaga.empresaId || vaga.empresaId <= 0) {
            throw new IllegalArgumentException("Vaga '${vaga.nome}' precisa de um ID de empresa válido")
        }
        if (vaga.local.isEmpty()) {
            vaga.local = "Home-Office/Não informada"
        }
    }
}
