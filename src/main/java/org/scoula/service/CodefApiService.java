package org.scoula.service;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.ChungyakAccountDTO;
import org.scoula.exception.NoAccountException;
import org.scoula.mapper.AccountMapper;
import org.scoula.util.RsaEncryptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    /**
     * 계좌 자동 연결 후 청약 계좌 정보 및 거래내역 조회
     */
    public ChungyakAccountDTO autoConnectAndFetchChungyakAccount(
            String id, String password, String organization, String bankName, int userIdx) throws Exception {

        String accessToken = getAccessToken();
        String encryptedPassword = rsaEncryptor.encryptPassword(publicKey, password);

        String connectedId = createConnectedId(accessToken, id, encryptedPassword, organization);
        if (connectedId == null) {
            throw new NoAccountException("ConnectedId 생성 실패");
        }

        // 계좌 목록 조회
        String accountListJson = requestAccountList(accessToken, connectedId, organization);
        List<ChungyakAccountDTO> accounts = filterChungyakAccounts(accountListJson, bankName);

        if (accounts == null || accounts.isEmpty()) {
            throw new NoAccountException("청약 계좌를 찾을 수 없습니다.");
        }

        // 단일 계좌만 선택
        ChungyakAccountDTO account = accounts.get(0);

        // 거래내역에서 필요한 값만 추가
        addTransactionDetails(accessToken, connectedId, organization, account);

        // DB 저장 (isPayment 반영)
        boolean isPayment = account.getResAccountTrDate() != null;
        accountMapper.insertChungyakAccount(account, userIdx, isPayment);

        return account;
    }

    /**
     * 거래내역에서만 얻을 수 있는 값 채우기
     */
    private void addTransactionDetails(String accessToken, String connectedId,
                                       String organization, ChungyakAccountDTO dto) throws Exception {

        String url = "https://development.codef.io/v1/kr/bank/p/installment-savings/transaction-list";

        LocalDate today = LocalDate.now();
        String endDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String startDate = today.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        JsonObject body = new JsonObject();
        body.addProperty("connectedId", connectedId);
        body.addProperty("organization", organization);
        body.addProperty("account", dto.getResAccount());
        body.addProperty("startDate", startDate);
        body.addProperty("endDate", endDate);
        body.addProperty("orderBy", "0");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String decoded = URLDecoder.decode(response.getBody(), StandardCharsets.UTF_8.name());
        log.info("거래내역 응답 (디코딩): {}", decoded);

        JsonObject root = JsonParser.parseString(decoded).getAsJsonObject();
        JsonObject data = root.getAsJsonObject("data");

        // 거래내역 전용 값
        dto.setResFinalRoundNo(getSafe(data, "resFinalRoundNo"));

        if (data.has("resTrHistoryList")) {
            JsonArray history = data.getAsJsonArray("resTrHistoryList");
            if (!history.isEmpty()) {
                JsonObject lastTran = history.get(0).getAsJsonObject();
                dto.setResAccountTrDate(getSafe(lastTran, "resAccountTrDate"));
            } else {
                dto.setResAccountTrDate(null); // 거래내역 없음
            }
        }
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
                        ChungyakAccountDTO dto = new ChungyakAccountDTO();
                        dto.setAccountDisplay(getSafe(account, "resAccountDisplay"));
                        dto.setAccountBalance(getSafe(account, "resAccountBalance"));
                        dto.setAccountStartDate(getSafe(account, "resAccountStartDate"));
                        dto.setResAccount(getSafe(account, "resAccount"));
                        dto.setResAccountName(getSafe(account, "resAccountName"));
                        dto.setBankName(bankName);
                        resultList.add(dto);
                    }
                }
            }
        }

        return resultList;
    }

    public ChungyakAccountDTO getAccountByUserIdx(int userIdx) {
        ChungyakAccountDTO account = accountMapper.findAccountByUserIdx(userIdx);
        if (account == null) {
            throw new NoAccountException("해당 사용자에게 등록된 청약 계좌가 없습니다.");
        }
        return account;
    }

    private String getSafe(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : "";
    }

    public boolean deleteAccountByUserIdx(int userIdx) {
        try {
            ChungyakAccountDTO account = accountMapper.findAccountByUserIdx(userIdx);
            if (account == null) {
                return false;
            }
            accountMapper.deleteAccountByUserIdx(userIdx);
            return true;
        } catch (Exception e) {
            log.error("계좌 삭제 중 오류", e);
            throw new RuntimeException("계좌 삭제 실패", e);
        }
    }
}
