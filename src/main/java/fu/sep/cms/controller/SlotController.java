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

    @PostMapping
    public ResponseEntity<Slot> create(@RequestBody Slot slot) {
        Slot saved = slotService.createSlot(slot);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Slot> update(
            @PathVariable Long id,
            @RequestBody Slot slot
    ) {
        Slot updated = slotService.updateSlot(id, slot);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Slot> getLessonDetail(@PathVariable("id") Long id) {
        Slot slot = slotService.getSlotWithMaterials(id);
        return ResponseEntity.ok(slot);
    }
}
