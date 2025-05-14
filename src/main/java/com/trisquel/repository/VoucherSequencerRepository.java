package com.trisquel.repository;

import com.trisquel.model.VoucherSequencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherSequencerRepository extends JpaRepository<VoucherSequencer, Long> {

    /**
     * Find the voucher with the highest number
     *
     * @return the voucher with the highest number or null if no voucher exists
     */
    @Query("SELECT v FROM VoucherSequencer v WHERE v.number = (SELECT MAX(vs.number) FROM VoucherSequencer vs)")
    VoucherSequencer findLatestVoucher();
}
