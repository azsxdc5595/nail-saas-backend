
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.nailsaas.domain.ApplyNailPostRequest;
import com.nailsaas.domain.GetNailPostDetailRequest;
import com.nailsaas.domain.GetNailPostDetailResponse;
import com.nailsaas.domain.NailPostImageData;
import com.nailsaas.domain.NailPostImageRequest;
import com.nailsaas.domain.RemoveNailPostRequest;
import com.nailsaas.domain.SearchNailPostData;
import com.nailsaas.domain.SearchNailPostRequest;
import com.nailsaas.domain.SearchNailPostResponse;
import com.nailsaas.domain.UpdateNailPostRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.NailPost;
import com.nailsaas.entity.NailPostImage;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.NailPostImageRepository;
import com.nailsaas.repository.NailPostRepository;
import com.nailsaas.util.SecurityUtil;

@Service
public class NailPostService {

    private final NailPostRepository nailPostRepository;
    private final NailPostImageRepository nailPostImageRepository;
    private final NailPostImageStorageService imageStorageService;
    private final PermissionCheckService permissionCheckService;

    NailPostService(NailPostRepository nailPostRepository, NailPostImageStorageService imageStorageService, PermissionCheckService permissionCheckService, NailPostImageRepository nailPostImageRepository) {
        this.nailPostRepository = nailPostRepository;
        this.imageStorageService = imageStorageService;
        this.permissionCheckService = permissionCheckService;
        this.nailPostImageRepository = nailPostImageRepository;
    }

    // 美甲師查看自己的作品貼文
    @Transactional(readOnly = true)
    public SearchNailPostResponse searchMyNailPost(SearchNailPostRequest req) {
        req.setUserCode(SecurityUtil.getCurrentUserCode());
        return executeNailPostSearch(req, null);
    }

    // 美甲師新增自己的作品貼文
    @Transactional
    public void addNailPost(ApplyNailPostRequest req) {
        var manicurist = permissionCheckService.getCurrentManicurist();
        NailPost nailPost = new NailPost();
        nailPost.setManicuristId(manicurist.getId());
        nailPost.setPrice(req.getPrice());
        nailPost.setDescription(req.getDescription());
        nailPost.setStyleCode(req.getStyleCode());
        nailPost.setSeasonCode(req.getSeasonCode());
        nailPost.setMainColorCode(req.getMainColorCode());
        nailPost.setEnabled(req.getEnabled() == null ? 1 : req.getEnabled());
        nailPost.setCreateTime(LocalDateTime.now());
        nailPost = nailPostRepository.save(nailPost);
        saveImages(nailPost.getId(), req.getImages(), manicurist.getCode());
    }

    // 美甲師移除自己的作品貼文
    @Transactional
    public void removeNailPost(RemoveNailPostRequest req) {
        NailPost nailPost = findOwnedPost(req.getPostId());
        List<String> imagePaths = nailPostImageRepository.findAllByPostIdOrderBySortOrderAscIdAsc(nailPost.getId())
                .stream().map(NailPostImage::getStoragePath).toList();
        nailPostRepository.delete(nailPost);
        registerAfterCommitCleanup(imagePaths);
    }

    // 查詢某一位美甲師的作品貼文
    @Transactional(readOnly = true)
    public SearchNailPostResponse searchNailPost(SearchNailPostRequest req) {
        return executeNailPostSearch(req, 1);
    }

