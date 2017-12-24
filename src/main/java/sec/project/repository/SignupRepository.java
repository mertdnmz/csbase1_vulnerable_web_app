package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Signup;
import sec.project.domain.Account;

public interface SignupRepository extends JpaRepository<Signup, Long> {
    
    List<Signup> findByAccount(Account account);

}
