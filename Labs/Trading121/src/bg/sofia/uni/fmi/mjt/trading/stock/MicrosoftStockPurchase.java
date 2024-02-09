package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class MicrosoftStockPurchase implements StockPurchase{

    String stockTicker;
    int quantity;
    LocalDateTime purchaseTimestamp;
    double purchasePricePerUnit;


    public MicrosoftStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        stockTicker = "MSFT";
        this.purchaseTimestamp = purchaseTimestamp;
        this.quantity = quantity;
        this.purchasePricePerUnit = purchasePricePerUnit;
    }
    @Override
    public int getQuantity(){
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp(){
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit(){
        return purchasePricePerUnit;
    }

    @Override
    public double getTotalPurchasePrice(){
        return Math.round( (quantity * purchasePricePerUnit) * 100.0 ) / 100.0;
    }

    @Override
    public String getStockTicker(){
        return stockTicker;
    }
}
