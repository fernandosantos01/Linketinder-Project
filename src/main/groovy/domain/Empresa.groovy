package domain

import groovy.transform.Canonical

@Canonical
class Empresa extends Pessoa {
    String cnpj
    String pais


    @Override
    public String toString() {
        return """
        ------------------------------------------
        EMPRESA: $nome
        Email: $email | CNPJ: $cnpj
        Local: $pais, $estado - CEP: $cep
        Competências Esperadas: ${habilidades.join(', ')}
        Sobre: $descricao
        ------------------------------------------"""
    }
}
