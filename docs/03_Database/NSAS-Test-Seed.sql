-- Test-only, repeatable seed data for an NSAS test schema.
-- Purpose: provide synthetic shops, accounts, manicurists, services, and portfolio posts
-- for browsing and API testing. Does not create schema objects or touch production.
-- Run while connected to the intended test schema. Existing matching SEED-* records are reused.
-- All accounts use the same BCrypt test password: password
-- USER_ROLE is intentionally omitted because its live-schema status is not verified.
-- NAIL_POST_IMAGE is intentionally omitted because rows must point to real stored files.
-- Reservation and review rows are omitted because their business flows/schema are pending.

DECLARE
    v_address_id ADDRESS.ADDRESS_ID%TYPE;
    v_shop_id SHOP.SHOP_ID%TYPE;
    v_user_id USER_ACCOUNT.USER_ID%TYPE;
    v_manicurist_id MANICURIST.MANICURIST_ID%TYPE;
    v_code VARCHAR2(36);
    v_email VARCHAR2(100);
    v_shop_no PLS_INTEGER;
    v_shop_name VARCHAR2(100);
    v_city VARCHAR2(100);
    v_district VARCHAR2(100);
    v_style VARCHAR2(20);
    v_season VARCHAR2(20);
    v_color VARCHAR2(20);
    v_price NUMBER(10,2);
    v_duration NUMBER;
