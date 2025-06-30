/* ============================
   SEED DATA – Japanese Courses
   ============================ */

/* ---------- COURSE ---------- */
INSERT INTO course (id, title, topic, description, estimated_duration,
                    level, creator_id, requirements, image, status,
                    created_at, updated_at)
VALUES (1, 'Japanese N5 Foundations', 'jp-n5',
        'Starter course for JLPT N5 – kana, greetings, basic grammar.',
        20, 'BEGINNER', 'staff01',
        'Laptop + headphone', '/img/n5.png', 'PUBLISHED',
        NOW(), NOW()),

       (2, 'Everyday Japanese – N4', 'jp-n4',
        'Conversational Japanese, essential grammar and vocab for JLPT N4.',
        30, 'INTERMEDIATE', 'staff02',
        'Completed N5 or equivalent', '/img/n4.png', 'PUBLISHED',
        NOW(), NOW());

/* ---------- CHAPTER ---------- */
-- 3 chương cho course 1
INSERT INTO chapter (id, title, description, created_at, order_number,
                     status, prerequisite_chapter_id, course_id)
VALUES (1, 'Hiragana', 'Learn all 46 basic hiragana characters', NOW(), 1, 'PUBLISHED', NULL, 1),
       (2, 'Katakana', 'Learn basic katakana for loan-words', NOW(), 2, 'PUBLISHED', NULL, 1),
       (3, 'Basic Greetings', 'Common phrases for daily greetings', NOW(), 3, 'PUBLISHED', NULL, 1),

-- 3 chương cho course 2
       (4, 'Keigo Basics', 'Introduction to polite Japanese forms', NOW(), 1, 'PUBLISHED', NULL, 2),
       (5, 'Te-Form Mastery', 'All uses of the て-form in N4 grammar', NOW(), 2, 'PUBLISHED', NULL, 2),
       (6, 'Daily Conversations', 'Role-play dialogues for daily life', NOW(), 3, 'PUBLISHED', NULL, 2);

/* ---------- UNIT ---------- */
-- helper comment: 4 unit cho mỗi chapter (Vocabulary, Grammar, Listening, Quiz)

INSERT INTO unit (id, title, description, created_at, updated_at,
                  order_number, prerequisite_unit_id, chapter_id)
VALUES
    /* Chapter 1 – Hiragana */
    (1, 'Vocabulary', 'Hiragana reading drill', NOW(), NOW(), 1, NULL, 1),
    (2, 'Grammar', 'Particles は・が・を intro', NOW(), NOW(), 2, NULL, 1),
    (3, 'Listening Practice', 'Audio: native hiragana words', NOW(), NOW(), 3, NULL, 1),
    (4, 'Quiz', 'Hiragana multiple-choice test', NOW(), NOW(), 4, NULL, 1),

    /* Chapter 2 – Katakana */
    (5, 'Vocabulary', 'Katakana reading drill', NOW(), NOW(), 1, NULL, 2),
    (6, 'Grammar', 'Loan-word usage', NOW(), NOW(), 2, NULL, 2),
    (7, 'Listening Practice', 'Audio: katakana words', NOW(), NOW(), 3, NULL, 2),
    (8, 'Quiz', 'Katakana quiz', NOW(), NOW(), 4, NULL, 2),

    /* Chapter 3 – Basic Greetings */
    (9, 'Vocabulary', 'Common greeting words', NOW(), NOW(), 1, NULL, 3),
    (10, 'Grammar', 'Using です／ます', NOW(), NOW(), 2, NULL, 3),
    (11, 'Listening Practice', 'Greeting dialogues audio', NOW(), NOW(), 3, NULL, 3),
    (12, 'Quiz', 'Greetings comprehension test', NOW(), NOW(), 4, NULL, 3),

    /* Chapter 4 – Keigo Basics */
    (13, 'Vocabulary', 'Honorific verbs list', NOW(), NOW(), 1, NULL, 4),
    (14, 'Grammar', '～させていただく constructions', NOW(), NOW(), 2, NULL, 4),
    (15, 'Listening Practice', 'Keigo phone calls', NOW(), NOW(), 3, NULL, 4),
    (16, 'Quiz', 'Keigo basics quiz', NOW(), NOW(), 4, NULL, 4),

    /* Chapter 5 – Te-Form Mastery */
    (17, 'Vocabulary', 'Verbs te-form table', NOW(), NOW(), 1, NULL, 5),
    (18, 'Grammar', 'Sequential actions with て', NOW(), NOW(), 2, NULL, 5),
    (19, 'Listening Practice', 'Te-form conversations', NOW(), NOW(), 3, NULL, 5),
    (20, 'Quiz', 'Te-form mastery test', NOW(), NOW(), 4, NULL, 5),

    /* Chapter 6 – Daily Conversations */
    (21, 'Vocabulary', 'Daily life phrases', NOW(), NOW(), 1, NULL, 6),
    (22, 'Grammar', 'Casual vs polite switch', NOW(), NOW(), 2, NULL, 6),
    (23, 'Listening Practice', 'Daily convo audio', NOW(), NOW(), 3, NULL, 6),
    (24, 'Quiz', 'Conversation role-play quiz', NOW(), NOW(), 4, NULL, 6);