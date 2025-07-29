package fu.sep.apjf.entity;

public class EnumClass {

    public enum Level {
        N5, N4, N3, N2, N1
    }

    public enum Status {
        DRAFT,       // đang biên soạn
        PENDING,     // staff gửi duyệt
        PUBLISHED,   // manager duyệt
        REJECTED,    // manager từ chối
        ARCHIVED     // ngưng sử dụng
    }

    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, WRITING
    }

    public enum ExamScopeType {
        COURSE, CHAPTER, UNIT
    }

    public enum ExamStatus {
        PASSED, FAILED, IN_PROGRESS
    }

    public enum QuestionScope {
        KANJI, VOCAB, GRAMMAR, LISTENING, READING, WRITING
    }

    public enum MaterialType {
        KANJI, GRAMMAR, VOCAB, LISTENING, READING, WRITING
    }

    public enum PathStatus {
        FINISHED, STUDYING
    }
}
