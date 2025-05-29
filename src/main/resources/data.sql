-- USER 테이블 더미 데이터
INSERT INTO USER (ID, password, name, points, tendency) VALUES
                                                            ('root', '1234', '홍길동', 1000, '저축형'),
                                                            ('user2', 'pw2', '김철수', 1500, '소비형'),
                                                            ('user3', 'pw3', '이영희', 2000, '투자형'),
                                                            ('user4', 'pw4', '박영수', 2500, '저축형'),
                                                            ('user5', 'pw5', '최민지', 3000, '균형형');

-- STOCK 테이블 더미 데이터
INSERT INTO STOCK (ID, name, price, category) VALUES
                                                  ('stock001', '초코파이', 3000, '간식'),
                                                  ('stock002', '레고세트', 20000, '장난감'),
                                                  ('stock003', '딸기우유', 1200, '간식'),
                                                  ('stock004', '펜', 1500, '문구');

-- QUIZ 테이블 더미 데이터
INSERT INTO QUIZ (ID, question, answer, explanation, level) VALUES
                                                                (1, '돈을 저축하는 이유는?', '미래를 준비하기 위해', '미래의 필요를 대비할 수 있습니다.', '초급'),
                                                                (2, '가격이 오를 때 사는 것이 좋은가요?', '아니오', '낮을 때 사서 높을 때 파는 것이 이득입니다.', '중급');

-- WORKSHEET 테이블 더미 데이터
INSERT INTO WORKSHEET (ID, difficulty, title, content) VALUES
                                                           (1, '쉬움', '용돈 관리', 'http://example.com/worksheet1'),
                                                           (2, '보통', '소비와 저축', 'http://example.com/worksheet2');

-- FEEDBACK 테이블 더미 데이터
INSERT INTO FEEDBACK (ID, user_id, date, content) VALUES
                                                      (1, 'root', '2025-05-01', '좋은 콘텐츠였어요!'),
                                                      (2, 'user2', '2025-05-02', '재미있게 학습했어요.');

-- ARTICLE 테이블 더미 데이터
INSERT INTO ARTICLE (ID, stock_id, date, title, content, effect) VALUES
                                                                     (1, 'stock001', '2025-05-01', '초코파이 가격 상승', '수요 증가로 가격 상승', 'positive'),
                                                                     (2, 'stock002', '2025-05-01', '레고세트 품절 사태', '재고 부족으로 가격 급등', 'positive');

-- USER_STOCK 테이블 더미 데이터
INSERT INTO USER_STOCK (ID, user_id, stock_id, quantity, total) VALUES
                                                                    (1, 'root', 'stock001', 2, 6000),
                                                                    (2, 'user3', 'stock003', 5, 6000),
                                                                    (3, 'user5', 'stock002', 1, 20000),
                                                                    (4, 'user2', 'stock004', 3, 4500);

-- COMPLETION 테이블 더미 데이터
INSERT INTO COMPLETION (ID, user_id, worksheet_id, completion, date) VALUES
                                                                         (1, 'root', 1, TRUE, '2025-05-01'),
                                                                         (2, 'user2', 2, FALSE, '2025-05-02');

-- STOCKLOG 테이블 더미 데이터
INSERT INTO STOCKLOG (ID, user_id, stock_id, date, quantity) VALUES
                                                                 ('log1', 'root', 'stock001', '2025-05-01', 2),
                                                                 ('log2', 'user3', 'stock003', '2025-05-02', 5);

-- USER_QUIZ 테이블 더미 데이터
INSERT INTO USER_QUIZ (ID, user_id, quiz_id, date, correct) VALUES
                                                                (1, 'root', 1, '2025-05-01', TRUE),
                                                                (2, 'user2', 2, '2025-05-02', FALSE);
