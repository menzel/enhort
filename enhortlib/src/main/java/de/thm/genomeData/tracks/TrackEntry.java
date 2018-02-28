package de.thm.genomeData.tracks;

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

    public TrackEntry(String name, String description, String assembly, String cellline, String pack) {
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellline = cellline;
        this.pack = pack;
    }


    public TrackEntry(String name, String description, String filepath, String type, String assembly, String cellline, int filesize, String pack, int dbID, String source, String sourceURL) {
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
