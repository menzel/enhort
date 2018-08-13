// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.genomeData.sql;

import de.thm.genomeData.tracks.TrackEntry;
import de.thm.misc.Genome;
import de.thm.run.BackendServer;
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
        conn = null;
        String link = "jdbc:sqlite:" + BackendServer.dbfilepath;

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

                String assembly = rs.getString("genome_assembly");
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

        String sql = "SELECT * FROM enhort_view " + whereClause;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                TrackEntry entry = new TrackEntry(rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("directory") + "/" + rs.getString("bed_filename"),
                        (rs.getString("category").contains("Scored") ? "scored" : "inout"),
                        rs.getString("genome_assembly"),
                        rs.getString("cell_line"),
                        rs.getInt("lines"),
                        rs.getString("category"),
                        rs.getInt("id"),
                        rs.getString("project"),
                        rs.getString("url")
                );
                entries.add(entry);
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        return entries;
    }


    /**
     * Returns all cell lines from the db
     *
     * @return cell lines as Sorted map, with parents/childrens
     */
    public SortedMap<String, List<String>> getAllCellLines() {

        SortedMap<String, List<String>> celllines = new TreeMap<>();

        try {
            // get all cellliens without children
            String parents = "SELECT cell_line FROM cell_lines";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(parents);

            while (rs.next()) {
                celllines.put(rs.getString("cell_line"), null);
            }

        } catch (SQLException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        return celllines;
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
    }
}
