package domain

import groovy.transform.Canonical

@Canonical
class Pessoa {
    String nome
    String email
    String estado
    String cep
    String descricao
    List<String> habilidades
}
