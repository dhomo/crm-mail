package dhomo.crmmail.api.lead.leadStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadStatusRepository extends JpaRepository<LeadStatus, Long> {


    /**
     *
     * @return статус для новых заявок
     */
    default LeadStatus findNew(){
        return findFirstByOrderByOrderAsc();
    }
    LeadStatus findFirstByOrderByOrderAsc();


    /**
     *
     * @return статус для Отказа
     */
    default LeadStatus findReject(){
        return findFirstByOrderByOrderDesc();
    }
    LeadStatus findFirstByOrderByOrderDesc();
}