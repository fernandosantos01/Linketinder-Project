package servlet

import domain.Candidato
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import service.CandidatoService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDate

class CandidatoServlet extends HttpServlet {
    private final CandidatoService candidatoService

    CandidatoServlet(CandidatoService candidatoService) {
        this.candidatoService = candidatoService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        try {
            def lista = candidatoService.listarCandidatos().collect { c ->
                [id: c.id, nome: c.nome, email: c.email, cpf: c.cpf,
                 estado: c.estado, pais: c.pais, habilidades: c.habilidades]
            }
            resp.status = HttpServletResponse.SC_OK
            resp.writer.write(JsonOutput.toJson(lista))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        try {
            def body = new JsonSlurper().parse(req.reader)
            Candidato candidato = new Candidato(
                    nome: body.nome,
                    cpf: body.cpf,
                    email: body.email,
                    dataNascimento: LocalDate.parse(body.dataNascimento as String),
                    estado: body.estado ?: "",
                    pais: body.pais ?: "",
                    cep: body.cep ?: "",
                    descricao: body.descricao ?: "",
                    habilidades: body.habilidades ?: []
            )
            candidatoService.cadastrarCandidato(candidato)
            resp.status = HttpServletResponse.SC_CREATED
            resp.writer.write(JsonOutput.toJson([mensagem: "Candidato cadastrado com sucesso"]))
        } catch (IllegalArgumentException e) {
            resp.status = HttpServletResponse.SC_BAD_REQUEST
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }
}
