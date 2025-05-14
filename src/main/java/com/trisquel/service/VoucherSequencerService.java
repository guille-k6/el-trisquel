package com.trisquel.service;

import com.trisquel.model.VoucherSequencer;
import com.trisquel.repository.VoucherSequencerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoucherSequencerService {

    private final VoucherSequencerRepository voucherSequencerRepository;

    @Autowired
    public VoucherSequencerService(VoucherSequencerRepository voucherSequencerRepository) {
        this.voucherSequencerRepository = voucherSequencerRepository;
    }

    /**
     * Get the current sequence value
     *
     * @return the current sequence value or null if no sequence exists
     */
    public Long getCurrentSequence() {
        VoucherSequencer latestVoucher = voucherSequencerRepository.findLatestVoucher();
        if (latestVoucher == null) {
            return null;
        }
        return latestVoucher.getNumber();
    }

    /**
     * Set the sequence to a specific number
     *
     * @param number the number to set the sequence to
     * @return the new VoucherSequencer
     */
    public VoucherSequencer setSequence(Long number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        VoucherSequencer newVoucher = new VoucherSequencer(number);
        return voucherSequencerRepository.save(newVoucher);
    }
}
