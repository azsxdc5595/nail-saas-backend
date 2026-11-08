package com.nailsaas.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.NailPostImage;

public interface NailPostImageRepository extends JpaRepository<NailPostImage, Long> {

    List<NailPostImage> findAllByPostIdInOrderByPostIdAscSortOrderAscIdAsc(Collection<Long> postIds);

    List<NailPostImage> findAllByPostIdOrderBySortOrderAscIdAsc(Long postId);

    Optional<NailPostImage> findById(Long imageId);

    void deleteAllByPostId(Long postId);
}
