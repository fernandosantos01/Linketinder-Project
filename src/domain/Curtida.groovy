package domain

class Curtida {
    Candidato candidato
    Empresa empresa
    boolean candCurtiu = false
    boolean empCurtiu = false

    Curtida(Candidato candidato, Empresa empresa) {
        this.candidato = candidato
        this.empresa = empresa
    }

    boolean isMatch() {
        return candCurtiu && empCurtiu
    }

}
