package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.*;

import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI{
        private String owner;
        private PriceChartAPI priceChart;
        private StockPurchase[] stockPurchases;
        private int indexOfPurchases;
        private double budget;
        private final int maxSize;

    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.stockPurchases = new StockPurchase[maxSize];
        this.indexOfPurchases = 0;
        this.budget = budget;
        this.maxSize = maxSize;

    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.stockPurchases = new StockPurchase[maxSize];
        this.indexOfPurchases = 0;
        for(StockPurchase purchase : stockPurchases){
            this.stockPurchases[indexOfPurchases++] = purchase;
        }
        this.budget = budget;
        this.maxSize = maxSize;
    }

    /**
     * Purchases the provided quantity of stocks with the provided ticker. The budget in the portfolio should
     * decrease by the corresponding amount. If a stock is on-demand then naturally its price increases.
     * Every stock purchase should result in a 5% price increase of the purchased stock
     *
     * @param stockTicker the stock ticker
     * @param quantity    the quantity of stock that should be purchased
     * @return the stock purchase if it was successfully purchased. If the stock with the provided ticker is
     * not traded on the platform or the ticker is null, return null. If the budget is not enough to make the
     * purchase, return null. If quantity is not a positive number, return null. If the portfolio is already
     * at max size, return null.
     */
    public StockPurchase buyStock(String stockTicker, int quantity){

        if(stockTicker == null
                || (!stockTicker.equals("MSFT") && !stockTicker.equals("AMZ") && !stockTicker.equals("GOOG"))){
            return null;
        }

        if(quantity <= 0){
            return null;
        }

        if(indexOfPurchases >= maxSize){
            return null;
        }

        double priceOfStock = priceChart.getCurrentPrice(stockTicker);

        if(quantity * priceOfStock > budget){
            return null;
        }

        StockPurchase purchase = null;
        switch (stockTicker) {
            case "MSFT" -> {
                purchase = new MicrosoftStockPurchase(quantity,LocalDateTime.now(),priceOfStock);
            }
            case "AMZ" -> {
                purchase = new AmazonStockPurchase(quantity,LocalDateTime.now(),priceOfStock);
            }
            case "GOOG" -> {
                purchase = new GoogleStockPurchase(quantity,LocalDateTime.now(),priceOfStock);
            }
        }

        budget -= quantity * priceOfStock;
        priceChart.changeStockPrice(stockTicker,5); // up with 5%
        stockPurchases[indexOfPurchases++] = purchase;
        return purchase;
    }

    public StockPurchase[] getAllPurchases(){
        return stockPurchases;
    }

    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp){
        int count = 0;

        for(StockPurchase purchase : stockPurchases) {
            if(purchase == null){
                continue;
            }
            if (purchase.getPurchaseTimestamp().isAfter(startTimestamp)
                    && purchase.getPurchaseTimestamp().isBefore(endTimestamp)) {
                count++;
            }
        }

        StockPurchase[] purchasesInTimestamp = new StockPurchase[count];

        count = 0;
        for(StockPurchase purchase : stockPurchases) {
            if(purchase == null){
                continue;
            }
            if (purchase.getPurchaseTimestamp().isAfter(startTimestamp)
                    && purchase.getPurchaseTimestamp().isBefore(endTimestamp)) {
                purchasesInTimestamp[count++] = purchase;
            }
        }
        return purchasesInTimestamp;
    }

    public double getNetWorth(){
        double netWorth = 0;

        for(StockPurchase purchase : stockPurchases){
            if(purchase == null){
                continue;
            }
            netWorth += purchase.getQuantity()*priceChart.getCurrentPrice(purchase.getStockTicker());
        }

        return Math.round(netWorth * 100.0)/100.0;
    }

    public double getRemainingBudget(){
        return Math.round(budget*100.0)/100.0;
    }

    public String getOwner(){
        return this.owner;
    }
    
}
