package org.example.service;

import org.example.model.SymbolEntity;
import org.example.model.SymbolType;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.example.fileUtils.FileProcessing.getSymbolsFromJSON;
import static org.example.fileUtils.FileProcessing.writeRewardToFile;


public class RewardsService {

    public static HashMap<String, SymbolEntity> symbolsInventory = new HashMap<>();

    public static HashMap<String, Double> symbolsValuesHashMap;

    public static HashMap<String, String> horizontalSymbols = new HashMap<>();

    public RewardsService(String fileName) {
        try {
            symbolsValuesHashMap = getSymbolsFromJSON(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getReward(String[][] symbolsMatrix, int matrixNumberOfRows, int matrixNumberOfColumns) {
        String currentSymbolName = null;
        SymbolEntity currentSymbol = null;
        for (int row = 0; row < matrixNumberOfRows; row++) {
            boolean isHorizontal = true;
            for (int col = 0; col < matrixNumberOfColumns; col++) {

                currentSymbolName = symbolsMatrix[row][col].toUpperCase();
                currentSymbol = symbolsInventory.get(currentSymbolName);
                if (currentSymbol == null) {
                    currentSymbol = new SymbolEntity(currentSymbolName, symbolsValuesHashMap.get(currentSymbolName));
                } else {
                    currentSymbol.increaseNumberOfAppearance();
                }
                symbolsInventory.put(currentSymbolName, currentSymbol);
                if (col > 0 && symbolsMatrix[row][col] != symbolsMatrix[row][col - 1])
                    isHorizontal = false;
            }
            if (currentSymbol != null && isHorizontal)
                horizontalSymbols.put(currentSymbolName, "same_symbols_horizontally");
        }
    }

    public Double calculateFinalBet(String[][] symbolsMatrix, Double betAmount) throws FileNotFoundException, ParseException {
        boolean winningBet = false;
        List<String> rewardCalculations = new ArrayList<>();
        String bonusValue = null;

        for (String key : symbolsInventory.keySet()) {
            SymbolEntity currentSymbol = symbolsInventory.get(key);
            boolean isHorizontal = false;
            int horizontalMultiplier = 1;
            if (currentSymbol.getSymbolType().equals(SymbolType.SYMBOL_STANDARD) && currentSymbol.getNumberOfAppearance() >= 3) {

                String rewardString = currentSymbol.getSymbolName() + String.format("same_symbol_%s_times", currentSymbol.getNumberOfAppearance());
                if (horizontalSymbols.get(currentSymbol.getSymbolName()) != null) {
                    rewardString += ", " + horizontalSymbols.get(currentSymbol.getSymbolName());
                    isHorizontal = true;
                }
                rewardCalculations.add(rewardString);
                if (isHorizontal)
                    horizontalMultiplier = 2;
                if (!winningBet) {
                    betAmount = betAmount * currentSymbol.getRewardMultiplier() * getSameSymbolsRewardMultiplier(currentSymbol.getNumberOfAppearance()) * horizontalMultiplier;
                } else {
                    betAmount += betAmount * currentSymbol.getRewardMultiplier() * getSameSymbolsRewardMultiplier(currentSymbol.getNumberOfAppearance()) * horizontalMultiplier;
                }
                winningBet = true;

            } else if (currentSymbol.getSymbolType().equals(SymbolType.SYMBOL_BONUS) && !Objects.equals(currentSymbol.getSymbolName(), "MISS")) {
                String symbolName = currentSymbol.getSymbolName();
                bonusValue = symbolName;
                betAmount = symbolsValuesHashMap.get(symbolName) > 100 ? betAmount + symbolsValuesHashMap.get(symbolName) : betAmount * symbolsValuesHashMap.get(symbolName) * currentSymbol.getNumberOfAppearance();
            }
        }
        if (!winningBet)
            betAmount = 0.0;
        writeRewardToFile(symbolsMatrix, betAmount, rewardCalculations, bonusValue);
        return betAmount;
    }

    private double getSameSymbolsRewardMultiplier(int numberOfAppearance) {
        double result;
        switch (numberOfAppearance) {
            case 3:
                result = 1;
                break;
            case 4:
                result = 1.5;
                break;
            case 5:
                result = 2;
                break;
            case 6:
                result = 3;
                break;
            case 7:
                result = 5;
                break;
            case 8:
                result = 10;
                break;
            default:
                result = 20;
                break;
        }
        return result;
    }
}
