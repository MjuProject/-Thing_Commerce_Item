package com.thing.item.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "kakao-map", url = "")
public interface KakaoMapClient {

}
