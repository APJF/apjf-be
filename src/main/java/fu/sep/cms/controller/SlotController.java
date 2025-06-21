package fu.sep.cms.controller;

import fu.sep.cms.entity.Slot;
import fu.sep.cms.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    // 1. Thêm mới
    @PostMapping
    public ResponseEntity<Slot> create(@RequestBody Slot slot) {
        Slot saved = slotService.createSlot(slot);
        return ResponseEntity.status(201).body(saved);
    }

    // 2. Sửa
    @PutMapping("/{id}")
    public ResponseEntity<Slot> update(
            @PathVariable Long id,
            @RequestBody Slot slot
    ) {
        Slot updated = slotService.updateSlot(id, slot);
        return ResponseEntity.ok(updated);
    }

    // 3. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }

    // (Tùy chọn) Xem chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<Slot> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(slotService.getSlotById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Slot> getLessonDetail(@PathVariable("id") Long id) {
        Slot slot = slotService.getSlotDetail(id);
        return ResponseEntity.ok(slot);
    }
}
