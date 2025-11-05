package com.example.demo.repository;

import com.example.demo.model.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecordRepository extends JpaRepository<TaskRecord, Long> {
}
