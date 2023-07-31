package uz.mc.apptender.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.payload.AuthLotDTO;
import uz.mc.apptender.payload.CreateTenderDTO;
import uz.mc.apptender.payload.Error;

import java.net.URI;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class Utils {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.rest-template.username}")
    private String username;
    @Value("${app.rest-template.password}")
    private String password;
    @Value("${app.rest-template.url-get-role}")
    private String apiUrlToRole;

    public AuthLotDTO sendToGetRoleOfLot(Long inn, Long lotId) {
        URI uri = UriComponentsBuilder.fromUriString(apiUrlToRole).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);

        //LOT_ID AND INN SEND TO MUHAMMADALI'S SERVER WITH BASIC AUTH
        log.info("Send to get role and user_id from Muhammadali's server with inn: " + inn + " lod_id: " + lotId);
        HttpEntity<CreateTenderDTO> requestEntity = new HttpEntity<>(new CreateTenderDTO(lotId, inn), headers);

        JsonNode jsonNode;
        try {
            jsonNode = restTemplate.postForObject(uri, requestEntity, JsonNode.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.fillInStackTrace();
            log.error(e.getMessage() + "  inn: " + inn + ", lot_id: " + lotId);

            String responseBody = e.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            Error error;

            try {
                error = objectMapper.readValue(responseBody, Error.class);
            } catch (JsonProcessingException ex) {
                throw RestException.restThrow(responseBody);
            }

            throw RestException.restThrow(error.toString(), HttpStatus.resolve(error.getCode()));
        }

        AuthLotDTO authLotDTO = new AuthLotDTO();

        if (Objects.nonNull(jsonNode)) {
            String role = jsonNode.get("result").get("data").get("role").asText();
            int status = jsonNode.get("result").get("data").get("lot_status").asInt();
            boolean customerCanChange = jsonNode.get("result").get("data").get("customer_can_change").asBoolean();
            boolean offerorCanChange = jsonNode.get("result").get("data").get("offeror_can_change").asBoolean();
            long userId = jsonNode.get("result").get("data").get("user_id").asLong();

            authLotDTO.setRole(role);
            authLotDTO.setUserId(userId);
            authLotDTO.setStatus(status);
            authLotDTO.setOfferorCanChange(offerorCanChange);
            authLotDTO.setCustomerCanChange(customerCanChange);
            authLotDTO.setLotId(lotId);
        }

        return authLotDTO;
    }
}