BEGIN
    -- 3 shops and their addresses.
    FOR i IN 1..3 LOOP
        v_code := 'SEED-ADDR-' || TO_CHAR(i, 'FM00');
        v_city := CASE i WHEN 1 THEN '臺北市' WHEN 2 THEN '新北市' ELSE '臺中市' END;
        v_district := CASE i WHEN 1 THEN '大安區' WHEN 2 THEN '板橋區' ELSE '西區' END;

        MERGE INTO ADDRESS target
        USING (SELECT v_code address_code, v_city city, v_district district FROM dual) source
        ON (target.ADDRESS_CODE = source.address_code)
        WHEN NOT MATCHED THEN
            INSERT (ADDRESS_ID, ADDRESS_CODE, CITY, DISTRICT)
            VALUES (SEQ_ADDRESS.NEXTVAL, source.address_code, source.city, source.district);

        SELECT ADDRESS_ID INTO v_address_id FROM ADDRESS WHERE ADDRESS_CODE = v_code;
        v_code := 'SEED-SHOP-' || TO_CHAR(i, 'FM00');
        v_shop_name := CASE i WHEN 1 THEN '測試指尖研究所' WHEN 2 THEN '測試柔光美甲' ELSE '測試光澤工房' END;

        MERGE INTO SHOP target
        USING (SELECT v_code shop_code, v_shop_name shop_name, v_address_id address_id,
                      '02-0000-' || TO_CHAR(1000 + i) phone,
                      '供 API 與介面測試使用的虛構店家資料。' description,
                      v_city || v_district || '測試路' || TO_CHAR(i) || '號' address_text
                 FROM dual) source
        ON (target.SHOP_CODE = source.shop_code)
        WHEN NOT MATCHED THEN
            INSERT (SHOP_ID, SHOP_CODE, SHOP_NAME, ADDRESS_ID, PHONE, DESCRIPTION,
                    CREATE_TIME, UPDATE_TIME, ADDRESS)
            VALUES (SEQ_SHOP.NEXTVAL, source.shop_code, source.shop_name, source.address_id,
                    source.phone, source.description, SYSTIMESTAMP, SYSTIMESTAMP, source.address_text);
    END LOOP;

    -- 6 manicurists, each assigned to one of the three seed shops.
    FOR i IN 1..6 LOOP
        v_code := 'SEED-MANI-' || TO_CHAR(i, 'FM00');
        v_email := 'seed.mani' || TO_CHAR(i, 'FM00') || '@example.test';
        v_shop_no := MOD(i - 1, 3) + 1;
        v_shop_name := CASE v_shop_no WHEN 1 THEN '測試指尖研究所' WHEN 2 THEN '測試柔光美甲' ELSE '測試光澤工房' END;

        MERGE INTO USER_ACCOUNT target
        USING (SELECT 'SEED-USER-MANI-' || TO_CHAR(i, 'FM00') user_code,
                      v_email email,
                      '測試美甲師' || TO_CHAR(i, 'FM00') user_name
                 FROM dual) source
        ON (target.EMAIL = source.email)
        WHEN NOT MATCHED THEN
            INSERT (USER_ID, USER_CODE, USER_NAME, PASSWORD, EMAIL, PHONE,
                    CREATE_TIME, UPDATE_TIME, VERIFIED)
            VALUES (SEQ_USER_ACCOUNT.NEXTVAL, source.user_code, source.user_name,
                    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
                    source.email, '091100' || TO_CHAR(i, 'FM0000'),
                    SYSTIMESTAMP, SYSTIMESTAMP, '1');

        SELECT USER_ID INTO v_user_id FROM USER_ACCOUNT WHERE EMAIL = v_email;
        SELECT SHOP_ID INTO v_shop_id FROM SHOP WHERE SHOP_CODE = 'SEED-SHOP-' || TO_CHAR(v_shop_no, 'FM00');

        MERGE INTO MANICURIST target
        USING (SELECT v_code manicurist_code, v_user_id user_id, v_shop_id shop_id,
                      '測試用美甲師介紹 ' || TO_CHAR(i, 'FM00') intro,
                      '測試美甲師' || TO_CHAR(i, 'FM00') display_name
                 FROM dual) source
        ON (target.MANICURIST_CODE = source.manicurist_code)
        WHEN NOT MATCHED THEN
            INSERT (MANICURIST_ID, MANICURIST_CODE, SHOP_ID, USER_ID, INTRO,
                    CREATE_TIME, UPDATE_TIME, STATUS, DISPLAY_NAME, ROLE)
            VALUES (SEQ_MANICURIST.NEXTVAL, source.manicurist_code, source.shop_id,
                    source.user_id, source.intro, SYSTIMESTAMP, SYSTIMESTAMP,
                    'ACTIVE', source.display_name, CASE WHEN i IN (1, 3, 5) THEN 'OWNER' ELSE 'STAFF' END);
    END LOOP;

    -- 24 synthetic member accounts (4 per manicurist), verified for login testing.
    FOR i IN 1..24 LOOP
        v_email := 'seed.member' || TO_CHAR(i, 'FM00') || '@example.test';
        MERGE INTO USER_ACCOUNT target
        USING (SELECT 'SEED-USER-MEMBER-' || TO_CHAR(i, 'FM00') user_code,
                      v_email email,
                      '測試會員' || TO_CHAR(i, 'FM00') user_name
                 FROM dual) source
        ON (target.EMAIL = source.email)
        WHEN NOT MATCHED THEN
            INSERT (USER_ID, USER_CODE, USER_NAME, PASSWORD, EMAIL, PHONE,
                    CREATE_TIME, UPDATE_TIME, VERIFIED)
            VALUES (SEQ_USER_ACCOUNT.NEXTVAL, source.user_code, source.user_name,
                    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
                    source.email, '092200' || TO_CHAR(i, 'FM0000'),
                    SYSTIMESTAMP, SYSTIMESTAMP, '1');
    END LOOP;

    -- 24 services and 36 enabled portfolio posts (no image metadata without real files).
    FOR i IN 1..6 LOOP
        v_code := 'SEED-MANI-' || TO_CHAR(i, 'FM00');
        SELECT MANICURIST_ID INTO v_manicurist_id FROM MANICURIST WHERE MANICURIST_CODE = v_code;

        FOR j IN 1..4 LOOP
            v_code := 'SEED-SERVICE-' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(j, 'FM00');
            v_price := CASE j WHEN 1 THEN 800 WHEN 2 THEN 1200 WHEN 3 THEN 1600 ELSE 2200 END;
            v_duration := CASE j WHEN 1 THEN 60 WHEN 2 THEN 90 WHEN 3 THEN 120 ELSE 150 END;
            MERGE INTO SERVICE_ITEM target
            USING (SELECT v_code service_code, v_manicurist_id manicurist_id,
                          CASE j WHEN 1 THEN '單色凝膠' WHEN 2 THEN '簡約造型' WHEN 3 THEN '精緻設計' ELSE '延甲設計' END service_name,
                          v_price price, v_duration duration_min,
                          '僅供測試的合成服務項目。' description FROM dual) source
            ON (target.SERVICE_CODE = source.service_code)
            WHEN NOT MATCHED THEN
                INSERT (SERVICE_ID, SERVICE_CODE, MANICURIST_ID, SERVICE_NAME, PRICE,
                        DURATION_MIN, DESCRIPTION, IS_ACTIVE, CREATE_TIME, UPDATE_TIME)
                VALUES (SEQ_SERVICE_ITEM.NEXTVAL, source.service_code, source.manicurist_id,
                        source.service_name, source.price, source.duration_min, source.description,
                        1, SYSTIMESTAMP, SYSTIMESTAMP);
        END LOOP;

        FOR j IN 1..6 LOOP
            v_style := CASE MOD(i + j, 6) WHEN 0 THEN 'KOREAN' WHEN 1 THEN 'JAPANESE'
                        WHEN 2 THEN 'FRENCH' WHEN 3 THEN 'MINIMAL' WHEN 4 THEN 'CUTE' ELSE 'ELEGANT' END;
            v_season := CASE MOD(j, 4) WHEN 0 THEN 'SPRING' WHEN 1 THEN 'SUMMER'
                          WHEN 2 THEN 'AUTUMN' ELSE 'WINTER' END;
            v_color := CASE MOD(i + j, 6) WHEN 0 THEN 'PINK' WHEN 1 THEN 'NUDE'
                         WHEN 2 THEN 'WHITE' WHEN 3 THEN 'BLUE' WHEN 4 THEN 'RED' ELSE 'MULTI' END;
            v_price := 900 + (j * 250);

            MERGE INTO NAIL_POST target
            USING (SELECT v_manicurist_id manicurist_id,
                          v_price price, v_style style_code, v_season season_code, v_color color_code,
                          '合成作品資料 ' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(j, 'FM00') || '，供搜尋與明細 API 測試。' description
                     FROM dual) source
            ON (target.DESCRIPTION = source.description)
            WHEN NOT MATCHED THEN
                INSERT (POST_ID, MANICURIST_ID, PRICE, DESCRIPTION, STYLE_CODE, SEASON_CODE,
                        MAIN_COLOR_CODE, ENABLED, CREATE_TIME, UPDATE_TIME)
                VALUES (SEQ_NAIL_POST.NEXTVAL, source.manicurist_id, source.price, source.description,
                        source.style_code, source.season_code, source.color_code, 1, SYSTIMESTAMP, SYSTIMESTAMP);
        END LOOP;
    END LOOP;

    COMMIT;
END;


