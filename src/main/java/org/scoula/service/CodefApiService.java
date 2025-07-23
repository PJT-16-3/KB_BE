package org.scoula.service;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.ChungyakAccountDTO;
import org.scoula.mapper.AccountMapper;
import org.scoula.util.RsaEncryptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class CodefApiService {

    private final RsaEncryptionUtil rsaEncryptor;
    private final AccountMapper accountMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${codef.client.id}")
    private String clientId;

    @Value("${codef.client.secret}")
    private String clientSecret;

    @Value("${codef.public.key}")
    private String publicKey;

    public List<ChungyakAccountDTO> autoConnectAndFetchChungyakAccounts(
            String id, String password, String organization, String bankName, int userIdx) throws Exception {

        String accessToken = getAccessToken();
        String encryptedPassword = rsaEncryptor.encryptPassword(publicKey, password);
        String connectedId = createConnectedId(accessToken, id, encryptedPassword, organization);

        if (connectedId == null) return Collections.emptyList();

        String accountListJson = requestAccountList(accessToken, connectedId, organization);
        List<ChungyakAccountDTO> accounts = filterChungyakAccounts(accountListJson, bankName);
        saveChungyakAccounts(accounts, userIdx, false);

        return accounts;
    }

    private void saveChungyakAccounts(List<ChungyakAccountDTO> accounts, int userIdx, boolean isPayment) {
        for (ChungyakAccountDTO dto : accounts) {
            accountMapper.insertChungyakAccount(dto, userIdx, isPayment);
        }
    }

    public List<ChungyakAccountDTO> getChungyakAccountsByUserIdx(int userIdx) {
        return accountMapper.findAccountsByUserIdx(userIdx);
    }

    private String getAccessToken() {
        String url = "https://oauth.codef.io/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String basicAuth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + basicAuth);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String responseBody = response.getBody();
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        return json.get("access_token").getAsString();
    }

    private String createConnectedId(String accessToken, String id, String encryptedPassword, String organization) throws Exception {
        String url = "https://development.codef.io/v1/account/create";

        JsonObject account = new JsonObject();
        account.addProperty("countryCode", "KR");
        account.addProperty("businessType", "BK");
        account.addProperty("clientType", "P");
        account.addProperty("organization", organization);
        account.addProperty("loginType", "1");
        account.addProperty("id", id);
        account.addProperty("password", encryptedPassword);

        JsonObject body = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add(account);
        body.add("accountList", arr);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String encoded = response.getBody();
        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());

        log.info("ConnectedId 응답 (디코딩): {}", decoded);

        JsonObject json = JsonParser.parseString(decoded).getAsJsonObject();
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : null;

        if (data != null && data.has("connectedId")) {
            return data.get("connectedId").getAsString();
        }

        return null;
    }

    private String requestAccountList(String accessToken, String connectedId, String organization) throws Exception {
        String url = "https://development.codef.io/v1/kr/bank/p/account/account-list";

        JsonObject body = new JsonObject();
        body.addProperty("organization", organization);
        body.addProperty("connectedId", connectedId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String encoded = response.getBody();
        String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());

        log.info("Account List 응답 (디코딩): {}", decoded);
        return decoded;
    }

    private List<ChungyakAccountDTO> filterChungyakAccounts(String json, String bankName) {
        List<ChungyakAccountDTO> resultList = new ArrayList<>();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (root.has("data")) {
            JsonObject data = root.getAsJsonObject("data");
            if (data.has("resDepositTrust")) {
                for (JsonElement elem : data.getAsJsonArray("resDepositTrust")) {
                    JsonObject account = elem.getAsJsonObject();
                    String accountName = account.has("resAccountName") ? account.get("resAccountName").getAsString() : "";

                    if (accountName.contains("청약")) {
                        ChungyakAccountDTO dto = new ChungyakAccountDTO(
                                getSafe(account, "resAccountDisplay"),
                                getSafe(account, "resAccountBalance"),
                                getSafe(account, "resAccountStartDate"),
                                getSafe(account, "resAccount"),
                                getSafe(account, "resAccountName"),
                                bankName
                        );
                        resultList.add(dto);
                    }
                }
            }
        }

        return resultList;
    }

    private String getSafe(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : "";
    }
}
