//package fu.sep.apjf.mapper;
//
//import fu.sep.apjf.dto.response.ExamResultSummaryDto;
//import fu.sep.apjf.entity.ExamResult;
//
//import java.util.List;
//
//public final class ExamHistoryMapper {
//
//    private ExamHistoryMapper() {
//    }
//
//    public static ExamResultSummaryDto toDto(ExamResult examResult) {
//        if (examResult == null) {
//            return null;
//        }
//
//        int correctAnswers = (int) examResult.getDetails().stream()
//                .filter(detail -> detail.getIsCorrect())
//                .count();
//
////        return new ExamResultSummaryDto(
////                examResult.getId(),
////                examResult.getExam().getId(),
////                examResult.getExam().getTitle(),
////                examResult.getExam().getDescription(),
////                examResult.getExam().getExamScopeType(),
////                examResult.getStartedAt(),
////                examResult.getSubmittedAt(),
////                examResult.getScore(),
////                examResult.getStatus(),
////                examResult.getExam().getCourse() != null ? examResult.getExam().getCourse().getId() : null,
////                examResult.getExam().getCourse() != null ? examResult.getExam().getCourse().getTitle() : null,
////                examResult.getExam().getChapter() != null ? examResult.getExam().getChapter().getId() : null,
////                examResult.getExam().getChapter() != null ? examResult.getExam().getChapter().getTitle() : null,
////                examResult.getExam().getUnit() != null ? examResult.getExam().getUnit().getId() : null,
////                examResult.getExam().getUnit() != null ? examResult.getExam().getUnit().getTitle() : null,
////                examResult.getExam().getQuestions().size(),
////                correctAnswers,
////                examResult.getExam().getDuration()
////        );
////    }
//
////    public static List<ExamResultSummaryDto> toDtoList(List<ExamResult> examResults) {
////        if (examResults == null) {
////            return List.of();
////        }
////        return examResults.stream()
////                .map(ExamHistoryMapper::toDto)
////                .toList();
////    }
//}
