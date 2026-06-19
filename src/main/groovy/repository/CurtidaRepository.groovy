package repository

import domain.Match

interface CurtidaRepository {
    Match candidatoCurteVaga(Integer candidatoId, Integer vagaId)
    Match empresaCurteCandidato(Integer empresaId, Integer candidatoId)
}
