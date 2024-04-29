package org.example.fileUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class FileProcessing {

    public static HashMap<String, Double> getSymbolsFromJSON(String fileName) throws IOException, ParseException {
        HashMap<String, Double> symbolsFromFile = new HashMap<>();
        Object jsonContent = new JSONParser().parse(new FileReader(fileName));

        JSONObject jsonContentAsJson = (JSONObject) jsonContent;

        int numberOfRows = Integer.parseInt(String.valueOf(jsonContentAsJson.get("rows")));
        int numberOfColumns = Integer.parseInt(String.valueOf(jsonContentAsJson.get("columns")));

        System.out.println(numberOfRows);
        System.out.println(numberOfColumns);

        Object symbolsContent = new JSONParser().parse(String.valueOf(jsonContentAsJson.get("symbols")));
        JSONArray symbolsContentJsonItems = new JSONArray();
        symbolsContentJsonItems.add(symbolsContent);

        Iterator symbolsJsonIterator;
        Iterator symbolIterator = symbolsContentJsonItems.iterator();

        while (symbolIterator.hasNext()) {
            symbolsJsonIterator = ((Map) symbolIterator.next()).entrySet().iterator();
            while (symbolsJsonIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) symbolsJsonIterator.next();

                Object valueRewardObj = pair.getValue();
                JSONObject valueReward = (JSONObject) valueRewardObj;
                if (!Objects.equals(String.valueOf(pair.getKey()), "MISS")) {

                    try {
                        Double rewardValue;
                        String rewardType = null;
                        if (valueReward.get("reward_multiplier") != null) rewardType = "reward_multiplier";
                        else if (valueReward.get("extra") != null) rewardType = ("extra");
                        rewardValue = Double.parseDouble(String.valueOf(valueReward.get(rewardType)));
                        symbolsFromFile.put(String.valueOf(pair.getKey()).toUpperCase(), rewardValue);

                    } catch (Exception e) {

                    }
                }
            }
        }
        symbolsFromFile.put("MISS", 0.0);
        return symbolsFromFile;
    }


    public static void writeRewardToFile(String[][] inputMatrix, Double reward, List<String> rewardCalculations, String bonusSymbol) throws FileNotFoundException, ParseException {
        JSONObject winJsonFileContent = new JSONObject();

        JSONArray appliedWinningCombinationsAsJson = new JSONArray();
        appliedWinningCombinationsAsJson.add(parseMatrixAsList(inputMatrix, 3, 3));

        winJsonFileContent.put("matrix", appliedWinningCombinationsAsJson);


        winJsonFileContent.put("reward", reward);

        Map<String, String> appliedWinningCombinations = new LinkedHashMap<>(rewardCalculations.size());
        for (String rew : rewardCalculations) {
            String symbol = rew.substring(0, 1);
            String winningCombination = rew.substring(1);
            appliedWinningCombinations.put(symbol, winningCombination);

        }

        appliedWinningCombinationsAsJson = new JSONArray();

        appliedWinningCombinationsAsJson.add(appliedWinningCombinations);
        winJsonFileContent.put("applied_winning_combinations", appliedWinningCombinationsAsJson);

        winJsonFileContent.put("applied_bonus_symbol", bonusSymbol);

        PrintWriter pw = new PrintWriter("betResult.json");
        pw.write(winJsonFileContent.toJSONString());

        pw.flush();
        pw.close();
    }

    private static List<String> parseMatrixAsList(String[][] matrix, int rows, int columns){
        List<String> finalMatrixConverted = new ArrayList<>();
        List<String> interimList;
        for(int i = 0 ; i < rows; i ++)
        {
            interimList = new ArrayList<>();
            for(int j = 0 ; j < columns ; j++) {
                interimList.add(matrix[i][j]);
            }
            finalMatrixConverted.add(interimList.toString());
        }
        return finalMatrixConverted;
    }

}

