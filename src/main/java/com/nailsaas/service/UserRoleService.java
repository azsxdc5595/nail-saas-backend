package com.nailsaas.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.GetUserRolesResponse;
import com.nailsaas.entity.UserRole;
import com.nailsaas.enums.SystemRoleCodeEnum;
import com.nailsaas.repository.UserRoleRepository;
import com.nailsaas.domain.UserRoleId;

@Service
public class UserRoleService {

    private static final Integer ACTIVE = 1;

    private final PermissionCheckService permissionCheckService;
    private final UserRoleRepository userRoleRepository;

    public UserRoleService(PermissionCheckService permissionCheckService,
            UserRoleRepository userRoleRepository) {
        this.permissionCheckService = permissionCheckService;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public GetUserRolesResponse getCurrentUserRoles() {
        Long userId = permissionCheckService.getCurrentUser().getId();
        List<String> roles = userRoleRepository
                .findByUserIdAndIsActiveOrderByRoleCodeAsc(userId, ACTIVE)
                .stream()
                .map(UserRole::getRoleCode)
                .toList();

        return GetUserRolesResponse.builder()
                .roles(roles)
                .build();
    }

    @Transactional(readOnly = true)
    public List<String> getActiveRoleCodes(String userCode) {
        return userRoleRepository.findActiveRoleCodesByUserCode(userCode, ACTIVE);
    }

    @Transactional
    public void grantRole(Long userId, SystemRoleCodeEnum roleCode) {
        String roleName = roleCode.getCode();
        UserRoleId id = new UserRoleId(userId, roleName);
        LocalDateTime now = LocalDateTime.now();

        userRoleRepository.findById(id).ifPresentOrElse(existingRole -> {
            if (!Objects.equals(existingRole.getIsActive(), ACTIVE)) {
                existingRole.setIsActive(ACTIVE);
                existingRole.setUpdateTime(now);
                userRoleRepository.save(existingRole);
            }
        }, () -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleCode(roleName);
            userRole.setIsActive(ACTIVE);
            userRole.setCreateTime(now);
            userRole.setUpdateTime(now);
            userRoleRepository.save(userRole);
        });
    }
}
