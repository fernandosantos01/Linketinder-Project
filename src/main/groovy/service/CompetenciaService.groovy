package service

import repository.CompetenciaRepository

class CompetenciaService {
    private final CompetenciaRepository competenciaRepository

    CompetenciaService(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository
    }

    List<String> listarCompetencias() {
        competenciaRepository.listarCompetencias()
    }
}
