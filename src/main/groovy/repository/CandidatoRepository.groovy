package repository

import domain.Candidato

interface CandidatoRepository {
    List<Candidato> listarCandidatos()
    void salvarCandidato(Candidato candidato)
}