package com.safebank.api.repository;

import com.safebank.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.member.id = :memberId")
    List<Account> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT a FROM Account a WHERE a.member.email = :email")
    List<Account> findByMemberEmail(@Param("email") String email);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.status = 'ACTIVE'")
    BigDecimal calculateTotalBalance();
}
