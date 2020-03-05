package dataBase;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

//mysql> create table score
//        -> (
//        -> id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
//        -> name VARCHAR(40),
//        -> score INT NOT NULL,
//        -> playdate TIMESTAMP);
//        Query OK, 0 rows affected (0.07 sec)


@Entity
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
}
