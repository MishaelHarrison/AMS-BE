package AMS.repos;

import AMS.models.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.id = ?1 and a.customer.userData.id = ?2")
    Optional<Account> findUsersAccount(Long Id, Long userId);
    
}
