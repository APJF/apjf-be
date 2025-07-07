/* ============================
   SEED DATA – CMS Service Test Data
   ============================ */

/* ---------- AUTHORITIES ---------- */
INSERT INTO authorities (authority)
VALUES ('ROLE_USER'),
       ('ROLE_STAFF'),
       ('ROLE_MANAGER'),
       ('ROLE_ADMIN');

/* ---------- TOPIC ---------- */
INSERT INTO topic (name)
VALUES ('Tiếng Nhật'),
       ('Lập trình');

/* ---------- COURSE (Simple version for testing) ---------- */
INSERT INTO course (id, title, description, duration, level, status)
VALUES ('course-01', 'Tieng Nhat N5', 'Khoa hoc co ban', 40.5, 'BEGINNER', 'PUBLISHED');

/* Add second course after first one works */
INSERT INTO course (id, title, description, duration, level, status, prerequisite_course_id)
VALUES ('course-02', 'Tieng Nhat N4', 'Khoa hoc nang cao', 60.0, 'INTERMEDIATE', 'PUBLISHED', 'course-01');

/* ---------- CHAPTER ---------- */
INSERT INTO chapter (id, title, description, status, course_id)
VALUES ('chapter-01', 'Hiragana', 'Hoc Hiragana co ban', 'PUBLISHED', 'course-01'),
       ('chapter-02', 'Katakana', 'Hoc Katakana co ban', 'PUBLISHED', 'course-01'),
       ('chapter-03', 'Ngu phap N4', 'Hoc ngu phap N4', 'PUBLISHED', 'course-02'),
       ('chapter-04', 'Hoi thoai', 'Luyen hoi thoai', 'PUBLISHED', 'course-02');

/* ---------- UNIT ---------- */
INSERT INTO unit (id, title, description, status, chapter_id)
VALUES ('unit-01', 'Tu vung Hiragana', 'Hoc tu vung co ban', 'PUBLISHED', 'chapter-01'),
       ('unit-02', 'Luyen doc Hiragana', 'Bai tap doc', 'PUBLISHED', 'chapter-01'),
       ('unit-03', 'Tu vung Katakana', 'Hoc tu vung Katakana', 'PUBLISHED', 'chapter-02'),
       ('unit-04', 'Luyen viet Katakana', 'Bai tap viet', 'PUBLISHED', 'chapter-02'),
       ('unit-05', 'Te-form', 'Hoc te-form', 'PUBLISHED', 'chapter-03'),
       ('unit-06', 'Kha nang', 'Dien dat kha nang', 'PUBLISHED', 'chapter-03'),
       ('unit-07', 'Hoi thoai cua hang', 'Luyen tap mua sam', 'PUBLISHED', 'chapter-04'),
       ('unit-08', 'Hoi thoai nha hang', 'Luyen tap di an', 'PUBLISHED', 'chapter-04');

/* ---------- MATERIAL ---------- */
INSERT INTO material (id, description, file_url, type, unit_id)
VALUES ('material-01', 'Video Hiragana', '/videos/hiragana.mp4', 'GRAMMAR', 'unit-01'),
       ('material-02', 'PDF Hiragana', '/docs/hiragana.pdf', 'VOCAB', 'unit-01'),
       ('material-03', 'Audio Katakana', '/audios/katakana.mp3', 'LISTENING', 'unit-03'),
       ('material-04', 'Quiz Te-form', '/quizzes/te-form.html', 'GRAMMAR', 'unit-05');

/* ---------- COURSE_TOPIC ---------- */
INSERT INTO course_topic (course_id, topic_id)
VALUES ('course-01', 1),
       ('course-02', 1);

/* ---------- APPROVAL_REQUEST ---------- */
INSERT INTO approval_request (course_id, chapter_id, unit_id, material_id, target_type, created_by, created_at, request_type, decision, feedback, reviewed_by, reviewed_at)
VALUES
    -- PENDING requests (chờ Manager duyệt)
    ('course-01', NULL, NULL, NULL, 'COURSE', 'instructor-01', NOW() - INTERVAL '2 days', 'UPDATE', 'PENDING', NULL, NULL, NULL),
    (NULL, 'chapter-01', NULL, NULL, 'CHAPTER', 'instructor-02', NOW() - INTERVAL '1 days', 'CREATE', 'PENDING', NULL, NULL, NULL),
    (NULL, NULL, 'unit-05', NULL, 'UNIT', 'instructor-01', NOW() - INTERVAL '3 hours', 'UPDATE', 'PENDING', NULL, NULL, NULL),

    -- APPROVED requests (đã được Manager duyệt)
    ('course-02', NULL, NULL, NULL, 'COURSE', 'instructor-02', NOW() - INTERVAL '7 days', 'CREATE', 'APPROVED', 'Khóa học chất lượng cao, nội dung phù hợp với learner path', 'manager-01', NOW() - INTERVAL '5 days'),
    (NULL, 'chapter-02', NULL, NULL, 'CHAPTER', 'instructor-01', NOW() - INTERVAL '10 days', 'CREATE', 'APPROVED', 'Chương được thiết kế tốt với bài tập phong phú', 'manager-02', NOW() - INTERVAL '8 days'),

    -- REJECTED requests (đã bị Manager từ chối)
    (NULL, NULL, NULL, 'material-01', 'MATERIAL', 'instructor-03', NOW() - INTERVAL '15 days', 'UPDATE', 'REJECTED', 'Chất lượng video chưa đạt yêu cầu, cần cải thiện âm thanh và hình ảnh', 'manager-01', NOW() - INTERVAL '12 days');

