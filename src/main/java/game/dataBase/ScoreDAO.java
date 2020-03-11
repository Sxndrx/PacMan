package game.dataBase;


import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ScoreDAO {
    public static void insertNewScore(Score newScore){
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.save(newScore);
            transaction.commit();
        }catch (Exception e){
            if(transaction!=null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public static List<Score> getHighest(int num){
        List scores = null;
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            scores = session.createQuery("from Score ")
                    .setMaxResults(num)
                    .list();
        }
        return scores;
    }
}