
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.ApplyShopRequest;
import com.nailsaas.domain.UpdateUserRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.Shop;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.enums.ManicuristStatusEnum;
import com.nailsaas.enums.RoleEnum;
import com.nailsaas.repository.EmailVerificationRepository;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.ShopInviteCodeRepository;
import com.nailsaas.repository.ShopRepository;
import com.nailsaas.repository.UserAccountRepository;
import com.nailsaas.util.Generate;
import com.nailsaas.util.SecurityUtil;

@Service
public class ShopService {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    
    @Autowired
    private ShopInviteCodeRepository shopInviteCodeRepository;
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private Generate generate;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MailService mailService;

    
    // 新增店家
    @Transactional
    public void apply(ApplyShopRequest req) {
        UserAccount userAccount = userAccountRepository
                .findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
        
        Optional<Shop> optionalShop = shopRepository.findByShopName(req.getShopName());

        if (optionalShop.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "店名已被使用");
        }
        Shop shop = new Shop();
        shop.setCode(generate.generateUuid());
        shop.setShopName(req.getShopName());
        shop.setAddressId(req.getAddressId());
        shop.setPhone(req.getPhone());
        shop.setDescription(req.getDescription());
        shop.setCreateTime(LocalDateTime.now());

        shopRepository.save(shop);
        optionalShop = shopRepository.findByShopName(req.getShopName());
        Long shopId = optionalShop.get().getId();
        Manicurist manicurist = new Manicurist();
        manicurist.setCode(generate.generateUuid());
        manicurist.setShopId(shopId);
        manicurist.setUserId(userAccount.getId());
        manicurist.setCreateTime(LocalDateTime.now());
        manicurist.setStatus(ManicuristStatusEnum.ACTIVE.getCode());
        manicurist.setDisplayName(userAccount.getUserName());
        manicurist.setRole(RoleEnum.OWNER.getCode());

        try {
            manicuristRepository.save(manicurist);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("已經是美甲師");
        }
    }

    // 查詢
    public UserAccount getUserinfo() {
        return userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));
    }

    // 註銷
    public void cancelAccount() {
        UserAccount user = userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        userAccountRepository.delete(user);
    }

    public void updateMe(UpdateUserRequest req) {

        UserAccount user = userAccountRepository
                .findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到使用者"));

        // 👉 只更新有傳的欄位（關鍵）
        if (req.getUserName() != null) {
            user.setUserName(req.getUserName());
        }

        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }

        user.setUpdateTime(LocalDateTime.now());

        userAccountRepository.save(user);
    }

}