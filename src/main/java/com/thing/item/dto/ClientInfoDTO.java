package com.thing.item.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ClientInfoDTO {

    private Integer clientIndex;
    private String clientId;
    private String email;
    private String phoneNumber;
    private Date birthdate;
    private String clientName;
    private String nickname;
    private String clientPhoto;

}
