package fu.sep.apjf.entity;

public class EnumClass {

    public enum Level {
        N5, N4, N3, N2, N1
    }

    public enum Status {// staff gửi duyệt
        ACTIVE,   // manager duyệt// manager từ chối
        INACTIVE     // ngưng sử dụng
    }

    public enum ExamType {
        MULTIPLE_CHOICE, WRITING
    }

    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, WRITING
    }

    public enum ExamScopeType {
        COURSE, CHAPTER, UNIT
    }

    public enum ExamStatus {
        PASSED, FAILED, IN_PROGRESS, SUBMITTED
    }

    public enum QuestionScope {
        KANJI, VOCAB, GRAMMAR, LISTENING, READING, WRITING
    }

    public enum MaterialType {
        KANJI, GRAMMAR, VOCAB, LISTENING, READING, WRITING
    }

    public enum PathStatus {
        FINISHED, STUDYING, PENDING
    }

    public enum GradingMethod {
        MANUAL, AI
    }

}