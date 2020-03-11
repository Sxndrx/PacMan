package game.dataBase;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;

@javax.persistence.Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "score")
    private int score;

    @Column(name = "playdate")
    private Timestamp playdate;

    public Score() {
    }

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
        playdate = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getPlaydate() {
        return playdate;
    }

    public void setPlaydate(Timestamp playdate) {
        this.playdate = playdate;
    }

    @Override
    public String toString() {
        return name+": "+score+" "+playdate.toString();
    }
}
