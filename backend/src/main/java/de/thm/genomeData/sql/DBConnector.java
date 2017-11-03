package de.thm.genomeData.sql;

import de.thm.misc.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class DBConnector {
    private Connection conn;

    private final Logger logger = LoggerFactory.getLogger(DBConnector.class);

    public void connect(){
        final String path;
        conn = null;

        if (System.getenv("HOME").contains("menzel")) {
            path = "/home/menzel/Desktop/THM/lfba/enhort/";
        } else {
            path = "/home/mmnz21/enhort/";
        }

        String link = "jdbc:sqlite:" + path + "track.db";

        try {

            conn = DriverManager.getConnection(link);

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }
    }

    @Deprecated
    public Map<Genome.Assembly, Map<String, Integer>> getChrSizes(){
        Map<Genome.Assembly, Map<String, Integer>> sizes = new HashMap<>();

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

                sizes.put(Genome.Assembly.valueOf(assembly), map);
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }


        return sizes;

    }

    public List<TrackEntry> getAllTracks() {
        return getAllTracks("");
    }

    public List<TrackEntry> getAllTracks(String whereClause) {
        List<TrackEntry> entries = new ArrayList<>();

        if(conn == null)
            throw new RuntimeException("No connection to the database");

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
                        rs.getInt("filesize"),
                        rs.getString("package"));

                entries.add(entry);
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }


        return entries;
    }

    public List<String> getCompilationByName(String name, Genome.Assembly assembly){
        List<String> trackNames = new ArrayList<>();

        String sql = "SELECT * FROM compilationsView WHERE compilationName == '" + name + "' AND assembly == '" + assembly + "'";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                trackNames.add(rs.getString("trackName"));
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        return trackNames;
    }

    /**
     * Returns all cell lines from the db
     *
     * @return cell lines as Sorted map, with parents/childrens
     */
    public SortedMap<String,List<String>> getAllCellLines() {

        SortedMap<String, List<String>> celllines = new TreeMap<>();


        try {

            // get all cellliens without children
            String parents = "SELECT name FROM celllines WHERE parent = ''";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(parents);

            while(rs.next()){
                celllines.put(rs.getString("name"), null);
            }

            // get celllines with children
            String children = "SELECT name, parent FROM celllines WHERE parent != '' ORDER BY parent";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(children);

            String oldParent = "";
            List<String> tmpNames = new ArrayList<>();

            while(rs.next()) {

                String currentParent = rs.getString("parent");

                if(oldParent.equals(currentParent)){
                    tmpNames.add(rs.getString("name"));

                } else {
                    if(tmpNames.size() > 0)
                        celllines.put(oldParent, tmpNames);

                    tmpNames = new ArrayList<>();
                    oldParent = currentParent;
                }
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        return celllines;
    }


    public TrackEntry createTrackEntry(String name, String description, String filepath, String type, String assembly, String cellline, int filesize, String pack) {

        return new TrackEntry(name, description, filepath, type, assembly, cellline, filesize, pack);
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
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
        private String pack;
        private int filesize;

        TrackEntry(String name, String description, String filepath, String type, String assembly, String cellline, int filesize, String pack) {
            this.name = name;
            this.description = description;
            this.filepath = filepath;
            this.type = type;
            this.assembly = assembly;
            this.cellline = cellline;
            this.filesize = filesize;
            this.pack = pack;
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

        public String getPack() {
            return pack;
        }

    }

}
