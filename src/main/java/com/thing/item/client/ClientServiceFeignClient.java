package com.thing.item.client;

import com.thing.item.dto.APIResponseDTO;
import com.thing.item.dto.ClientInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service")
public interface ClientServiceFeignClient {

    @GetMapping(value = "/clients/{client-idx}", produces = "application/json")
    APIResponseDTO<ClientInfoDTO> getClient(@PathVariable("client-idx") Integer clientIdx);

}
