package fu.sep.apjf.dto;

import fu.sep.apjf.entity.EnumClass;
import lombok.Data;

import java.util.List;

@Data
public class CreateExamDto {
    private String title;
    private String description;
    private Integer duration;
    private EnumClass.ExamScopeType examScopeType;
    private List<String> questionIds;
}
