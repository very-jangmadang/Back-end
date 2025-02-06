package com.example.demo.service.handler;

import org.springframework.stereotype.Service;

import java.util.Random;

// 랜덤 닉네임 생성
@Service
public class NicknameGenerator {

    private static final String[] ADJECTIVES = {
            "멋진", "화려한", "귀여운", "상큼한", "신비한", "용감한", "행복한", "푸른", "빛나는", "활발한"
    };
    private static final String[] NOUNS = {
            "고양이", "나무", "별", "물결", "토끼", "호랑이", "햇살", "구름", "바람", "사자"
    };

    public static String generateNickname() {
        Random random = new Random();

        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];

        int randomNumber = 1000 + random.nextInt(9000);

        return adjective + " " + noun + randomNumber;
    }
}