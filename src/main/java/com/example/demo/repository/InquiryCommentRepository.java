package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
}
