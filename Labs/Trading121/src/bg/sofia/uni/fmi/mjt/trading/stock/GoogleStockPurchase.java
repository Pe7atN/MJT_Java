package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class GoogleStockPurchase implements StockPurchase{

    String stockTicker;
    int quantity;
    LocalDateTime purchaseTimestamp;
    double purchasePricePerUnit;


    public GoogleStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        stockTicker = "GOOG";
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

