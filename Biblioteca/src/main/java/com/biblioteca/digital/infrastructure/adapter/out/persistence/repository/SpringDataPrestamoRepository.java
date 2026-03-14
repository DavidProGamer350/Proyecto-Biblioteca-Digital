// com.biblioteca.digital.infrastructure.adapter.out.persistence.repository.SpringDataPrestamoRepository.java
package com.biblioteca.digital.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.biblioteca.digital.infrastructure.adapter.out.persistence.entity.PrestamoEntity;


@Repository
public interface SpringDataPrestamoRepository extends JpaRepository<PrestamoEntity, Long> {
}
