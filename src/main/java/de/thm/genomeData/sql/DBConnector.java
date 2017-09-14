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



    public List<TrackEntry> getAllTracks() {
        return getAllTracks("");
    }

    public List<TrackEntry> getAllTracks(String whereClause) {
        List<TrackEntry> entries = new ArrayList<>();

        String sql = "SELECT * FROM tracks " + whereClause;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                TrackEntry entry = new TrackEntry(rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("file"),
                        rs.getString("type"),
                        rs.getString("assembly"),
                        rs.getString("cellline"),
                        rs.getInt("filesize"));

                entries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return entries;
    }

    public List<String> getCompilationByName(String name, GenomeFactory.Assembly assembly){
        List<String> trackNames = new ArrayList<>();

        String sql = "SELECT * FROM compilationsView WHERE compilationName == '" + name + "' AND assembly == '" + assembly + "'";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                trackNames.add(rs.getString("trackName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trackNames;
    }


    /**
     * Inner Class for Object mapping
     */
    public class TrackEntry {
        private String name;
        private String description;
        private String filepath;
        private String type;
        private String assembly;
        private String cellline;
        private int filesize;

        public TrackEntry(String name, String description, String filepath, String type, String assembly, String cellline, int filesize) {
            this.name = name;
            this.description = description;
            this.filepath = filepath;
            this.type = type;
            this.assembly = assembly;
            this.cellline = cellline;
            this.filesize = filesize;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getFilepath() {
            return filepath;
        }

        public String getType() {
            return type;
        }

        public String getAssembly() {
            return assembly;
        }

        public String getCellline() {
            return cellline;
        }

        public int getFilesize() {
            return filesize;
        }
    }
}
