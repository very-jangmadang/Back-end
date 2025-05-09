package com.example.demo.base;

public class Constants {

    // 최대 주소 갯수
    public static final int MAX_ADDRESS_COUNT = 6;

    // 배송지 입력 기한 (시간 단위)
    //public static final int ADDRESS_DEADLINE = 72;
    //테스트용
    public static final int ADDRESS_DEADLINE = 1;

    // 운송장 입력 기한 (시간 단위)
    //public static final int SHIPPING_DEADLINE = 96;
    // 테스트용
    public static final int SHIPPING_DEADLINE = 1;


    // 배송지/운송장 입력 기한 연장 (시간 단위)
    //public static final int EXTENSION_HOURS = 24;
    //테스트용
    public static final int EXTENSION_HOURS = 1;

    // 선택 가능 기한 (시간 단위)
    public static final int CHOICE_PERIOD = 24;

    // 수령 완료 기한(7일) (시간 단위)
    public static final int COMPLETE = 168;

    // 추첨 완료된 래플 개최자 리디렉션 url
    public static final String DELIVERY_OWNER_URL = "/api/member/delivery/%d/owner";

    // 미추첨된 래플 개최자 리디렉션 url
    public static final String RAFFLE_OWNER_URL = "/api/member/raffles/%d/result";

    // 당첨자 배송 정보 확인 url
    public static final String DELIVERY_WINNER_URL = "/api/member/delivery/%d/winner";

    // 래플 url
    public static final String RAFFLE_URL = "https://www.jangmadang.site/raffles/%d";

    // 최대 조회 닉네임 개수
    public static final int MAX_NICKNAMES = 50;

    // 수수료 비율
    public static final double FEE_RATE = 0.07;

}