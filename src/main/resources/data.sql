/* ============================
   SEED DATA – CMS Service Test Data
   ============================ */

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