    // 查看作品貼文詳細資訊
    @Transactional(readOnly = true)
    public GetNailPostDetailResponse detailNailPost(GetNailPostDetailRequest req) {

        NailPost nailPost = nailPostRepository
                .findByIdAndEnabled(req.getPostId(), 1)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NAIL_POST_NOT_FOUND));

        return toDetailResponse(nailPost);
    }

    // 美甲師查看自己的作品貼文明細（包含未啟用貼文）
    @Transactional(readOnly = true)
    public GetNailPostDetailResponse detailMyNailPost(GetNailPostDetailRequest req) {
        return toDetailResponse(findOwnedPost(req.getPostId()));
    }

    private GetNailPostDetailResponse toDetailResponse(NailPost nailPost) {
        GetNailPostDetailResponse response = new GetNailPostDetailResponse();

        response.setPostId(nailPost.getId());
        response.setManicuristId(nailPost.getManicuristId());
        response.setPrice(nailPost.getPrice());
        response.setDescription(nailPost.getDescription());
        response.setStyleCode(nailPost.getStyleCode());
        response.setSeasonCode(nailPost.getSeasonCode());
        response.setMainColorCode(nailPost.getMainColorCode());
        response.setEnabled(nailPost.getEnabled());
        response.setImages(nailPostImageRepository.findAllByPostIdOrderBySortOrderAscIdAsc(nailPost.getId())
                .stream().map(this::toImageData).collect(Collectors.toList()));

        return response;
    }

    @Transactional(readOnly = true)
    public ImageResponse loadImage(Long imageId) {
        NailPostImage image = nailPostImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NAIL_POST_NOT_FOUND));
        NailPost nailPost = nailPostRepository.findById(image.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NAIL_POST_NOT_FOUND));
        if (!Integer.valueOf(1).equals(nailPost.getEnabled())) {
            permissionCheckService.checkCurrentManicuristOwnership(nailPost.getManicuristId());
        }
        return new ImageResponse(imageStorageService.load(image.getStoragePath()), image.getContentType());
    }

    public record ImageResponse(Resource resource, String contentType) {}

    // 條件查詢作品貼文
    private SearchNailPostResponse executeNailPostSearch(SearchNailPostRequest req, Integer enabled) {

        Pageable pageable = req.toPageable();

        Page<NailPost> nailPostPage =
                nailPostRepository.search(
                    req.getUserCode(),
                    enabled,
                    req.getDisplayName(),
                    req.getDescription(),
                    req.getStyleCode(),
                    req.getSeasonCode(),
                    req.getMainColorCode(),
                    req.getMinPrice(),
                    req.getMaxPrice(),
                    pageable
                );

        List<SearchNailPostData> dataList = new ArrayList<>();

        List<NailPost> posts = nailPostPage.getContent();
        Map<Long, List<NailPostImage>> imagesByPost = loadImages(posts);
        for (NailPost nailPost : posts) {

            SearchNailPostData data = new SearchNailPostData();

            data.setPostId(nailPost.getId());
            data.setManicuristId(nailPost.getManicuristId());
            data.setPrice(nailPost.getPrice());
            data.setDescription(nailPost.getDescription());
            data.setStyleCode(nailPost.getStyleCode());
            data.setSeasonCode(nailPost.getSeasonCode());
            data.setMainColorCode(nailPost.getMainColorCode());
            List<NailPostImage> images = imagesByPost.getOrDefault(nailPost.getId(), List.of());
            images.stream().filter(image -> Integer.valueOf(1).equals(image.getIsCover())).findFirst()
                    .or(() -> images.stream().findFirst()).map(this::toImageData).ifPresent(data::setCoverImage);

            dataList.add(data);
        }

        SearchNailPostResponse response = new SearchNailPostResponse();

        response.setNailPostList(dataList);
        response.setTotalCount((int) nailPostPage.getTotalElements()
        );

        return response;
    }

    @Transactional
    public void updateNailPost(UpdateNailPostRequest req) {
        var manicurist = permissionCheckService.getCurrentManicurist();
        NailPost nailPost = findOwnedPost(req.getPostId(), manicurist);
        if (req.getPrice() != null) {
            nailPost.setPrice(req.getPrice());
        }
        if (req.getDescription() != null) {
            nailPost.setDescription(req.getDescription());
        }
        if (req.getStyleCode() != null) {
            nailPost.setStyleCode(req.getStyleCode());
        }
        if (req.getSeasonCode() != null) {
            nailPost.setSeasonCode(req.getSeasonCode());
        }
        if (req.getMainColorCode() != null) {
            nailPost.setMainColorCode(req.getMainColorCode());
        }
        if (req.getEnabled() != null) {
            nailPost.setEnabled(req.getEnabled());
        }
        nailPost.setUpdateTime(LocalDateTime.now());
        nailPostRepository.save(nailPost);
        if (Boolean.TRUE.equals(req.getClearImages()) && req.getImages() != null) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
        }
        if (req.getImages() != null || Boolean.TRUE.equals(req.getClearImages())) {
            List<String> oldImagePaths = nailPostImageRepository.findAllByPostIdOrderBySortOrderAscIdAsc(nailPost.getId())
                    .stream().map(NailPostImage::getStoragePath).toList();
            nailPostImageRepository.deleteAllByPostId(nailPost.getId());
            if (!Boolean.TRUE.equals(req.getClearImages())) {
                saveImages(nailPost.getId(), req.getImages(), manicurist.getCode());
            }
            registerAfterCommitCleanup(oldImagePaths);
        }
    }

    private NailPost findOwnedPost(Long postId) {
        return findOwnedPost(postId, permissionCheckService.getCurrentManicurist());
    }

    private NailPost findOwnedPost(Long postId, Manicurist manicurist) {
        NailPost nailPost = nailPostRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NAIL_POST_NOT_FOUND));
        permissionCheckService.checkManicuristOwnership(manicurist.getId(), nailPost.getManicuristId());
        return nailPost;
    }

    private void saveImages(Long postId, List<NailPostImageRequest> requests, String manicuristCode) {
        if (requests == null || requests.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<NailPostImage> images = new ArrayList<>();
        for (int index = 0; index < requests.size(); index++) {
            NailPostImageRequest request = requests.get(index);
            NailPostImageStorageService.StoredImage stored = imageStorageService.store(request.getFile(), manicuristCode);
            registerRollbackCleanup(stored);
            NailPostImage image = new NailPostImage();
            image.setPostId(postId);
            image.setStorageName(stored.storageName());
            image.setStoragePath(stored.storagePath());
            String originalName = request.getOriginalName() == null ? stored.originalName() : request.getOriginalName();
            if (originalName != null && originalName.length() > 255) {
                imageStorageService.delete(stored);
                throw new BusinessException(ErrorCodeEnum.REQUEST_INVALID);
            }
            image.setOriginalName(originalName);
            image.setContentType(stored.contentType());
            image.setFileSize(stored.fileSize());
            image.setSortOrder(request.getSortOrder() == null ? index + 1 : request.getSortOrder());
            image.setIsCover(request.getIsCover() == null ? 0 : request.getIsCover());
            image.setCreateTime(now);
            image.setUpdateTime(now);
            images.add(image);
        }
        nailPostImageRepository.saveAll(images);
    }

    private void registerRollbackCleanup(NailPostImageStorageService.StoredImage stored) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    imageStorageService.delete(stored);
                }
            }
        });
    }

    private void registerAfterCommitCleanup(List<String> storagePaths) {
        if (storagePaths.isEmpty() || !TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                storagePaths.forEach(imageStorageService::deleteStoredPath);
            }
        });
    }

    private Map<Long, List<NailPostImage>> loadImages(List<NailPost> posts) {
        if (posts.isEmpty()) {
            return Map.of();
        }
        List<Long> postIds = posts.stream().map(NailPost::getId).collect(Collectors.toList());
        return nailPostImageRepository.findAllByPostIdInOrderByPostIdAscSortOrderAscIdAsc(postIds).stream()
                .collect(Collectors.groupingBy(NailPostImage::getPostId, HashMap::new, Collectors.toList()));
    }

    private NailPostImageData toImageData(NailPostImage image) {
        NailPostImageData data = new NailPostImageData();
        data.setImageId(image.getId());
        data.setStorageName(image.getStorageName());
        data.setStoragePath(image.getStoragePath());
        data.setImageUrl("/nailPost/image/" + image.getId());
        data.setOriginalName(image.getOriginalName());
        data.setContentType(image.getContentType());
        data.setFileSize(image.getFileSize());
        data.setSortOrder(image.getSortOrder());
        data.setIsCover(image.getIsCover());
        return data;
    }
}
