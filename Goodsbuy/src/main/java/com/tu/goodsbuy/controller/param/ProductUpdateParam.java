package com.tu.goodsbuy.controller.param;


import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@lombok.Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ProductUpdateParam {

    @jakarta.validation.constraints.NotBlank
    private String productNo;
    @jakarta.validation.constraints.NotBlank
    private String productName;

    @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력가능합니다.")
    @jakarta.validation.constraints.NotBlank
    private String productPrice;

    @jakarta.validation.constraints.NotBlank
    private String productInfo;
}
