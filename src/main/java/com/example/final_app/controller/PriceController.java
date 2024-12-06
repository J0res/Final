package com.example.final_app.controller;

import com.example.final_app.service.TelegramService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;
//dawdawd
@RestController
public class PriceController {

    private final ConcurrentHashMap<String, PriceData> priceDataMap = new ConcurrentHashMap<>();
    private final TelegramService telegramService;

    public PriceController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public void updateData(String symbol, String exchange, double buyPrice, double sellPrice, double profit, double spread) {
        PriceData data = new PriceData(symbol, exchange, buyPrice, sellPrice, profit, spread);
        priceDataMap.put(symbol, data);

        // Формируем сообщение и отправляем в Telegram
        String message = String.format(
                "💸 *%s -> Binance | %s*\n\n" +
                        "📈 *Покупка:*\nОбъем: %.2f USDT\nЦена: %.6f\n\n" +
                        "📉 *Продажа:*\nОбъем: %.2f FUN\nЦена: %.6f\n\n" +
                        "💰 *Прибыль:* %.2f USDT\n📊 *Спред:* %.2f%%\n",
                exchange, symbol, buyPrice, buyPrice / 0.0001, sellPrice, sellPrice / 0.0001, profit, spread
        );

        telegramService.sendMessage(message);
    }

    @GetMapping("/average-prices")
    public ConcurrentHashMap<String, PriceData> getAveragePrices() {
        return priceDataMap;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceData {
        private String symbol;
        private String exchange;
        private double buyPrice;
        private double sellPrice;
        private double profit;
        private double spread;
    }
}
