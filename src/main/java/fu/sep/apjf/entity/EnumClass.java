package fu.sep.apjf.entity;

/**
 * A container class for all enums related to the exam management system.
 */
public class EnumClass {

    public enum Level {
        N5,
        N4,
        N3,
        N2,
        N1
    }

    public enum Status {
        DRAFT,       // đang biên soạn
        PENDING,     // staff gửi duyệt
        PUBLISHED,   // manager duyệt
        REJECTED,    // manager từ chối
        ARCHIVED     // ngưng sử dụng
    }

    /**
     * Enum representing types of questions in an exam.
     */
    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER
    }

    /**
     * Enum representing the scope of an exam (e.g., global, school, class).
     */
    public enum ExamScopeType {
        GLOBAL, SCHOOL, CLASS
    }

    /**
     * Enum representing the status of an exam.
     */
    public enum ExamStatus {
        PASSED, FAILED
    }
}
