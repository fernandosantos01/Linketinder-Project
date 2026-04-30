package domain

import groovy.transform.ToString
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@ToString(includeNames = true)
class Candidato extends Pessoa {
    String cpf
    LocalDate dataNascimento

    int getIdade() {
        if (dataNascimento) {
            return Period.between(dataNascimento, LocalDate.now()).years
        }
        return 0
    }

    @Override
    String toString() {
        return """
        ------------------------------------------
        ID: {$id}
        CANDIDATO: $nome (${getIdade()} anos)
        Email: $email | CPF: $cpf
        Data de Nascimento: ${dataNascimento?.format('dd/MM/yyyy' as DateTimeFormatter) ?: 'Não informada'}
        Local: $pais - $estado - CEP: $cep
        Competências: ${habilidades.join(', ') ?: 'Nenhuma'}
        Sobre: $descricao
        ------------------------------------------"""
    }
}