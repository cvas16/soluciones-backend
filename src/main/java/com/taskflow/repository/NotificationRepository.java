package com.taskflow.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.taskflow.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
	List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    
    long countByRecipientIdAndIsReadFalse(Long recipientId);
}
