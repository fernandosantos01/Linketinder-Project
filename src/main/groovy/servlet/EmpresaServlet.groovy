package servlet

import domain.Empresa
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import service.EmpresaService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class EmpresaServlet extends HttpServlet {
    private final EmpresaService empresaService

    EmpresaServlet(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8")
        try {
            def lista = empresaService.listarEmpresas().collect { e ->
                [id: e.id, nome: e.nome, cnpj: e.cnpj, email: e.email,
                 estado: e.estado, descricao: e.descricao]
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
            Empresa empresa = new Empresa(
                    nome: body.nome,
                    cnpj: body.cnpj,
                    email: body.email ?: "",
                    descricao: body.descricao ?: "",
                    estado: body.estado ?: "",
                    pais: body.pais ?: "",
                    cep: body.cep ?: "",
                    habilidades: []
            )
            empresaService.cadastrarEmpresas(empresa)
            resp.status = HttpServletResponse.SC_CREATED
            resp.writer.write(JsonOutput.toJson([mensagem: "Empresa cadastrada com sucesso"]))
        } catch (IllegalArgumentException e) {
            resp.status = HttpServletResponse.SC_BAD_REQUEST
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            resp.writer.write(JsonOutput.toJson([erro: e.message]))
        }
    }
}
