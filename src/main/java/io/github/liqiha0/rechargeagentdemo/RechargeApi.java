package io.github.liqiha0.rechargeagentdemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;


public class RechargeApi {
    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final String API_HOST = "https://api.recharge.bitransformer.com";
    private static final String KEY = "key"; // TODO: replace it
    private static final String SECRET = "secret"; // TODO: replace it
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 充值
     *
     * @param outTradeNo  代理流水号
     * @param phoneNumber 手机号
     * @param productCode 产品代码
     * @param notifyUrl   通知URL
     */
    public static String recharge(String outTradeNo, String phoneNumber, String productCode, String notifyUrl) throws IOException, URISyntaxException {
        HttpHost httpHost = HttpHost.create(API_HOST);
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("outTradeNo", outTradeNo);
        objectNode.put("phoneNumber", phoneNumber);
        objectNode.put("productCode", productCode);
        if (notifyUrl != null) {
            objectNode.put("notifyUrl", notifyUrl);
        }
        ClassicHttpRequest request = ClassicRequestBuilder.post()
                .setPath("/api/recharge")
                .setEntity(objectMapper.writeValueAsString(objectNode), ContentType.APPLICATION_JSON)
                .setHeader(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                .build();
        try (ClassicHttpResponse response = httpClient.execute(httpHost, request)) {
            JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
            return jsonNode.get("tradeNo").asText();
        }
    }

    public static void getOrder(String orderId) throws IOException, URISyntaxException {
        HttpHost httpHost = HttpHost.create(API_HOST);
        ClassicHttpRequest request = ClassicRequestBuilder.get()
                .setPath("/api/order")
                .addParameter("tradeNo", orderId)
                .setHeader(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                .build();
        try (ClassicHttpResponse response = httpClient.execute(httpHost, request)) {
            System.out.println(response.getEntity().getContent());
        }
    }

    private static String getAuthorizationHeader() {
        Base64.Encoder encoder = Base64.getEncoder();
        return "Basic " + encoder.encodeToString(String.format("%s:%s", KEY, SECRET).getBytes());
    }
}
