package fu.sep.cms.service.impl;

import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Slot;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.SlotRepository;
import fu.sep.cms.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public Slot createSlot(Slot slot) {
        // Kiểm tra chapter tồn tại
        Chapter ch = chapterRepository.findById(slot.getChapter().getId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        slot.setChapter(ch);
        slot.setCreatedAt(LocalDateTime.now());
        slot.setUpdatedAt(LocalDateTime.now());
        return slotRepository.save(slot);
    }

    @Override
    public Slot updateSlot(Long id, Slot incoming) {
        Slot s = slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        // Cập nhật các trường
        s.setTitle(incoming.getTitle());
        s.setDescription(incoming.getDescription());
        s.setOrderNumber(incoming.getOrderNumber());
        // Nếu đổi chapter:
        if (incoming.getChapter() != null) {
            Chapter ch = chapterRepository.findById(incoming.getChapter().getId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            s.setChapter(ch);
        }
        s.setUpdatedAt(LocalDateTime.now());
        return slotRepository.save(s);
    }

    @Override
    public void deleteSlot(Long id) {
        slotRepository.deleteById(id);
    }

    @Override
    public Slot getSlotWithMaterials(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
    }
}