/* ---------- EXAM TEST DATA ---------- */

/* Questions */
INSERT INTO questions (id, content, correct_answer, type, explanation, file_url, created_at)
VALUES
    ('q1', 'Hiragana あ được đọc như thế nào?', 'a', 'MULTIPLE_CHOICE', 'あ được đọc là "a"', null, NOW()),
    ('q2', 'Katakana カ được đọc như thế nào?', 'ka', 'MULTIPLE_CHOICE', 'カ được đọc là "ka"', null, NOW()),
    ('q3', 'Te-form của taberu là gì?', 'tabete', 'SHORT_ANSWER', 'Te-form của taberu là tabete', null, NOW()),
    ('q4', 'Tiếng Nhật có bao nhiêu chữ Hiragana cơ bản?', null, 'TRUE_FALSE', '46 chữ Hiragana cơ bản', null, NOW()),
    ('q5', 'Điền từ thích hợp: Watashi __ gakusei desu', 'wa', 'FILL_BLANK', 'Sử dụng trợ từ wa', null, NOW());

/* Question Options */
INSERT INTO question_options (id, content, is_correct, question_id)
VALUES
    ('opt1', 'a', true, 'q1'),
    ('opt2', 'i', false, 'q1'),
    ('opt3', 'u', false, 'q1'),
    ('opt4', 'e', false, 'q1'),

    ('opt5', 'ka', true, 'q2'),
    ('opt6', 'ki', false, 'q2'),
    ('opt7', 'ku', false, 'q2'),
    ('opt8', 'ke', false, 'q2'),

    ('opt9', '46', true, 'q4'),
    ('opt10', '50', false, 'q4');

/* Exams */
INSERT INTO exams (id, title, description, duration, exam_scope_type, created_at)
VALUES
    ('exam1', 'Kiểm tra Hiragana cơ bản', 'Bài kiểm tra về chữ Hiragana', 30, 'CLASS', NOW()),
    ('exam2', 'Kiểm tra Katakana cơ bản', 'Bài kiểm tra về chữ Katakana', 25, 'CLASS', NOW()),
    ('exam3', 'Kiểm tra tổng hợp N5', 'Bài kiểm tra toàn diện N5', 60, 'SCHOOL', NOW());

/* Exam Questions (Many-to-Many) */
INSERT INTO exam_questions (exam_id, question_id)
VALUES
    ('exam1', 'q1'),
    ('exam1', 'q4'),
    ('exam1', 'q5'),

    ('exam2', 'q2'),
    ('exam2', 'q4'),

    ('exam3', 'q1'),
    ('exam3', 'q2'),
    ('exam3', 'q3'),
    ('exam3', 'q4'),
    ('exam3', 'q5');

/* Exam Locations */
INSERT INTO exam_locations (id, exam_id, scope_id, scope_type)
VALUES
    ('loc1', 'exam1', 'class-101', 'CLASS'),
    ('loc2', 'exam1', 'class-102', 'CLASS'),
    ('loc3', 'exam2', 'class-101', 'CLASS'),
    ('loc4', 'exam3', 'school-001', 'SCHOOL');

/* Exam Results */
INSERT INTO exam_results (id, started_at, submitted_at, score, status, user_id, exam_id)
VALUES
    ('result1', '2024-12-01 09:00:00', '2024-12-01 09:25:00', 8.5, 'PASSED', 'user1', 'exam1'),
    ('result2', '2024-12-01 09:00:00', '2024-12-01 09:20:00', 6.0, 'FAILED', 'user2', 'exam1'),
    ('result3', '2024-12-01 10:00:00', '2024-12-01 10:55:00', 9.2, 'PASSED', 'user1', 'exam3'),
    ('result4', '2024-12-01 10:00:00', null, null, null, 'user3', 'exam3'); -- Chưa nộp bài

/* Exam Result Answers */
INSERT INTO exam_result_answers (id, user_answer, is_correct, exam_result_id, question_id, selected_option_id)
VALUES
    ('ans1', 'a', true, 'result1', 'q1', 'opt1'),
    ('ans2', '46', true, 'result1', 'q4', 'opt9'),
    ('ans3', 'wa', true, 'result1', 'q5', null),

    ('ans4', 'i', false, 'result2', 'q1', 'opt2'),
    ('ans5', '50', false, 'result2', 'q4', 'opt10'),
    ('ans6', 'ga', false, 'result2', 'q5', null),

    ('ans7', 'a', true, 'result3', 'q1', 'opt1'),
    ('ans8', 'ka', true, 'result3', 'q2', 'opt5'),
    ('ans9', 'tabete', true, 'result3', 'q3', null),
    ('ans10', '46', true, 'result3', 'q4', 'opt9'),
    ('ans11', 'wa', true, 'result3', 'q5', null);
