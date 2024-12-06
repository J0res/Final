package com.example.final_app.service;

import com.example.final_app.controller.PriceController;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class PoloniexWebSocketClient extends WebSocketClient {

    private final String symbol;
    private final PriceController priceController;

    public PoloniexWebSocketClient(URI serverUri, String symbol, PriceController priceController) {
        super(serverUri);
        this.symbol = symbol;
        this.priceController = priceController;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Poloniex for symbol: " + symbol);
        String subscriptionMessage = String.format("{\"command\": \"subscribe\", \"channel\": \"%s\"}", symbol);
        send(subscriptionMessage);
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        if (json.has("asks") && json.has("bids")) {
            double buyPrice = json.getJSONArray("bids").getJSONArray(0).getDouble(0);
            double sellPrice = json.getJSONArray("asks").getJSONArray(0).getDouble(0);
            double profit = sellPrice - buyPrice;
            double spread = (profit / buyPrice) * 100;

            priceController.updateData(symbol, "Poloniex", buyPrice, sellPrice, profit, spread);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from Poloniex: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
