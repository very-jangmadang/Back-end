INSERT INTO category (name) VALUES ('WOMEN_CLOTHING');         -- 여성의류
INSERT INTO category (name) VALUES ('MEN_CLOTHING');           -- 남성의류
INSERT INTO category (name) VALUES ('SHOES');                  -- 신발
INSERT INTO category (name) VALUES ('ACCESSORIES');            -- 악세서리
INSERT INTO category (name) VALUES ('DIGITAL');                -- 디지털
INSERT INTO category (name) VALUES ('APPLIANCES');             -- 가전제품
INSERT INTO category (name) VALUES ('SPORTS_LEISURE');         -- 스포츠/레저
INSERT INTO category (name) VALUES ('VEHICLES');               -- 차량/오토바이
INSERT INTO category (name) VALUES ('GOODS');                  -- 굿즈
INSERT INTO category (name) VALUES ('ART_RARE_COLLECTIBLES');  -- 예술/희귀/수집품
INSERT INTO category (name) VALUES ('MUSIC_INSTRUMENTS');      -- 음반/악기
INSERT INTO category (name) VALUES ('BOOKS_TICKETS_STATIONERY'); -- 도서/티켓/문구
INSERT INTO category (name) VALUES ('BEAUTY');                 -- 뷰티
INSERT INTO category (name) VALUES ('INTERIOR');               -- 인테리어
INSERT INTO category (name) VALUES ('HOUSEHOLD_ITEMS');        -- 생활용품
INSERT INTO category (name) VALUES ('TOOLS_INDUSTRIAL');       -- 공구/산업용품
INSERT INTO category (name) VALUES ('FOOD');                   -- 식품
INSERT INTO category (name) VALUES ('BABY');                   -- 육아
INSERT INTO category (name) VALUES ('PETS');                   -- 반려동물
INSERT INTO category (name) VALUES ('OTHERS');                 -- 기타
INSERT INTO category (name) VALUES ('TALENTS');                -- 재능

INSERT INTO user (email, nickname, ticket_num, provider, role, address, score, withdraw_time)
VALUES ('testuser@example.com', 'testuser', 100, 'kakao', 'USER', '123 Test Street, Test City', 4.5, NULL);