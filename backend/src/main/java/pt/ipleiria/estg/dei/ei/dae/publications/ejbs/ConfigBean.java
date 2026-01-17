package pt.ipleiria.estg.dei.ei.dae.publications.ejbs;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import pt.ipleiria.estg.dei.ei.dae.publications.entities.enumeration.Role;

import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    private UserBean userBean;

    @EJB
    private TagBean tagBean;

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @PostConstruct
    public void populateDB() {
        try {
            System.out.println("--- A INICIAR O CONFIGBEAN (POPULAR BD) ---");

            userBean.create("admin", "123", "Administrador", "admin@xyz.pt", Role.Administrador);

            userBean.create("Responsavel", "123", "Manuel C", "manuel@xyz.pt", Role.Responsavel);

            userBean.create("ColaboradorA", "123", "João A", "joao@xyz.pt", Role.Colaborador);
            userBean.create("ColaboradorB", "123", "Joana B", "joana@xyz.pt", Role.Colaborador);

            tagBean.create("Engenharia de Software");
            tagBean.create("Inteligencia Artificial");
            tagBean.create("Redes de Computadores");
            tagBean.create("Sistemas Distribuidos");
            tagBean.create("Programação Web");

            System.out.println("--- BD POPULADA COM SUCESSO! ---");

        } catch (Exception e) {
            logger.severe("ERRO CRÍTICO NO CONFIGBEAN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}