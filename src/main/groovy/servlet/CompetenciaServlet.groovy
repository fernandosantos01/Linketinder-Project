package servlet

import groovy.json.JsonOutput
import service.CompetenciaService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CompetenciaServlet extends HttpServlet {
    private final CompetenciaService competenciaService

    CompetenciaServlet(CompetenciaService competenciaService) {
        this.competenciaService = competenciaService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        try {
            List<String> competencias = competenciaService.listarCompetencias()
            resp.status = HttpServletResponse.SC_OK
            resp.writer.write(JsonOutput.toJson(competencias))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }
}
