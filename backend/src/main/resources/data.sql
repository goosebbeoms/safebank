-- SafeBank 초기 데이터
-- 실행 순서: Member -> Account -> Transaction

-- 기존 데이터 정리 (개발환경용)
-- DELETE FROM transaction;
-- DELETE FROM account;
-- DELETE FROM member;

-- 1. 회원 데이터 (member 테이블)
INSERT INTO member (member_id, name, email, phone_number, status, created_at, updated_at)
VALUES (1, '홍길동', 'hong@safebank.com', '010-1111-1111', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '김철수', 'kim@safebank.com', '010-2222-2222', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '이영희', 'lee@safebank.com', '010-3333-3333', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '박민수', 'park@safebank.com', '010-4444-4444', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, '최지은', 'choi@safebank.com', '010-5555-5555', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. 계좌 데이터 (account 테이블)
INSERT INTO account (account_id, account_number, member_id, balance, status, created_at, updated_at)
VALUES (1, '3333123456789012', 1, 1500000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '3333234567890123', 1, 750000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '3333345678901234', 2, 2000000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '3333456789012345', 3, 850000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, '3333567890123456', 4, 1200000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (6, '3333678901234567', 5, 500000.00, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 거래 내역 데이터 (transaction 테이블)
INSERT INTO transaction (transaction_id, from_account_id, to_account_id, amount, type, status, description, created_at)
VALUES
-- 이체 거래들
(1, 1, 3, 100000.00, 'TRANSFER', 'COMPLETED', '생활비 송금', CURRENT_TIMESTAMP - INTERVAL 5 DAY),
(2, 3, 4, 50000.00, 'TRANSFER', 'COMPLETED', '용돈', CURRENT_TIMESTAMP - INTERVAL 4 DAY),
(3, 5, 1, 200000.00, 'TRANSFER', 'COMPLETED', '대출 상환', CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(4, 2, 6, 75000.00, 'TRANSFER', 'COMPLETED', '식비 지원', CURRENT_TIMESTAMP - INTERVAL 2 DAY),
(5, 4, 5, 30000.00, 'TRANSFER', 'COMPLETED', '카페 비용', CURRENT_TIMESTAMP - INTERVAL 1 DAY),

-- 입금 거래들 (from_account_id가 NULL인 경우)
(6, NULL, 1, 500000.00, 'DEPOSIT', 'COMPLETED', '급여 입금', CURRENT_TIMESTAMP - INTERVAL 7 DAY),
(7, NULL, 2, 300000.00, 'DEPOSIT', 'COMPLETED', '보너스 입금', CURRENT_TIMESTAMP - INTERVAL 6 DAY),
(8, NULL, 3, 100000.00, 'DEPOSIT', 'COMPLETED', '이자 입금', CURRENT_TIMESTAMP - INTERVAL 5 DAY),

-- 출금 거래들 (from_account_id와 to_account_id가 동일한 경우)
(9, 1, 1, 50000.00, 'WITHDRAWAL', 'COMPLETED', 'ATM 출금', CURRENT_TIMESTAMP - INTERVAL 3 DAY),
(10, 3, 3, 100000.00, 'WITHDRAWAL', 'COMPLETED', '현금 출금', CURRENT_TIMESTAMP - INTERVAL 1 DAY);
