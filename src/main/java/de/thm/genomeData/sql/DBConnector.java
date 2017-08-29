package de.thm.genomeData.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    private Connection conn;

    public void connect(){
        conn = null;

        String path = "/home/menzel/Desktop/THM/lfba/enhort/";
        String link = "jdbc:sqlite:" + path + "track.db";

        try {
            conn = DriverManager.getConnection(link);

            System.out.println("connected to sqlite db");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public Connection getConn() {
        return conn;
    }

    public List<String> getAllTracks() {
        List<String> paths = new ArrayList<>();

        String sql = "SELECT * FROM tracks";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                paths.add(rs.getString("file"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return paths;
    }
}
