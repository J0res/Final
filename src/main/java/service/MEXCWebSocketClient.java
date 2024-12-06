package service;

import kg.com.api_salamat.controller.PriceController;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class MEXCWebSocketClient extends WebSocketClient {

    private final String symbol;
    private final PriceController priceController;

    public MEXCWebSocketClient(URI serverUri, String symbol, PriceController priceController) {
        super(serverUri);
        this.symbol = symbol;
        this.priceController = priceController;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to MEXC for symbol: " + symbol);
        send("{\"method\":\"SUBSCRIPTION\",\"params\":[\"spot@public.limit.depth.v3.api@" + symbol + "@20\"]}");
    }

    @Override
    public void onMessage(String message) {
        JSONObject json = new JSONObject(message);
        if (json.has("asks") && json.has("bids")) {
            // Пример обработки данных
            double buyPrice = json.getJSONArray("bids").getJSONObject(0).getDouble("p");
            double sellPrice = json.getJSONArray("asks").getJSONObject(0).getDouble("p");
            double profit = sellPrice - buyPrice;
            double spread = (profit / buyPrice) * 100;

            priceController.updateData(symbol, "MEXC", buyPrice, sellPrice, profit, spread);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
