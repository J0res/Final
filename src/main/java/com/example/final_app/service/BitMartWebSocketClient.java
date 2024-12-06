//package kg.com.api_salamat.service;
//
//import kg.com.api_salamat.controller.PriceController;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.net.URI;
//
//
//public class BitMartWebSocketClient extends WebSocketClient {
//
//    private final String symbol;
//    private final PriceController priceController;
//
//    public BitMartWebSocketClient(URI serverUri, String symbol, PriceController priceController) {
//        super(serverUri);
//        this.symbol = symbol;
//        this.priceController = priceController;
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//        System.out.println("Connected to BitMart stream for symbol: " + symbol);
//        sendUpdateRequest();
//    }
//
//    @Override
//    public void onMessage(String message) {
//        JSONObject json = new JSONObject(message);
//
//        try {
//            if (json.has("data")) {
//                handleDataUpdate(json);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleDataUpdate(JSONObject json) {
//        JSONArray dataArray = json.getJSONArray("data");
//        for (int i = 0; i < dataArray.length(); i++) {
//            JSONObject data = dataArray.getJSONObject(i);
//            if (data.has("last_price")) {
//                handleTickerUpdate(data);
//            }
//            if (data.has("asks") && data.has("bids")) {
//                handleDepthUpdate(data);
//            }
//        }
//    }
//
//    private void handleDepthUpdate(JSONObject data) {
//        JSONArray asks = data.getJSONArray("asks");
//        JSONArray bids = data.getJSONArray("bids");
//
//        double bidSum = 0.0;
//        double askSum = 0.0;
//        double bidTotalPrice = 0.0;
//        double askTotalPrice = 0.0;
//
//        for (int i = 0; i < bids.length(); i++) {
//            JSONArray bid = bids.getJSONArray(i);
//            double bidPrice = bid.getDouble(0);
//            double bidQuantity = bid.getDouble(1);
//            double bidValue = bidPrice * bidQuantity;
//
//            if (bidValue >= 1000 && bidValue <= 2000) {
//                bidSum = bidQuantity;
//                bidTotalPrice = bidValue;
//                break;
//            }
//        }
//
//        for (int i = 0; i < asks.length(); i++) {
//            JSONArray ask = asks.getJSONArray(i);
//            double askPrice = ask.getDouble(0);
//            double askQuantity = ask.getDouble(1);
//            double askValue = askPrice * askQuantity;
//
//            if (askValue >= 1000 && askValue <= 2000) {
//                askSum = askQuantity;
//                askTotalPrice = askValue;
//                break;
//            }
//        }
//
//        double averageBidPrice = bidSum > 0 ? bidTotalPrice / bidSum : 0.0;
//        double averageAskPrice = askSum > 0 ? askTotalPrice / askSum : 0.0;
//
//        priceController.updateBitMartData(
//                symbol,
//                averageBidPrice,     // Update bid/ask prices and sums
//                averageAskPrice,
//                bidTotalPrice,
//                askTotalPrice,
//                null,   // High24h - do not update
//                null,   // Low24h - do not update
//                null    // Volume24h - do not update
//        );
//    }
//
//    private void handleTickerUpdate(JSONObject data) {
//        try {
//            double high24h = data.getDouble("high_24h");
//            double low24h = data.getDouble("low_24h");
//            double volume24h = data.getDouble("quote_volume_24h");
//            double lastPriceInUSDT = data.getDouble("last_price");
//
//            double volume24hInUSDT = volume24h * lastPriceInUSDT;
//
//            // Обновляем данные с помощью контроллера
//            priceController.updateBitMartData(
//                    symbol,
//                    null,  // Не обновляем averageBidPrice
//                    null,  // Не обновляем averageAskPrice
//                    null,  // Не обновляем bidSumUSDT
//                    null,  // Не обновляем askSumUSDT
//                    high24h,
//                    low24h,
//                    volume24hInUSDT
//            );
//        } catch (Exception e) {
//            System.err.println("Error processing ticker update: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//    public void sendUpdateRequest() {
//        String subscriptionMessageTicker = "{ \"op\": \"subscribe\", \"args\": [\"spot/ticker:" + symbol + "\"] }";
//        String subscriptionMessageDepth = "{ \"op\": \"subscribe\", \"args\": [\"spot/depth5:" + symbol + "\"] }";
//    //    this.send(subscriptionMessageTicker);
//        this.send(subscriptionMessageDepth);
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        System.out.printf("Connection closed for %s with exit code %d additional info: %s%n", symbol, code, reason);
//        if (code == 1006) {
//            reconnectWithBackoff();
//        }
//    }
//
//    public void reconnectWithBackoff() {
//        int retryCount = 0;
//        int maxRetries = 5;
//        long waitTime = 1000; // 1 секунда
//
//        while (retryCount < maxRetries) {
//            try {
//                Thread.sleep(waitTime);
//                connectBlocking(); // метод для переподключения
//                break;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            retryCount++;
//            waitTime *= 2; // Экспоненциальная задержка
//        }
//    }
//
//    @Override
//    public void onError(Exception ex) {
//        System.err.println("An error occurred for " + symbol + ": " + ex.getMessage());
//    }
//}
