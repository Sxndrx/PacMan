package game;

import game.dataBase.HibernateUtil;
import javafx.application.Platform;

public class Utils {
    public static void exitGame(){
        HibernateUtil.shutdown();
        Platform.exit();
    }
}
