package repository

import domain.Vaga

interface VagaRepository{
    List<Vaga> listarVagas()
    void salvarVaga(Vaga vaga)
}