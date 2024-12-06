package com.example.final_app.service;

import com.example.final_app.controller.PriceController;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class OKXWebSocketClient extends WebSocketClient {

    private final String symbol;
    private final PriceController priceController;

    public OKXWebSocketClient(URI serverUri, String symbol, PriceController priceController) {
        super(serverUri);
        this.symbol = symbol;
        this.priceController = priceController;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to OKX for symbol: " + symbol);
        String subscriptionMessage = String.format("{\"op\": \"subscribe\", \"args\": [{\"channel\": \"tickers\", \"instId\": \"%s\"}]}", symbol);
        send(subscriptionMessage);
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        if (json.has("data")) {
            JSONObject data = json.getJSONArray("data").getJSONObject(0);
            double buyPrice = data.getDouble("bidPx");
            double sellPrice = data.getDouble("askPx");
            double profit = sellPrice - buyPrice;
            double spread = (profit / buyPrice) * 100;

            priceController.updateData(symbol, "OKX", buyPrice, sellPrice, profit, spread);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from OKX: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
