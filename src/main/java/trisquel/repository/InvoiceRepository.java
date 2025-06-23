package trisquel.repository;

import trisquel.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface InvoiceRepository extends JpaRepository<Invoice, Long> {}
