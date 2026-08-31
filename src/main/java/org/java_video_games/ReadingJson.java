package org.java_video_games;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

public class ReadingJson {
    public static void main(String[] args) {
        ObjectMapper configJsonObjectMapper = new ObjectMapper();
        File configJsonFile = new File("src/main/resources/Game_Data/Config.json");
        JsonNode configData = configJsonObjectMapper.readTree(configJsonFile);
//        System.out.println(Arrays.toString(configData.tileMap));
        System.out.println(configData.get("tileMap").asArray());
        System.out.println(configData.get("allTimeHighScore").asInt());
    }
}
