package com.becker.simulation.liquid.config;

/**
 * Different configurations to choose from.
 *
 * @author Bary Becker
 */
public enum ConfigurationEnum {
    
    BASIC("Basic", "A stream of water into a pool",  getFileBase() + "config1.xml"),
    SPIGOT("Spigot", "A spigot aimed to the right", getFileBase() + "spigot.xml"),
    WATER_WALL("Water wall", "An initial wall of water on the right", getFileBase() + "wallOfWater.xml");


    private String name;
    private String description;
    private String fileName;
            
    /**
     * Constructor
     * @param the nice name for the configuration that will show in the dropDown.
     * @param the actual xml filename that condatins the config data.
     */
    private ConfigurationEnum(String name, String description, String fileName) {
        this.name = name;
        this.description = description;
        this.fileName = fileName;
    }

    private static final String getFileBase() {
        return "com/becker/simulation/liquid/data/";
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return name;
    }
}
