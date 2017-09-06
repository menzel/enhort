package de.thm.genomeData.sql;

import de.thm.logo.GenomeFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnector {
    private Connection conn;

    public void connect(){
        conn = null;

        String path = "/home/menzel/Desktop/THM/lfba/enhort/";
        String link = "jdbc:sqlite:" + path + "track.db";

        try {
            conn = DriverManager.getConnection(link);

            System.out.println("DBConnector: connected to sqlite db");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<GenomeFactory.Assembly, Map<String, Integer>> getChrSizes(){
        Map<GenomeFactory.Assembly, Map<String, Integer>> sizes = new HashMap<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tracks");

            while (rs.next()) {

                String assembly = rs.getString("assembly");
                Map<String, Integer> map = new HashMap<>();

                Statement innerstmt = conn.createStatement();
                ResultSet inner = innerstmt.executeQuery("SELECT * FROM chrSizes WHERE assembly = '" + assembly + "';");

                while(inner.next())
                    map.put(inner.getString("chrNumber"), inner.getInt("size"));

                sizes.put(GenomeFactory.Assembly.valueOf(assembly), map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return sizes;

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
