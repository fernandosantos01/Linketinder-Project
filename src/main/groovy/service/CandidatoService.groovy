package service

import domain.Candidato
import repository.CandidatoRepository

class CandidatoService {
    private final CandidatoRepository candidatoRepository

    CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    List<Candidato> listarCandidatos() {
        return candidatoRepository.listarCandidatos()
    }

    void cadastrarCandidato(Candidato candidato){
        validarDadosDoCandidato(candidato)
        candidatoRepository.salvarCandidato(candidato)
    }

    private static void validarDadosDoCandidato(Candidato candidato) {
        if (!candidato.nome?.trim()) {
            throw new IllegalArgumentException("Nome do candidato é obrigatório")
        }
        if (!candidato.cpf?.trim()) {
            throw new IllegalArgumentException("CPF é obrigatório")
        }
        if (!candidato.dataNascimento) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória")
        }
    }
}
