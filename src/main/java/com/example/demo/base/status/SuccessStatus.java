package com.example.demo.base.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.example.demo.base.code.BaseCode;
import com.example.demo.base.code.ReasonDTO;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 0. Yoon - 공통 성공
    _OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // 1. Yoon - 회원 관련 성공
    USER_SIGNUP_SUCCESS(HttpStatus.OK, "USER_2001", "회원가입이 성공적으로 완료되었습니다."),
    USER_WITHDRAWN_SUCCESS(HttpStatus.OK, "USER_2002", "성공적으로 탈퇴하였습니다. 일정 시간 내로 재로그인 하지 않을시, 모든 정보가 삭제됩니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "USER_2003", "로그인에 성공하였습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2004", "성공적으로 로그아웃하였습니다."),

    // 2. Yoon - 토큰 관련 성공
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "TOKEN_2001", "리프레시 토큰을 통한 토큰 갱신에 성공하였습니다."),

    // 3. Yoon - 관리자 모드 관련 성공
    ADMIN_TURN_ADMIN(HttpStatus.OK, "ADMIN_2001", "관리자 모드로 변환에 성공하였습니다."),
    ADMIN_TURN_USER(HttpStatus.OK, "ADMIN_2002", "사용자 모드로 변환에 성공하였습니다."),
    ADMIN_GET_ALL_USER(HttpStatus.OK, "ADMIN_2003", "모든 사용자를 조회하였습니다."),

    // 4. HyungJin - 래플 관련 성공
    RAFFLE_UPLOAD_SUCCESS(HttpStatus.OK, "RAFFLE_2001", "래플 업로드에 성공하였습니다."),
    RAFFLE_FETCH_SUCCESS(HttpStatus.OK, "RAFFLE_2002", "래플 조회에 성공하였습니다."),


    // 5. HyungJin - 사진 관련 성공
    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "IMAGE_2001", "사진 업로드에 성공하였습니다."),

    // 6. Yoon - 결제 관련 성공
    PAYMENT_READY_SUCCESS(HttpStatus.OK, "PAYMENT_2001", "결제 준비가 성공적으로 완료되었습니다."),
    PAYMENT_APPROVE_SUCCESS(HttpStatus.OK, "PAYMENT_2002", "결제 승인이 성공적으로 완료되었습니다."),
    PAYMENT_CANCEL_SUCCESS(HttpStatus.OK, "PAYMENT_2003", "결제가 성공적으로 취소되었습니다."),
    PAYMENT_HISTORY_SUCCESS(HttpStatus.OK, "PAYMENT_2004", "내역을 성공적으로 조회하였습니다."),

    // 7. Yoon - 유저 결제 관련 성공
    USER_PAYMENT_GET_TICKET(HttpStatus.OK, "USER_PAYMENT_2001", "유저의 현재 티켓 갯수가 성공적으로 조회되었습니다."),
    USER_PAYMENT_UPDATE_BANK_INFO(HttpStatus.OK, "USER_PAYMENT_2002", "유저의 은행 정보가 성공적으로 업데이트되었습니다."),
    USER_PAYMENT_GET_BANK_INFO(HttpStatus.OK, "USER_PAYMENT_2003", "유저의 은행 정보가 성공적으로 조회되었습니다."),

    // 8. Yoon - 환전 관련 성공
    EXCHANGE_HISTORY_SUCCESS(HttpStatus.OK, "EXCHANGE_2001", "성공적으로 환전되었습니다."),

    // 9. Yoon - 팔로우 관련 성공
    FOLLOW_SUCCESS(HttpStatus.OK, "FOLLOW_2001", "상점을 성공적으로 팔로우했습니다."),
    FOLLOW_UNFOLLOW_SUCCESS(HttpStatus.OK, "FOLLOW_2002", "상점을 성공적으로 언팔로우했습니다."),

    // 10. Yoon - 거래 관련 성공
    TRADE_TICKET_SUCCESS(HttpStatus.OK, "TRADE_2001", "티켓 거래가 완료되었습니다.");


    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return getReason();
    }
}