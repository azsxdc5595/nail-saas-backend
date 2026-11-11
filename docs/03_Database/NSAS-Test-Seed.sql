-- Test-only, repeatable seed data for an NSAS test schema.
-- Purpose: provide synthetic shops, accounts, manicurists, 120 services, and 70 portfolio posts
-- for browsing and API testing. Does not create schema objects or touch production.
-- Run while connected to the intended test schema. Existing matching SEED-* records are reused.
-- Requires at least one existing ADDRESS row; existing rows are reused and this seed does not create addresses.
-- All accounts use the same BCrypt test password: ares0418
-- USER_ROLE seeding follows Account Role Assignment and requires USER_ROLE in the target test schema.
-- NAIL_POST_IMAGE rows reference unique files under D:\Nail\postImage\SEED-MANI-xx\20261110\.
-- Reservation and review rows are omitted because their business flows/schema are pending.

DECLARE
    v_address_id ADDRESS.ADDRESS_ID%TYPE;
    v_shop_id SHOP.SHOP_ID%TYPE;
    v_user_id USER_ACCOUNT.USER_ID%TYPE;
    v_manicurist_id MANICURIST.MANICURIST_ID%TYPE;
    v_code VARCHAR2(36);
    v_email VARCHAR2(100);
    v_shop_no PLS_INTEGER;
    v_address_count PLS_INTEGER;
    v_shop_name VARCHAR2(100);
    v_city VARCHAR2(100);
    v_district VARCHAR2(100);
    v_style VARCHAR2(20);
    v_season VARCHAR2(20);
    v_color VARCHAR2(20);
    v_price NUMBER(10,2);
    v_duration NUMBER;
    v_post_id NAIL_POST.POST_ID%TYPE;
    v_post_no PLS_INTEGER;
    v_local_post_no PLS_INTEGER;
    v_mani_no PLS_INTEGER;
    v_description NAIL_POST.DESCRIPTION%TYPE;
