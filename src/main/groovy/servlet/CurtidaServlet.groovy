package servlet

import domain.Match
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import service.CurtidaService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CurtidaServlet extends HttpServlet {
    private final CurtidaService curtidaService

    CurtidaServlet(CurtidaService curtidaService) {
        this.curtidaService = curtidaService
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        String pathInfo = req.getPathInfo() ?: ""

        try {
            def body = new JsonSlurper().parse(req.reader)

            if (pathInfo == "/candidato") {
                Match match = curtidaService.candidatoCurteVaga(body.candidatoId as Integer, body.vagaId as Integer)
                resp.status = HttpServletResponse.SC_CREATED
                resp.writer.write(buildResposta(match))
            } else if (pathInfo == "/empresa") {
                Match match = curtidaService.empresaCurteCandidato(body.empresaId as Integer, body.candidatoId as Integer)
                resp.status = HttpServletResponse.SC_CREATED
                resp.writer.write(buildResposta(match))
            } else {
                resp.status = HttpServletResponse.SC_NOT_FOUND
                resp.writer.write(JsonOutput.toJson([erro: "Use /curtidas/candidato ou /curtidas/empresa"]))
            }
        } catch (IllegalArgumentException e) {
            resp.status = HttpServletResponse.SC_BAD_REQUEST
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }

    private static String buildResposta(Match match) {
        if (match) {
            return JsonOutput.toJson([
                mensagem   : "MATCH! Candidato e empresa se curtiram!",
                match      : true,
                candidatoId: match.candidatoId,
                empresaId  : match.empresaId,
                vagaId     : match.vagaId
            ])
        }
        return JsonOutput.toJson([mensagem: "Curtida registrada com sucesso", match: false])
    }
}
