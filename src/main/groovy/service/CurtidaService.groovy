package service

import domain.Match
import repository.CurtidaRepository

class CurtidaService {
    private final CurtidaRepository curtidaRepository

    CurtidaService(CurtidaRepository curtidaRepository) {
        this.curtidaRepository = curtidaRepository
    }

    Match candidatoCurteVaga(Integer candidatoId, Integer vagaId) {
        validarId(candidatoId, "Candidato")
        validarId(vagaId, "Vaga")
        return curtidaRepository.candidatoCurteVaga(candidatoId, vagaId)
    }

    Match empresaCurteCandidato(Integer empresaId, Integer candidatoId) {
        validarId(empresaId, "Empresa")
        validarId(candidatoId, "Candidato")
        return curtidaRepository.empresaCurteCandidato(empresaId, candidatoId)
    }

    private static void validarId(Integer id, String entidade) {
        if (!id || id <= 0) {
            throw new IllegalArgumentException("ID de ${entidade} inválido: ${id}")
        }
    }
}
