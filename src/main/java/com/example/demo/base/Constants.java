package com.example.demo.base;

public class Constants {
  
    // 최대 주소 갯수
    public static final int MAX_ADDRESS_COUNT = 6;

    // 배송지 입력 기한 (시간 단위)
    public static final int ADDRESS_DEADLINE = 72;

    // 운송장 입력 기한 (시간 단위)
    public static final int SHIPPING_DEADLINE = 96;

    // 배송지/운송장 입력 기한 연장 (시간 단위)
    public static final int WAIT = 24;

    // 추첨 완료된 래플 개최자 리디렉션 url
    public static final String DELIVERY_OWNER_URL = "/api/permit/delivery/%d/owner";

    // 미추첨된 래플 개최자 리디렉션 url
    public static final String RAFFLE_OWNER_URL = "/api/permit/raffles/%d/result";

    // 최대 조회 닉네임 개수
    public static final int MAX_NICKNAMES = 50;

}
