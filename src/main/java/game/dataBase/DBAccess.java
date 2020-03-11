package game.dataBase;

import java.sql.*;
import java.util.ArrayList;

public class DBAccess {
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private final String dbName = "pacMan.score";

    public void open() throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.jdbc.Driver");
            //user = root,
            //password = Admin123$
            // change is required
            connection = DriverManager.getConnection("jdbc:mysql://localhost/pacMan?"+"user=root&password=Admin123$");

    }

    public ArrayList<String[]> readTenScores(){
        ArrayList<String[]> result = new ArrayList<>();
        try {
            open();
            preparedStatement = connection.prepareStatement("SELECT userName, points, date from score order by points desc limit ?,?");
            //preparedStatement.setString(1, "score");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, 9);
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);
            resultSet = preparedStatement.executeQuery();
            writeToArray(resultSet, result);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            close();
        }
        return result;
    }

    public int submitScore(int points, String userName) throws SQLException {
        int id=0;
        try {
            open();
            preparedStatement = connection.prepareStatement("insert into score (userName, points, date) values (?,?,sysdate())");
            preparedStatement.setString(1, userName);
            preparedStatement.setInt(2, points);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("select * from score order by id desc limit 1");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            close();
        }

        return id;
    }

    public int getHighestResult(){
        int points=0;
        try {
            open();
            preparedStatement = connection.prepareStatement("SELECT points from score order by points desc limit 1");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                points = resultSet.getInt(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        System.out.println(points);
        return points;
    }


    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if(preparedStatement!=null)
                preparedStatement.close();

            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {

        }
    }

    private void writeToArray(ResultSet resultSet, ArrayList<String[]> list) throws SQLException {

        while(resultSet.next()){
            String[] record = new String[resultSet.getMetaData().getColumnCount()];
            for(int i=1; i<=resultSet.getMetaData().getColumnCount(); i++){
                record[i-1]=resultSet.getString(i);
            }
            list.add(record);
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            for(int i=1; i<=resultSet.getMetaData().getColumnCount(); i++){
                System.out.println(resultSet.getMetaData().getColumnName(i) + " " + resultSet.getString(i));
            }
        }
    }
}