BEGIN
    -- 3 shops linked to existing address lookup rows.
    SELECT COUNT(*) INTO v_address_count FROM ADDRESS;
    IF v_address_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'ADDRESS must contain at least one existing row before running this seed.');
    END IF;

    FOR i IN 1..3 LOOP
        SELECT ADDRESS_ID, CITY, DISTRICT
          INTO v_address_id, v_city, v_district
          FROM (
                SELECT ADDRESS_ID, CITY, DISTRICT,
                       ROW_NUMBER() OVER (ORDER BY ADDRESS_ID) AS ROW_NO
                  FROM ADDRESS
          )
         WHERE ROW_NO = MOD(i - 1, v_address_count) + 1;

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
        WHEN MATCHED THEN
            UPDATE SET target.PASSWORD = '$2a$10$FUko7SNq4k1I8SjZfLBzOelp4jkmp65R.PvPJQmJtsWVBSw6/Lui.'
        WHEN NOT MATCHED THEN
            INSERT (USER_ID, USER_CODE, USER_NAME, PASSWORD, EMAIL, PHONE,
                    CREATE_TIME, UPDATE_TIME, VERIFIED)
            VALUES (SEQ_USER_ACCOUNT.NEXTVAL, source.user_code, source.user_name,
                    '$2a$10$FUko7SNq4k1I8SjZfLBzOelp4jkmp65R.PvPJQmJtsWVBSw6/Lui.',
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
        WHEN MATCHED THEN
            UPDATE SET target.PASSWORD = '$2a$10$FUko7SNq4k1I8SjZfLBzOelp4jkmp65R.PvPJQmJtsWVBSw6/Lui.'
        WHEN NOT MATCHED THEN
            INSERT (USER_ID, USER_CODE, USER_NAME, PASSWORD, EMAIL, PHONE,
                    CREATE_TIME, UPDATE_TIME, VERIFIED)
            VALUES (SEQ_USER_ACCOUNT.NEXTVAL, source.user_code, source.user_name,
                    '$2a$10$FUko7SNq4k1I8SjZfLBzOelp4jkmp65R.PvPJQmJtsWVBSw6/Lui.',
                    source.email, '092200' || TO_CHAR(i, 'FM0000'),
                    SYSTIMESTAMP, SYSTIMESTAMP, '1');
    END LOOP;

    -- All verified seed accounts get MEMBER; manicurists get MANICURIST, and the three seeded shop creators get SHOP_MANAGER.
    -- No ADMIN test account is defined by this seed.
    MERGE INTO USER_ROLE target
    USING (
        SELECT ua.USER_ID, 'MEMBER' ROLE_CODE
          FROM USER_ACCOUNT ua
         WHERE ua.USER_CODE LIKE 'SEED-USER-%'
        UNION ALL
        SELECT ua.USER_ID, 'MANICURIST' ROLE_CODE
          FROM USER_ACCOUNT ua
          JOIN MANICURIST m ON m.USER_ID = ua.USER_ID
         WHERE ua.USER_CODE LIKE 'SEED-USER-MANI-%'
           AND m.MANICURIST_CODE LIKE 'SEED-MANI-%'
        UNION ALL
        SELECT ua.USER_ID, 'SHOP_MANAGER' ROLE_CODE
          FROM USER_ACCOUNT ua
          JOIN MANICURIST m ON m.USER_ID = ua.USER_ID
         WHERE ua.USER_CODE LIKE 'SEED-USER-MANI-%'
           AND m.MANICURIST_CODE LIKE 'SEED-MANI-%'
           AND m.MANICURIST_CODE IN ('SEED-MANI-01', 'SEED-MANI-03', 'SEED-MANI-05')
    ) source
    ON (target.USER_ID = source.USER_ID AND target.ROLE_CODE = source.ROLE_CODE)
    WHEN MATCHED THEN
        UPDATE SET target.IS_ACTIVE = 1,
                   target.UPDATE_TIME = CASE WHEN target.IS_ACTIVE <> 1
                                             THEN SYSTIMESTAMP ELSE target.UPDATE_TIME END
    WHEN NOT MATCHED THEN
        INSERT (USER_ID, ROLE_CODE, IS_ACTIVE, GRANTED_BY_USER_ID, CREATE_TIME, UPDATE_TIME)
        VALUES (source.USER_ID, source.ROLE_CODE, 1, NULL, SYSTIMESTAMP, SYSTIMESTAMP);

    -- 120 services: 20 per manicurist.
    FOR i IN 1..6 LOOP
        v_code := 'SEED-MANI-' || TO_CHAR(i, 'FM00');
        SELECT MANICURIST_ID INTO v_manicurist_id FROM MANICURIST WHERE MANICURIST_CODE = v_code;

        FOR j IN 1..20 LOOP
            v_code := 'SEED-SERVICE-' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(j, 'FM00');
            v_price := 700 + (MOD(j - 1, 5) * 350) + (MOD(i, 3) * 100);
            v_duration := 60 + (MOD(j - 1, 4) * 30);
            MERGE INTO SERVICE_ITEM target
            USING (SELECT v_code service_code, v_manicurist_id manicurist_id,
                          '測試美甲服務 ' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(j, 'FM00') service_name,
                          v_price price, v_duration duration_min,
                          '僅供測試的合成服務項目 ' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(j, 'FM00') || '。' description FROM dual) source
            ON (target.SERVICE_CODE = source.service_code)
            WHEN MATCHED THEN
                UPDATE SET target.SERVICE_NAME = source.service_name
            WHEN NOT MATCHED THEN
                INSERT (SERVICE_ID, SERVICE_CODE, MANICURIST_ID, SERVICE_NAME, PRICE,
                        DURATION_MIN, DESCRIPTION, IS_ACTIVE, CREATE_TIME, UPDATE_TIME)
                VALUES (SEQ_SERVICE_ITEM.NEXTVAL, source.service_code, source.manicurist_id,
                        source.service_name, source.price, source.duration_min, source.description,
                        1, SYSTIMESTAMP, SYSTIMESTAMP);
        END LOOP;

    END LOOP;

    -- Remove seed posts outside the new set when upgrading a previous 36- or 120-post run.
    DELETE FROM NAIL_POST
     WHERE REGEXP_LIKE(DESCRIPTION, '^合成作品資料 [0-9]{2}-[0-9]{2}，供搜尋與明細 API 測試。$')
       AND NOT EXISTS (
           SELECT 1
             FROM MANICURIST m
            WHERE m.MANICURIST_ID = NAIL_POST.MANICURIST_ID
              AND m.MANICURIST_CODE LIKE 'SEED-MANI-%'
              AND TO_NUMBER(SUBSTR(NAIL_POST.DESCRIPTION, 8, 2)) = TO_NUMBER(SUBSTR(m.MANICURIST_CODE, 11, 2))
              AND TO_NUMBER(SUBSTR(NAIL_POST.DESCRIPTION, 11, 2))
                    BETWEEN 1 AND CASE WHEN TO_NUMBER(SUBSTR(m.MANICURIST_CODE, 11, 2)) = 6 THEN 10 ELSE 12 END
       );

    -- 70 enabled posts distributed across six manicurists.
    FOR post_seq IN 1..70 LOOP
        v_post_no := post_seq;
        v_mani_no := CEIL(v_post_no / 12);
        v_local_post_no := MOD(v_post_no - 1, 12) + 1;
        v_code := 'SEED-MANI-' || TO_CHAR(v_mani_no, 'FM00');
        SELECT MANICURIST_ID INTO v_manicurist_id FROM MANICURIST WHERE MANICURIST_CODE = v_code;

        v_style := CASE MOD(v_post_no, 6) WHEN 0 THEN 'KOREAN' WHEN 1 THEN 'JAPANESE'
                    WHEN 2 THEN 'FRENCH' WHEN 3 THEN 'MINIMAL' WHEN 4 THEN 'CUTE' ELSE 'ELEGANT' END;
        v_season := CASE MOD(v_post_no, 4) WHEN 0 THEN 'SPRING' WHEN 1 THEN 'SUMMER'
                      WHEN 2 THEN 'AUTUMN' ELSE 'WINTER' END;
        v_color := CASE MOD(v_post_no, 6) WHEN 0 THEN 'PINK' WHEN 1 THEN 'NUDE'
                     WHEN 2 THEN 'WHITE' WHEN 3 THEN 'BLUE' WHEN 4 THEN 'RED' ELSE 'MULTI' END;
        v_price := 900 + (MOD(v_post_no - 1, 10) * 250);
        v_description := '合成作品資料 ' || TO_CHAR(v_mani_no, 'FM00') || '-' ||
                         TO_CHAR(v_local_post_no, 'FM00') || '，供搜尋與明細 API 測試。';

        MERGE INTO NAIL_POST target
        USING (SELECT v_manicurist_id manicurist_id, v_price price,
                      v_description description, v_style style_code,
                      v_season season_code, v_color color_code FROM dual) source
        ON (target.DESCRIPTION = source.description)
        WHEN MATCHED THEN
            UPDATE SET target.MANICURIST_ID = source.manicurist_id,
                       target.PRICE = source.price,
                       target.STYLE_CODE = source.style_code,
                       target.SEASON_CODE = source.season_code,
                       target.MAIN_COLOR_CODE = source.color_code,
                       target.ENABLED = 1,
                       target.UPDATE_TIME = SYSTIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (POST_ID, MANICURIST_ID, PRICE, DESCRIPTION, STYLE_CODE, SEASON_CODE,
                    MAIN_COLOR_CODE, ENABLED, CREATE_TIME, UPDATE_TIME)
            VALUES (SEQ_NAIL_POST.NEXTVAL, source.manicurist_id, source.price,
                    source.description, source.style_code, source.season_code,
                    source.color_code, 1, SYSTIMESTAMP, SYSTIMESTAMP);
    END LOOP;

    -- 75 globally unique images distributed over 0-5 images per post.
    FOR image_seed IN (
        SELECT 1 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-001-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-001-01.jpg' storage_path,
               'SEED-POST-001-01.jpg' original_name, 'image/jpeg' content_type,
               189748 file_size FROM dual
        UNION ALL
        SELECT 3 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-003-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-003-01.jpg' storage_path,
               'SEED-POST-003-01.jpg' original_name, 'image/jpeg' content_type,
               616564 file_size FROM dual
        UNION ALL
        SELECT 5 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-005-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-005-01.jpg' storage_path,
               'SEED-POST-005-01.jpg' original_name, 'image/jpeg' content_type,
               175355 file_size FROM dual
        UNION ALL
        SELECT 7 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-007-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-007-01.jpg' storage_path,
               'SEED-POST-007-01.jpg' original_name, 'image/jpeg' content_type,
               93537 file_size FROM dual
        UNION ALL
        SELECT 9 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-009-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-009-01.jpg' storage_path,
               'SEED-POST-009-01.jpg' original_name, 'image/jpeg' content_type,
               794148 file_size FROM dual
        UNION ALL
        SELECT 11 post_no, 1 image_no, 1 manicurist_no,
               'SEED-POST-011-01.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-011-01.jpg' storage_path,
               'SEED-POST-011-01.jpg' original_name, 'image/jpeg' content_type,
               132882 file_size FROM dual
        UNION ALL
        SELECT 11 post_no, 2 image_no, 1 manicurist_no,
               'SEED-POST-011-02.jpg' storage_name, 'SEED-MANI-01/20261110/SEED-POST-011-02.jpg' storage_path,
               'SEED-POST-011-02.jpg' original_name, 'image/jpeg' content_type,
               103742 file_size FROM dual
        UNION ALL
        SELECT 13 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-013-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-013-01.jpg' storage_path,
               'SEED-POST-013-01.jpg' original_name, 'image/jpeg' content_type,
               153257 file_size FROM dual
        UNION ALL
        SELECT 13 post_no, 2 image_no, 2 manicurist_no,
               'SEED-POST-013-02.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-013-02.jpg' storage_path,
               'SEED-POST-013-02.jpg' original_name, 'image/jpeg' content_type,
               1234194 file_size FROM dual
        UNION ALL
        SELECT 13 post_no, 3 image_no, 2 manicurist_no,
               'SEED-POST-013-03.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-013-03.jpg' storage_path,
               'SEED-POST-013-03.jpg' original_name, 'image/jpeg' content_type,
               84803 file_size FROM dual
        UNION ALL
        SELECT 15 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-015-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-015-01.jpg' storage_path,
               'SEED-POST-015-01.jpg' original_name, 'image/jpeg' content_type,
               310405 file_size FROM dual
        UNION ALL
        SELECT 15 post_no, 2 image_no, 2 manicurist_no,
               'SEED-POST-015-02.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-015-02.jpg' storage_path,
               'SEED-POST-015-02.jpg' original_name, 'image/jpeg' content_type,
               70534 file_size FROM dual
        UNION ALL
        SELECT 15 post_no, 3 image_no, 2 manicurist_no,
               'SEED-POST-015-03.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-015-03.jpg' storage_path,
               'SEED-POST-015-03.jpg' original_name, 'image/jpeg' content_type,
               303475 file_size FROM dual
        UNION ALL
        SELECT 15 post_no, 4 image_no, 2 manicurist_no,
               'SEED-POST-015-04.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-015-04.jpg' storage_path,
               'SEED-POST-015-04.jpg' original_name, 'image/jpeg' content_type,
               208844 file_size FROM dual
        UNION ALL
        SELECT 16 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-016-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-016-01.jpg' storage_path,
               'SEED-POST-016-01.jpg' original_name, 'image/jpeg' content_type,
               125552 file_size FROM dual
        UNION ALL
        SELECT 17 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-017-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-017-01.jpg' storage_path,
               'SEED-POST-017-01.jpg' original_name, 'image/jpeg' content_type,
               63007 file_size FROM dual
        UNION ALL
        SELECT 17 post_no, 2 image_no, 2 manicurist_no,
               'SEED-POST-017-02.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-017-02.jpg' storage_path,
               'SEED-POST-017-02.jpg' original_name, 'image/jpeg' content_type,
               136724 file_size FROM dual
        UNION ALL
        SELECT 17 post_no, 3 image_no, 2 manicurist_no,
               'SEED-POST-017-03.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-017-03.jpg' storage_path,
               'SEED-POST-017-03.jpg' original_name, 'image/jpeg' content_type,
               218387 file_size FROM dual
        UNION ALL
        SELECT 17 post_no, 4 image_no, 2 manicurist_no,
               'SEED-POST-017-04.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-017-04.jpg' storage_path,
               'SEED-POST-017-04.jpg' original_name, 'image/jpeg' content_type,
               126600 file_size FROM dual
        UNION ALL
        SELECT 17 post_no, 5 image_no, 2 manicurist_no,
               'SEED-POST-017-05.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-017-05.jpg' storage_path,
               'SEED-POST-017-05.jpg' original_name, 'image/jpeg' content_type,
               113177 file_size FROM dual
        UNION ALL
        SELECT 18 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-018-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-018-01.jpg' storage_path,
               'SEED-POST-018-01.jpg' original_name, 'image/jpeg' content_type,
               55359 file_size FROM dual
        UNION ALL
        SELECT 20 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-020-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-020-01.jpg' storage_path,
               'SEED-POST-020-01.jpg' original_name, 'image/jpeg' content_type,
               60894 file_size FROM dual
        UNION ALL
        SELECT 22 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-022-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-022-01.jpg' storage_path,
               'SEED-POST-022-01.jpg' original_name, 'image/jpeg' content_type,
               144826 file_size FROM dual
        UNION ALL
        SELECT 24 post_no, 1 image_no, 2 manicurist_no,
               'SEED-POST-024-01.jpg' storage_name, 'SEED-MANI-02/20261110/SEED-POST-024-01.jpg' storage_path,
               'SEED-POST-024-01.jpg' original_name, 'image/jpeg' content_type,
               117175 file_size FROM dual
        UNION ALL
        SELECT 26 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-026-01.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-026-01.jpg' storage_path,
               'SEED-POST-026-01.jpg' original_name, 'image/jpeg' content_type,
               117004 file_size FROM dual
        UNION ALL
        SELECT 28 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-028-01.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-028-01.jpg' storage_path,
               'SEED-POST-028-01.jpg' original_name, 'image/jpeg' content_type,
               440869 file_size FROM dual
        UNION ALL
        SELECT 28 post_no, 2 image_no, 3 manicurist_no,
               'SEED-POST-028-02.webp' storage_name, 'SEED-MANI-03/20261110/SEED-POST-028-02.webp' storage_path,
               'SEED-POST-028-02.webp' original_name, 'image/webp' content_type,
               302494 file_size FROM dual
        UNION ALL
        SELECT 30 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-030-01.webp' storage_name, 'SEED-MANI-03/20261110/SEED-POST-030-01.webp' storage_path,
               'SEED-POST-030-01.webp' original_name, 'image/webp' content_type,
               275752 file_size FROM dual
        UNION ALL
        SELECT 30 post_no, 2 image_no, 3 manicurist_no,
               'SEED-POST-030-02.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-030-02.jpg' storage_path,
               'SEED-POST-030-02.jpg' original_name, 'image/jpeg' content_type,
               186076 file_size FROM dual
        UNION ALL
        SELECT 30 post_no, 3 image_no, 3 manicurist_no,
               'SEED-POST-030-03.png' storage_name, 'SEED-MANI-03/20261110/SEED-POST-030-03.png' storage_path,
               'SEED-POST-030-03.png' original_name, 'image/png' content_type,
               388255 file_size FROM dual
        UNION ALL
        SELECT 32 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-032-01.png' storage_name, 'SEED-MANI-03/20261110/SEED-POST-032-01.png' storage_path,
               'SEED-POST-032-01.png' original_name, 'image/png' content_type,
               519818 file_size FROM dual
        UNION ALL
        SELECT 32 post_no, 2 image_no, 3 manicurist_no,
               'SEED-POST-032-02.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-032-02.jpg' storage_path,
               'SEED-POST-032-02.jpg' original_name, 'image/jpeg' content_type,
               43349 file_size FROM dual
        UNION ALL
        SELECT 32 post_no, 3 image_no, 3 manicurist_no,
               'SEED-POST-032-03.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-032-03.jpg' storage_path,
               'SEED-POST-032-03.jpg' original_name, 'image/jpeg' content_type,
               617307 file_size FROM dual
        UNION ALL
        SELECT 32 post_no, 4 image_no, 3 manicurist_no,
               'SEED-POST-032-04.png' storage_name, 'SEED-MANI-03/20261110/SEED-POST-032-04.png' storage_path,
               'SEED-POST-032-04.png' original_name, 'image/png' content_type,
               1320271 file_size FROM dual
        UNION ALL
        SELECT 33 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-033-01.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-033-01.jpg' storage_path,
               'SEED-POST-033-01.jpg' original_name, 'image/jpeg' content_type,
               147795 file_size FROM dual
        UNION ALL
        SELECT 34 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-034-01.webp' storage_name, 'SEED-MANI-03/20261110/SEED-POST-034-01.webp' storage_path,
               'SEED-POST-034-01.webp' original_name, 'image/webp' content_type,
               432474 file_size FROM dual
        UNION ALL
        SELECT 34 post_no, 2 image_no, 3 manicurist_no,
               'SEED-POST-034-02.webp' storage_name, 'SEED-MANI-03/20261110/SEED-POST-034-02.webp' storage_path,
               'SEED-POST-034-02.webp' original_name, 'image/webp' content_type,
               47070 file_size FROM dual
        UNION ALL
        SELECT 34 post_no, 3 image_no, 3 manicurist_no,
               'SEED-POST-034-03.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-034-03.jpg' storage_path,
               'SEED-POST-034-03.jpg' original_name, 'image/jpeg' content_type,
               191600 file_size FROM dual
        UNION ALL
        SELECT 34 post_no, 4 image_no, 3 manicurist_no,
               'SEED-POST-034-04.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-034-04.jpg' storage_path,
               'SEED-POST-034-04.jpg' original_name, 'image/jpeg' content_type,
               133369 file_size FROM dual
        UNION ALL
        SELECT 34 post_no, 5 image_no, 3 manicurist_no,
               'SEED-POST-034-05.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-034-05.jpg' storage_path,
               'SEED-POST-034-05.jpg' original_name, 'image/jpeg' content_type,
               105422 file_size FROM dual
        UNION ALL
        SELECT 35 post_no, 1 image_no, 3 manicurist_no,
               'SEED-POST-035-01.jpg' storage_name, 'SEED-MANI-03/20261110/SEED-POST-035-01.jpg' storage_path,
               'SEED-POST-035-01.jpg' original_name, 'image/jpeg' content_type,
               445636 file_size FROM dual
        UNION ALL
        SELECT 37 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-037-01.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-037-01.jpg' storage_path,
               'SEED-POST-037-01.jpg' original_name, 'image/jpeg' content_type,
               79476 file_size FROM dual
        UNION ALL
        SELECT 39 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-039-01.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-039-01.jpg' storage_path,
               'SEED-POST-039-01.jpg' original_name, 'image/jpeg' content_type,
               58570 file_size FROM dual
        UNION ALL
        SELECT 41 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-041-01.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-041-01.jpg' storage_path,
               'SEED-POST-041-01.jpg' original_name, 'image/jpeg' content_type,
               245241 file_size FROM dual
        UNION ALL
        SELECT 43 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-043-01.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-043-01.jpg' storage_path,
               'SEED-POST-043-01.jpg' original_name, 'image/jpeg' content_type,
               103338 file_size FROM dual
        UNION ALL
        SELECT 45 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-045-01.webp' storage_name, 'SEED-MANI-04/20261110/SEED-POST-045-01.webp' storage_path,
               'SEED-POST-045-01.webp' original_name, 'image/webp' content_type,
               147076 file_size FROM dual
        UNION ALL
        SELECT 45 post_no, 2 image_no, 4 manicurist_no,
               'SEED-POST-045-02.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-045-02.jpg' storage_path,
               'SEED-POST-045-02.jpg' original_name, 'image/jpeg' content_type,
               95223 file_size FROM dual
        UNION ALL
        SELECT 47 post_no, 1 image_no, 4 manicurist_no,
               'SEED-POST-047-01.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-047-01.jpg' storage_path,
               'SEED-POST-047-01.jpg' original_name, 'image/jpeg' content_type,
               164982 file_size FROM dual
        UNION ALL
        SELECT 47 post_no, 2 image_no, 4 manicurist_no,
               'SEED-POST-047-02.jpg' storage_name, 'SEED-MANI-04/20261110/SEED-POST-047-02.jpg' storage_path,
               'SEED-POST-047-02.jpg' original_name, 'image/jpeg' content_type,
               112391 file_size FROM dual
        UNION ALL
        SELECT 49 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-049-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-049-01.jpg' storage_path,
               'SEED-POST-049-01.jpg' original_name, 'image/jpeg' content_type,
               112534 file_size FROM dual
        UNION ALL
        SELECT 49 post_no, 2 image_no, 5 manicurist_no,
               'SEED-POST-049-02.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-049-02.jpg' storage_path,
               'SEED-POST-049-02.jpg' original_name, 'image/jpeg' content_type,
               501795 file_size FROM dual
        UNION ALL
        SELECT 49 post_no, 3 image_no, 5 manicurist_no,
               'SEED-POST-049-03.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-049-03.jpg' storage_path,
               'SEED-POST-049-03.jpg' original_name, 'image/jpeg' content_type,
               57184 file_size FROM dual
        UNION ALL
        SELECT 50 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-050-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-050-01.jpg' storage_path,
               'SEED-POST-050-01.jpg' original_name, 'image/jpeg' content_type,
               180114 file_size FROM dual
        UNION ALL
        SELECT 51 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-051-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-051-01.jpg' storage_path,
               'SEED-POST-051-01.jpg' original_name, 'image/jpeg' content_type,
               127305 file_size FROM dual
        UNION ALL
        SELECT 51 post_no, 2 image_no, 5 manicurist_no,
               'SEED-POST-051-02.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-051-02.jpg' storage_path,
               'SEED-POST-051-02.jpg' original_name, 'image/jpeg' content_type,
               87949 file_size FROM dual
        UNION ALL
        SELECT 51 post_no, 3 image_no, 5 manicurist_no,
               'SEED-POST-051-03.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-051-03.jpg' storage_path,
               'SEED-POST-051-03.jpg' original_name, 'image/jpeg' content_type,
               51111 file_size FROM dual
        UNION ALL
        SELECT 51 post_no, 4 image_no, 5 manicurist_no,
               'SEED-POST-051-04.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-051-04.jpg' storage_path,
               'SEED-POST-051-04.jpg' original_name, 'image/jpeg' content_type,
               96431 file_size FROM dual
        UNION ALL
        SELECT 51 post_no, 5 image_no, 5 manicurist_no,
               'SEED-POST-051-05.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-051-05.jpg' storage_path,
               'SEED-POST-051-05.jpg' original_name, 'image/jpeg' content_type,
               72820 file_size FROM dual
        UNION ALL
        SELECT 52 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-052-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-052-01.jpg' storage_path,
               'SEED-POST-052-01.jpg' original_name, 'image/jpeg' content_type,
               99898 file_size FROM dual
        UNION ALL
        SELECT 54 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-054-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-054-01.jpg' storage_path,
               'SEED-POST-054-01.jpg' original_name, 'image/jpeg' content_type,
               180716 file_size FROM dual
        UNION ALL
        SELECT 56 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-056-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-056-01.jpg' storage_path,
               'SEED-POST-056-01.jpg' original_name, 'image/jpeg' content_type,
               150121 file_size FROM dual
        UNION ALL
        SELECT 58 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-058-01.png' storage_name, 'SEED-MANI-05/20261110/SEED-POST-058-01.png' storage_path,
               'SEED-POST-058-01.png' original_name, 'image/png' content_type,
               177919 file_size FROM dual
        UNION ALL
        SELECT 60 post_no, 1 image_no, 5 manicurist_no,
               'SEED-POST-060-01.jpg' storage_name, 'SEED-MANI-05/20261110/SEED-POST-060-01.jpg' storage_path,
               'SEED-POST-060-01.jpg' original_name, 'image/jpeg' content_type,
               6137849 file_size FROM dual
        UNION ALL
        SELECT 62 post_no, 1 image_no, 6 manicurist_no,
               'SEED-POST-062-01.png' storage_name, 'SEED-MANI-06/20261110/SEED-POST-062-01.png' storage_path,
               'SEED-POST-062-01.png' original_name, 'image/png' content_type,
               1076938 file_size FROM dual
        UNION ALL
        SELECT 64 post_no, 1 image_no, 6 manicurist_no,
               'SEED-POST-064-01.webp' storage_name, 'SEED-MANI-06/20261110/SEED-POST-064-01.webp' storage_path,
               'SEED-POST-064-01.webp' original_name, 'image/webp' content_type,
               22172 file_size FROM dual
        UNION ALL
        SELECT 64 post_no, 2 image_no, 6 manicurist_no,
               'SEED-POST-064-02.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-064-02.jpg' storage_path,
               'SEED-POST-064-02.jpg' original_name, 'image/jpeg' content_type,
               70297 file_size FROM dual
        UNION ALL
        SELECT 66 post_no, 1 image_no, 6 manicurist_no,
               'SEED-POST-066-01.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-066-01.jpg' storage_path,
               'SEED-POST-066-01.jpg' original_name, 'image/jpeg' content_type,
               52532 file_size FROM dual
        UNION ALL
        SELECT 66 post_no, 2 image_no, 6 manicurist_no,
               'SEED-POST-066-02.png' storage_name, 'SEED-MANI-06/20261110/SEED-POST-066-02.png' storage_path,
               'SEED-POST-066-02.png' original_name, 'image/png' content_type,
               856209 file_size FROM dual
        UNION ALL
        SELECT 66 post_no, 3 image_no, 6 manicurist_no,
               'SEED-POST-066-03.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-066-03.jpg' storage_path,
               'SEED-POST-066-03.jpg' original_name, 'image/jpeg' content_type,
               425676 file_size FROM dual
        UNION ALL
        SELECT 68 post_no, 1 image_no, 6 manicurist_no,
               'SEED-POST-068-01.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-068-01.jpg' storage_path,
               'SEED-POST-068-01.jpg' original_name, 'image/jpeg' content_type,
               122619 file_size FROM dual
        UNION ALL
        SELECT 68 post_no, 2 image_no, 6 manicurist_no,
               'SEED-POST-068-02.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-068-02.jpg' storage_path,
               'SEED-POST-068-02.jpg' original_name, 'image/jpeg' content_type,
               70394 file_size FROM dual
        UNION ALL
        SELECT 68 post_no, 3 image_no, 6 manicurist_no,
               'SEED-POST-068-03.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-068-03.jpg' storage_path,
               'SEED-POST-068-03.jpg' original_name, 'image/jpeg' content_type,
               40813 file_size FROM dual
        UNION ALL
        SELECT 68 post_no, 4 image_no, 6 manicurist_no,
               'SEED-POST-068-04.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-068-04.jpg' storage_path,
               'SEED-POST-068-04.jpg' original_name, 'image/jpeg' content_type,
               82514 file_size FROM dual
        UNION ALL
        SELECT 68 post_no, 5 image_no, 6 manicurist_no,
               'SEED-POST-068-05.jpg' storage_name, 'SEED-MANI-06/20261110/SEED-POST-068-05.jpg' storage_path,
               'SEED-POST-068-05.jpg' original_name, 'image/jpeg' content_type,
               994155 file_size FROM dual
        UNION ALL
        SELECT 69 post_no, 1 image_no, 6 manicurist_no,
               'SEED-POST-069-01.png' storage_name, 'SEED-MANI-06/20261110/SEED-POST-069-01.png' storage_path,
               'SEED-POST-069-01.png' original_name, 'image/png' content_type,
               735812 file_size FROM dual
    ) LOOP
        SELECT POST_ID INTO v_post_id
          FROM NAIL_POST
         WHERE DESCRIPTION = '合成作品資料 ' || TO_CHAR(image_seed.manicurist_no, 'FM00') || '-' ||
               TO_CHAR(MOD(image_seed.post_no - 1, 12) + 1, 'FM00') || '，供搜尋與明細 API 測試。';

        MERGE INTO NAIL_POST_IMAGE target
        USING (SELECT v_post_id post_id, image_seed.storage_name storage_name,
                      image_seed.storage_path storage_path, image_seed.original_name original_name,
                      image_seed.content_type content_type, image_seed.file_size file_size,
                      image_seed.image_no sort_order,
                      CASE WHEN image_seed.image_no = 1 THEN 1 ELSE 0 END is_cover
                 FROM dual) source
        ON (target.POST_ID = source.post_id AND target.STORAGE_NAME = source.storage_name)
        WHEN MATCHED THEN
            UPDATE SET target.STORAGE_PATH = source.storage_path,
                       target.ORIGINAL_NAME = source.original_name,
                       target.CONTENT_TYPE = source.content_type,
                       target.FILE_SIZE = source.file_size,
                       target.SORT_ORDER = source.sort_order,
                       target.IS_COVER = source.is_cover,
                       target.UPDATE_TIME = SYSTIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (IMAGE_ID, POST_ID, STORAGE_NAME, STORAGE_PATH, ORIGINAL_NAME,
                    CONTENT_TYPE, FILE_SIZE, SORT_ORDER, IS_COVER, CREATE_TIME, UPDATE_TIME)
            VALUES (SEQ_NAIL_POST_IMAGE.NEXTVAL, source.post_id, source.storage_name,
                    source.storage_path, source.original_name, source.content_type,
                    source.file_size, source.sort_order, source.is_cover,
                    SYSTIMESTAMP, SYSTIMESTAMP);
    END LOOP;

    COMMIT;
END;


