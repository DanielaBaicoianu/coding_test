package org.example;

import org.example.service.RewardsService;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static org.example.service.RewardsService.symbolsInventory;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String[][] matrix = {
                {"A", "A", "A"},                       // row 1
                {"B", "B", "B"},                             // row2
                {"B", "+1000", "A"}                            // row3
        };
        String fileName = args[0];
        Double betAmount = Double.valueOf(args[1]);

        RewardsService rewardsService = new RewardsService(fileName);
        RewardsService.getReward(matrix, 3, 3);
        System.out.println(symbolsInventory);
        System.out.println(rewardsService.calculateFinalBet(matrix, betAmount));
    }
}