import controller.*
import dao.*
import repository.CandidatoRepository
import repository.CompetenciaRepository
import repository.EmpresaRepository
import repository.VagaRepository
import service.*
import util.DatabaseConnectionManager
import util.IConnectionFactory
import view.*

class Linketinder {

    static void main(String[] args) {
        DatabaseConnectionManager.inicializa("POSTGRES")
        IConnectionFactory factory = DatabaseConnectionManager.getInstancia().getFactory()

        // DAOs
        CompetenciaDAO competenciaDAO = new CompetenciaDAO(factory)
        CandidatoRepository candidatoRepo = new CandidatoDAO(competenciaDAO, factory)
        EmpresaRepository empresaRepo = new EmpresaDAO(factory)
        VagaRepository vagaRepo = new VagaDAO(competenciaDAO, factory)
        CompetenciaRepository competenciaRepo = competenciaDAO

        // Services
        CandidatoService candidatoService = new CandidatoService(candidatoRepo)
        EmpresaService empresaService = new EmpresaService(empresaRepo)
        VagaService vagaService = new VagaService(vagaRepo)
        CompetenciaService competenciaService = new CompetenciaService(competenciaRepo)

        // Views
        Scanner leitor = new Scanner(System.in)
        CandidatoView candidatoView = new CandidatoView(leitor)
        EmpresaView empresaView = new EmpresaView(leitor)
        VagaView vagaView = new VagaView(leitor)
        CompetenciaView competenciaView = new CompetenciaView()
        MenuView menuView = new MenuView(leitor)

        // Controllers
        CandidatoController candidatoController = new CandidatoController(candidatoService, candidatoView)
        EmpresaController empresaController = new EmpresaController(empresaService, empresaView)
        VagaController vagaController = new VagaController(vagaService, empresaService, vagaView)
        CompetenciaController competenciaController = new CompetenciaController(competenciaService, competenciaView)
        MenuController menuController = new MenuController(menuView, candidatoController, empresaController, vagaController, competenciaController)

        menuController.iniciar()
    }
}
