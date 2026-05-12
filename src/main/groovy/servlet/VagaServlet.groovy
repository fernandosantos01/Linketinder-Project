package servlet

import domain.Vaga
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import service.VagaService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VagaServlet extends HttpServlet {
    private final VagaService vagaService

    VagaServlet(VagaService vagaService) {
        this.vagaService = vagaService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        try {
            def lista = vagaService.listarVagas().collect { v ->
                [id: v.id, nome: v.nome, descricao: v.descricao,
                 local: v.local, empresaId: v.empresaId, competencias: v.competencias]
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
            Vaga vaga = new Vaga(
                    nome: body.nome,
                    descricao: body.descricao ?: "",
                    local: body.local ?: "",
                    empresaId: body.empresaId as Integer,
                    competencias: body.competencias ?: []
            )
            vagaService.salvarVaga(vaga)
            resp.status = HttpServletResponse.SC_CREATED
            resp.writer.write(JsonOutput.toJson([mensagem: "Vaga publicada com sucesso"]))
        } catch (IllegalArgumentException e) {
            resp.status = HttpServletResponse.SC_BAD_REQUEST
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }
}
