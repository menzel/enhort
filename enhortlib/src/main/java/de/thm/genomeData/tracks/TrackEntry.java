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
package de.thm.genomeData.tracks;

import java.util.Random;

/**
 * Object mapping helper class
 */
public class TrackEntry {
    private String name;
    private String description;
    private String filepath;
    private String type;
    private String assembly;
    private String cellline;
    private String pack;

    private String sourceURL;
    private String source;

    private int filesize;
    private int dbID;

    public TrackEntry(String name,
                      String description,
                      String assembly,
                      String cellline,
                      String pack) {

        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellline = cellline;
        this.pack = pack;
        this.source = "";
        this.sourceURL = "";
        // This constructor is used only for creating tests tracks or converted tracks.
        // A random id is sufficient:
        this.dbID = 1000 * 1000 + new Random().nextInt(1000);
    }


    public TrackEntry(String name,
                      String description,
                      String assembly,
                      String cellline,
                      String pack,
                      String source,
                      String sourceURL) {

        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellline = cellline;
        this.pack = pack;
        this.source = source;
        this.sourceURL = sourceURL;
    }


    public TrackEntry(String name,
                      String description,
                      String filepath,
                      String type,
                      String assembly,
                      String cellline,
                      int filesize,
                      String pack,
                      int dbID,
                      String source,
                      String sourceURL) {
        this.name = name;
        this.description = description;
        this.filepath = filepath;
        this.type = type;
        this.assembly = assembly;
        this.cellline = cellline;
        this.filesize = filesize;
        this.pack = pack;
        this.dbID = dbID;
        this.source = source;
        this.sourceURL = sourceURL;
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

    public int getId() {
        return dbID;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public String getSource() {
        return source;
    }
}
