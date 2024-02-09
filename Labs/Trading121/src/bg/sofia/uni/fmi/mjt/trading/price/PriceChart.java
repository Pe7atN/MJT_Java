package bg.sofia.uni.fmi.mjt.trading.price;

public class PriceChart implements PriceChartAPI{
        private double microsoftStockPrice;
        private double googleStockPrice;
        private double amazonStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice){
     this.microsoftStockPrice = microsoftStockPrice;
     this.amazonStockPrice = amazonStockPrice;
     this.googleStockPrice = googleStockPrice;
    }

    @Override
    public double getCurrentPrice(String stockTicker){
        if(stockTicker == null)
            return 0.0;

        return switch (stockTicker) {
            case "MSFT" -> Math.round(microsoftStockPrice * 100.0) / 100.0;
            case "AMZ" -> Math.round(amazonStockPrice * 100.0) / 100.0;
            case "GOOG" -> Math.round(googleStockPrice * 100.0) / 100.0;
            default -> 0.0;
        };

    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange){
        if(stockTicker == null || percentChange <= 0)
            return false;

        switch (stockTicker) {
            case "MSFT" -> {
                microsoftStockPrice += microsoftStockPrice*(percentChange/ 100.0);
                return true;
            }
            case "AMZ" -> {
                amazonStockPrice += amazonStockPrice*(percentChange/ 100.0);
                return true;
            }
            case "GOOG" -> {
                googleStockPrice += googleStockPrice*(percentChange/ 100.0);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
