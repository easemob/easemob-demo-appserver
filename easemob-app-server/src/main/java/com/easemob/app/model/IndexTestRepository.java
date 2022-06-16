package com.easemob.app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexTestRepository extends JpaRepository<IndexTest, Long> {
//public interface IndexTestRepository {

}
