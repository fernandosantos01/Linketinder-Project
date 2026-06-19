import dao.*
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat
import repository.CandidatoRepository
import repository.CompetenciaRepository
import repository.CurtidaRepository
import repository.EmpresaRepository
import repository.VagaRepository
import service.*
import servlet.*
import util.DatabaseConnectionManager
import util.IConnectionFactory

class LinketinderServer {

    static void main(String[] args) {
        DatabaseConnectionManager.inicializa("POSTGRES")
        IConnectionFactory factory = DatabaseConnectionManager.getInstancia().getFactory()

        // DAOs
        CompetenciaDAO competenciaDAO = new CompetenciaDAO(factory)
        CandidatoRepository candidatoRepo = new CandidatoDAO(competenciaDAO, factory)
        EmpresaRepository empresaRepo = new EmpresaDAO(factory)
        VagaRepository vagaRepo = new VagaDAO(competenciaDAO, factory)
        CompetenciaRepository competenciaRepo = competenciaDAO
        CurtidaRepository curtidaRepo = new CurtidaDAO(factory)

        // Services
        CandidatoService candidatoService = new CandidatoService(candidatoRepo)
        EmpresaService empresaService = new EmpresaService(empresaRepo)
        VagaService vagaService = new VagaService(vagaRepo)
        CompetenciaService competenciaService = new CompetenciaService(competenciaRepo)
        CurtidaService curtidaService = new CurtidaService(curtidaRepo)

        // Tomcat
        Tomcat tomcat = new Tomcat()
        tomcat.setPort(8080)
        tomcat.getConnector()

        String docBase = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath()
        Context ctx = tomcat.addContext("", docBase)

        // Registro dos Servlets
        Tomcat.addServlet(ctx, "candidatoServlet", new CandidatoServlet(candidatoService))
        ctx.addServletMappingDecoded("/candidatos", "candidatoServlet")

        Tomcat.addServlet(ctx, "empresaServlet", new EmpresaServlet(empresaService))
        ctx.addServletMappingDecoded("/empresas", "empresaServlet")

        Tomcat.addServlet(ctx, "vagaServlet", new VagaServlet(vagaService))
        ctx.addServletMappingDecoded("/vagas", "vagaServlet")

        Tomcat.addServlet(ctx, "competenciaServlet", new CompetenciaServlet(competenciaService))
        ctx.addServletMappingDecoded("/competencias", "competenciaServlet")

        Tomcat.addServlet(ctx, "curtidaServlet", new CurtidaServlet(curtidaService))
        ctx.addServletMappingDecoded("/curtidas/*", "curtidaServlet")

        tomcat.start()
        println "Linketinder API iniciada em http://localhost:8080"
        tomcat.getServer().await()
    }
}
