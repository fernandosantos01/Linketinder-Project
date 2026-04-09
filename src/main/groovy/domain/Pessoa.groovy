package domain

import groovy.transform.Canonical

@Canonical
class Pessoa {
    Integer id
    String nome
    String email
    String pais
    String estado
    String cep
    String descricao
    List<String> habilidades
}
