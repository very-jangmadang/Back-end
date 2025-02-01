package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findAllByInquiryId(Long inquiryId);
    List<InquiryComment> findByInquiry(Inquiry inquiry);
}
