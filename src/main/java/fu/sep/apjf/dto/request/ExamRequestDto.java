package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

//tao hoac cap nhat bai thi
public record ExamRequestDto(
        @NotBlank(message = "ID bài kiểm tra không được để trống")
        String id,
        @NotBlank(message = "Tên bài kiểm tra không được để trống")
        String title,
        String description,
        @NotNull(message = "Thời gian kiểm tra không được để trống")
        Double duration,
        @NotNull(message = "Dạng bài kiểm tra không được để trống")
        EnumClass.ExamScopeType examScopeType,
        String courseId,
        String chapterId,
        String unitId,
        List<String>questionIds
) {}