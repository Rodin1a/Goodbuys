package com.tu.goodsbuy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class MemberUser {

    private Long userNo;
    private String userId;
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String userPwd;


}
