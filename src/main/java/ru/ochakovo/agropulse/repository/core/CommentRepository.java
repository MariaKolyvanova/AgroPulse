package ru.ochakovo.agropulse.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ochakovo.agropulse.entity.data.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByReport_Id(Long reportId);
}
