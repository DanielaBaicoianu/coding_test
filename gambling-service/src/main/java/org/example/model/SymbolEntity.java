package org.example.model;

public class SymbolEntity {

    private String symbolName;
    private SymbolType symbolType;
    private Double rewardMultiplier;

    private int numberOfAppearance;


    public SymbolEntity(String symbolName, Double rewardMultiplier) {
        this.symbolName = symbolName.toUpperCase();
        if (symbolName.length() > 1)
            this.symbolType = SymbolType.SYMBOL_BONUS;
        else
            this.symbolType = SymbolType.SYMBOL_STANDARD;
        this.rewardMultiplier = rewardMultiplier;
        this.numberOfAppearance = 1;

    }

    public String getSymbolName() {
        return symbolName;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public Double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void increaseNumberOfAppearance() {
        this.numberOfAppearance++;
    }

    public int getNumberOfAppearance() {
        return this.numberOfAppearance;
    }

    @Override
    public String toString() {
        return "SymbolEntity{" +
                "symbolName='" + symbolName + '\'' +
                ", symbolType=" + symbolType +
                ", rewardMultiplier=" + rewardMultiplier +
                '}';
    }
}
