package com.example.demo.base.status;

import com.example.demo.base.Constants;
import com.example.demo.base.code.BaseErrorCode;
import com.example.demo.base.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 0. Yoon - 공통 에러
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러가 발생했습니다. 관리자에게 문의하세요."),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_4001", "잘못된 요청입니다."),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_4002", "인증이 필요합니다."),
    COMMON_WRONG_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_4003", "잘못된 파라미터 값 입니다."),

    // 1. Yoon - 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_4001", "해당 유저를 찾을 수 없습니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "USER_4002", "이미 탈퇴한 유저입니다."),
    USER_ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "USER_4003", "이미 로그아웃한 유저입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_4004", "이미 존재하는 사용자입니다."),
    USER_INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "USER_4005", "로그인 경로가 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER_4006", "비밀번호 설정 규칙에 맞지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_4007", "비밀번호가 잘못되었습니다."),
    USER_NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_4008", "이미 존재하는 유저 닉네임입니다."),
    USER_WITHOUT_SESSION(HttpStatus.BAD_REQUEST, "USER_4009", "요청시 세션이 없습니다."),
    USER_WITHOUT_OAUTHEMAIL(HttpStatus.BAD_REQUEST, "USER_4010", "세션은 존재하지만 oauthEmail이 세션에 없습니다."),
    USER_NOT_BUSINESS(HttpStatus.BAD_REQUEST, "USER_4011", "사업자가 아닙니다."),

    // 2. Yoon - 토큰 관련 에러
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "TOKEN_4001", "토큰이 누락되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_4002", "해당 토큰을 찾을 수 없습니다."),
    TOKEN_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4003", "만료되거나 잘못된 엑세스 토큰입니다."),
    TOKEN_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_4004", "만료되거나 잘못된 리프레시 토큰입니다."),

    // 3. Yoon - 관리자 모드 관련 에러
    ADMIN_UNAUTHORIZED_ACCESS(HttpStatus.BAD_REQUEST, "ADMIN_4001", "관리자만 접근 가능한 경로입니다."),

    // 4. Yoon - 소셜 로그인 관련 에러
    OAUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "OAUTH_4001", "OAuth 로그인에 실패했습니다."),
    OAUTH_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH_4002", "OAuth 로그인 처리에 실패했습니다."),

    // Hyungjin - 로그인 관련 에러
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "LOGIN_4001", "인증되지 않은 사용자 접근입니다."),

    // 5. Hyungjin - 래플 관련 에러
    RAFFLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "RAFFLE_4001", "해당 래플을 찾을 수 없습니다."),
    RAFFLE_CANCEL_FAIL(HttpStatus.BAD_REQUEST, "RAFFLE_4002", "종료 가능한 래플이 아닙니다."),
    RAFFLE_CANT_DELETE(HttpStatus.BAD_REQUEST, "RAFFLE_4003", "래플 종료 후 삭제 가능합니다"),
    RAFFLE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "RAFFLE_4004", "이미 삭제된 래플입니다"),
    RAFFLE_ONLY_BUSINESS(HttpStatus.BAD_REQUEST, "RAFFLE_4005", "사업자가 아니면 래플 업로드를 할 수 없습니다."),

    // 6. Hyungjin - 카테고리 관련 에러
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "CATEGORY_4001", "해당 카테고리를 찾을 수 없습니다."),

    // 7. Dohyun - 찜 관련 에러
    LIKE_ALREADY_FOUND(HttpStatus.BAD_REQUEST, "LIKE_4001", "이미 존재하는 찜입니다."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIKE_4002", "해당 찜을 찾을 수 없습니다."),

    // 8.Dohyun- 후기 관련 에러
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW_4001", "해당 리뷰를 찾을 수 없습니다."),
    RAFFLE_USER_MISMATCH(HttpStatus.BAD_REQUEST, "REVIEW_4002","주최자와 래플이 일치하지 않습니다." ),
    NOT_WINNER(HttpStatus.BAD_REQUEST, "REVIEW_4003","당첨자가 아닙니다." ),
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "REVIEW_4004","이미 리뷰를 작성하였습니다."),
    NO_DELETE_AUTHORITY(HttpStatus.BAD_REQUEST, "REVIEW_4005","삭제 권한이 없습니다."),
    DELETED_RAFFLE(HttpStatus.BAD_REQUEST, "REVIEW_4006","삭제된 래플의 후기는 삭제할 수 없습니다."),
    CANNOT_REVIEW_FOR_DELETED_RAFFLE(HttpStatus.BAD_REQUEST, "REVIEW_4006","삭제된 래플에는 후기 작성 불가합니다."),
    // 9. Dohyun - 문의 관련 에러
    INQUIRY_NOT_FOUND(HttpStatus.BAD_REQUEST, "INQUIRY_4001", "해당 문의를 찾을 수 없습니다."),
    SCHEDULER_START_FAILED(HttpStatus.BAD_REQUEST, "SCHEDULER", "스케줄러실행불가."),
    // 10. Huiju - 응모 관련 에러
    APPLY_INSUFFICIENT_TICKET(HttpStatus.BAD_REQUEST, "APPLY_4001", "보유한 티켓 수가 부족합니다."),
    APPLY_RAFFLE_UNAVAILABLE(HttpStatus.BAD_REQUEST, "APPLY_4002", "응모가 불가능한 래플입니다."),
    APPLY_SELF_RAFFLE(HttpStatus.BAD_REQUEST, "APPLY_4003", "본인이 개최한 래플에는 응모할 수 없습니다."),
    APPLY_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "APPLY_4004", "이미 응모한 래플입니다."),

    // 11. Hyungjin - 이미지 관련 에러
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_4001", "이미지 업로드에 실패했습니다."),

    // 12. Huiju - Job 관련 에러
    JOB_EXECUTION_FAILED(HttpStatus.BAD_REQUEST, "JOB_4001", "Job 실행에 실패했습니다."),
    JOB_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5001", "Job 저장에 실패했습니다."),
    JOB_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5002", "알 수 없는 Job 에러가 발생했습니다."),
    JOB_CANCEL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JOB_5003", "Job 취소에 실패했습니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "JOB_4002", "Job 설정 가능한 배송 상태가 아닙니다."),

    // 13. Huiju - 당첨자 추첨 관련 에러
    DRAW_EMPTY(HttpStatus.BAD_REQUEST, "DRAW_4001", "응모한 사용자가 없습니다."),
    DRAW_NOT_OWNER(HttpStatus.BAD_REQUEST, "DRAW_4002", "해당 래플의 개최자가 아닙니다."),
    DRAW_NOT_ENDED(HttpStatus.BAD_REQUEST, "DRAW_4003", "아직 마감되지 않았습니다."),
    DRAW_NOT_IN(HttpStatus.BAD_REQUEST, "DRAW_4004", "해당 래플의 응모자가 아닙니다."),
    DRAW_PENDING(HttpStatus.BAD_REQUEST, "DRAW_4005", "아직 추첨이 되지 않았습니다."),
    DRAW_NOT_UNFULFILLED(HttpStatus.BAD_REQUEST, "DRAW_4006", "UNFULFILLED 래플이 아닙니다."),
    DRAW_FINISHED(HttpStatus.BAD_REQUEST, "DRAW_4007", "이미 종료된 래플입니다."),
    DRAW_ALREADY_CHECKED(HttpStatus.BAD_REQUEST, "DRAW_4008", "이미 결과를 확인한 래플입니다"),
    DRAW_OWNER(HttpStatus.BAD_REQUEST, "DRAW_4009", "래플의 개최자입니다."),
    DRAW_ALREADY_REDRAW(HttpStatus.BAD_REQUEST, "DRAW_4010", "두번 이상 재추첨할 수 없습니다."),
    DRAW_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DRAW_5001", "이메일 전송에 실패하였습니다."),

    // 14. Huiju - 배송 관련 에러
    DELIVERY_NOT_WINNER(HttpStatus.BAD_REQUEST, "DELIVERY_4001", "당첨자가 아닙니다."),
    DELIVERY_NOT_FOUND(HttpStatus.BAD_REQUEST, "DELIVERY_4002", "등록된 배송 정보가 없습니다."),
    DELIVERY_NOT_OWNER(HttpStatus.BAD_REQUEST, "DELIVERY_4003", "해당 래플의 개최자가 아닙니다."),
    DELIVERY_ALREADY_EXTEND(HttpStatus.BAD_REQUEST, "DELIVERY_4004", "한번 이상 연장할 수 없습니다."),
    DELIVERY_FAIL(HttpStatus.BAD_REQUEST, "DELIVERY_4005", "요청을 처리할 수 있는 상태가 아닙니다."),
    DELIVERY_NO_ADDRESS(HttpStatus.BAD_REQUEST, "DELIVERY_4006", "등록 가능한 주소가 없습니다."),

    // 15. Huiju - 주소 관련 에러
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "ADDRESS_4001", "존재하지 않는 주소입니다."),
    ADDRESS_MISMATCH_USER(HttpStatus.FORBIDDEN, "ADDRESS_4002", "선택한 주소가 해당 사용자에게 유효한 주소가 아닙니다."),
    ADDRESS_FULL(HttpStatus.BAD_REQUEST, "ADDRESS_4003", "최대 주소 갯수(" + Constants.MAX_ADDRESS_COUNT + "개)를 초과했습니다."),
    ADDRESS_LONG_MESSAGE(HttpStatus.BAD_REQUEST, "ADDRESS_4004", "입력 가능 최대 글자수를 초과하였습니다."),
    ADDRESS_DEFAULT_LOCKED(HttpStatus.BAD_REQUEST, "ADDRESS_4005", "기본 배송지는 삭제할 수 없습니다."),
    ADDRESS_HAS_ACTIVE_DELIVERY(HttpStatus.BAD_REQUEST, "ADDRESS_4006", "배송 정보로 등록된 주소는 삭제할 수 없습니다."),

    // 18. Yoon - 결제 관련 에러
    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "PAY_4001", "PAY API 요청 실패"),
    PAYMENT_REQUEST_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY_4002", "PAY API 요청 중 오류 발생"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_4041", "결제 정보가 없습니다"),
    PAYMENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4003", "결제 정보 저장 실패"),
    PAYMENT_HISTORY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4004", "결제 내역 조회 중 오류 발생"),

    // 19. Yoon - 결제 상세 관련 에러
    EXCHANGE_HISTORY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4005", "티켓 환전 내역 조회 중 오류 발생"),
    EXCHANGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_4006", "티켓 환전 처리 중 오류 발생"),

    // 20. Yoon - 유저 결제 관련 에러
    USER_PAYMENT_INVALID_PERIOD(HttpStatus.NOT_FOUND, "USER_PAYMENT_4001", "유효하지 않은 기간을 조회하셨습니다."),
    USER_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "USER_PAYMENT_4002", "잔액이 부족합니다."),

    // 21. dohyun- 문의 관련 에러
    CANNOT_DELTETE(HttpStatus.BAD_REQUEST, "INQUIRY_4001", "삭제 권한이 없습니다."),

    // 22. ajwoong - 검색 관련 에러
    SEARCH_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH_4001", "존재하지 않는 최근 검색어 입니다."),

    // 23. dohyun - 닉네임 변경 관련 에러
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "NICKNAME_4001", "이미 존재하는 닉네임입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "NICKNAME_4002","사용할 수 없는 닉네임입니다"),

    // 24. Yoon - 팔로우 관련 에러
    FOLLOW_ALREADY(HttpStatus.BAD_REQUEST, "FOLLOW_4001", "이미 팔로우한 상점입니다."),
    FOLLOW_NOT(HttpStatus.BAD_REQUEST, "FOLLOW_4002", "팔로우 상태가 아닙니다."),
    FOLLOW_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_4003", "상점을 찾을 수 없습니다."),
    FOLLOW_SELF(HttpStatus.BAD_REQUEST, "FOLLOW_4004", "자기 자신은 팔로우 할 수 없습니다."),

    // 25. Yoon - 거래 관련 에러
    TRADE_USER_PAYMENT_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "TRADE_4001", "유효하지 않은 기간입니다."),
    TRADE_INVALID_TICKET_COUNT(HttpStatus.BAD_REQUEST, "TRADE_4002", "티켓 개수는 1 이상이어야 합니다."),
    TRADE_INSUFFICIENT_TICKETS(HttpStatus.BAD_REQUEST, "TRADE_4003", "보유한 티켓이 부족합니다."),
    TRADE_INVALID_ROLE(HttpStatus.BAD_REQUEST, "TRADE_4004", "유효하지 않은 역할입니다. '구매자' 또는 '판매자'만 가능합니다."),

    BUSINESS_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "BUSINESS_4001", "사업자 번호 입력은 필수입니다.");

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return getReason();
    }

}
