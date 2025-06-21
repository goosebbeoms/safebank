package com.safebank.api.repository;

import com.safebank.api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t" +
            " LEFT JOIN FETCH t.fromAccount" +
            " LEFT JOIN FETCH t.toAccount" +
            " WHERE t.fromAccount.accountNumber = :accountNumber" +
            " OR t.toAccount.accountNumber = :accountNumber" +
            " ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountNumber(@Param("accountNumber") String accountNumber, Pageable pageable);

    @Query("SELECT t FROM Transaction t" +
            " LEFT JOIN FETCH t.fromAccount" +
            " LEFT JOIN FETCH t.toAccount" +
            " WHERE t.fromAccount.id = :accountId" +
            " OR t.toAccount.id = :accountId" +
            " ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);
}
