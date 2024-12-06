package service;

import kg.com.api_salamat.controller.PriceController;
import kg.com.api_salamat.service.BinanceWebSocketClient;
import kg.com.api_salamat.service.MEXCWebSocketClient;
import kg.com.api_salamat.service.OKXWebSocketClient;
import kg.com.api_salamat.service.PoloniexWebSocketClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
@Component
public class WebSocketConnectionRunner implements CommandLineRunner {

    private final PriceController priceController;

    public WebSocketConnectionRunner(PriceController priceController) {
        this.priceController = priceController;
    }

    @Override
    public void run(String... args) {
        List<String> symbols = List.of("BTC-USDT", "ETH-USDT");

        symbols.forEach(symbol -> {
            try {
                // Подключение к MEXC
                new MEXCWebSocketClient(new URI("wss://wbs.mexc.com/ws"), symbol, priceController).connectBlocking();

                // Подключение к OKX
                new OKXWebSocketClient(new URI("wss://ws.okx.com:8443/ws/v5/public"), symbol, priceController).connectBlocking();

                // Подключение к Poloniex
                new PoloniexWebSocketClient(new URI("wss://ws.poloniex.com/ws/public"), symbol, priceController).connectBlocking();

                // Подключение к Binance
                new BinanceWebSocketClient(new URI("wss://stream.binance.com:9443/ws"), symbol, priceController).connectBlocking();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
