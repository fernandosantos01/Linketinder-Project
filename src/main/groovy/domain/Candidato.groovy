package domain

import groovy.transform.ToString

@ToString(includeNames = true)
class Candidato extends Pessoa {
    String cpf
    int idade;

    @Override
    String toString() {
        return """
        ------------------------------------------
        CANDIDATO: $nome ($idade anos)
        Email: $email | CPF: $cpf
        Local: $estado - CEP: $cep
        Competências: ${habilidades.join(', ')}
        Sobre: $descricao
        ------------------------------------------"""
    }
}
