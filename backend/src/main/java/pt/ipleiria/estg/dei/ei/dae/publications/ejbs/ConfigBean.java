package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;

import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {

    @Inject
    private UserBean userBean;

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @PostConstruct
    public void populateDB() {
        try {
            System.out.println("--- INICIANDO POPULAÇÃO DA BD ---");

            // Admin
            userBean.create("admin", "123", "Administrador", "admin@xyz.pt", Role.Administrador);

            // Outros users
            userBean.create("ColaboradorA", "123", "João A", "joao@xyz.pt", Role.Colaborador);
            userBean.create("ColaboradorB", "123", "Joana B", "joana@xyz.pt", Role.Colaborador);
            userBean.create("Responsavel", "123", "Manuel C", "manuel@xyz.pt", Role.Responsavel);

            System.out.println("--- BD POPULADA COM SUCESSO ---");

        } catch (Exception e) {
            // Isto vai mostrar o erro real na consola do servidor
            logger.severe("ERRO AO POPULAR BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
