package fu.sep.cms.service;

import fu.sep.cms.entity.Slot;

public interface SlotService {
    Slot createSlot(Slot slot);
    Slot updateSlot(Long id, Slot slot);
    void deleteSlot(Long id);
    Slot getSlotById(Long id);
    Slot getSlotDetail(Long slotId);
}
