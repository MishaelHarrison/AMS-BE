package AMS.repos;

import AMS.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where c.userData.id = ?1")
    Optional<Customer> findByUser(Long Id);

    Optional<Customer> findByPAN(Long PAN);
}
