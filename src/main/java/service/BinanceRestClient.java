//package kg.com.api_salamat.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class BinanceRestClient {
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public List<String> getSymbols() {
//        String url = "https://api.binance.com/api/v3/exchangeInfo";
//        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//        List<Map<String, String>> symbols = (List<Map<String, String>>) response.get("symbols");
//        List<String> symbolNames = new ArrayList<>();
//        for (Map<String, String> symbol : symbols) {
//            symbolNames.add(symbol.get("symbol"));
//        }
//        return symbolNames;
//    }
//}