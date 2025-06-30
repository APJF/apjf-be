package fu.sep.cms.entity;

public enum Status {
    DRAFT,       // đang biên soạn
    PENDING,     // staff gửi duyệt
    PUBLISHED,   // manager duyệt
    REJECTED,    // manager từ chối
    ARCHIVED     // ngưng sử dụng
}