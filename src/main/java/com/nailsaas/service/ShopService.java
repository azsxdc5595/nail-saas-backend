
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.CreateShopRequest;
import com.nailsaas.domain.GetShopInfoReponse;
import com.nailsaas.domain.JoinShopRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.Shop;
import com.nailsaas.entity.ShopInviteCode;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.enums.InviteCodeStatusEnum;
import com.nailsaas.enums.ManicuristStatusEnum;
import com.nailsaas.enums.RoleEnum;
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
    private ShopInviteCodeRepository shopInviteCodeRepository;
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private Generate generate;

    // 新增店家
    @Transactional
    public void createShop(CreateShopRequest req) {
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
        Manicurist manicurist = new Manicurist();
        manicurist.setCode(generate.generateUuid());
        manicurist.setShopId(shop.getId());
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
    public UserAccount getCurrentUser() {
        return userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));
    }

    // 查詢
    public GetShopInfoReponse me() {
        UserAccount userAccount = getCurrentUser();
        Optional<Shop> optionalShop = Optional.ofNullable(shopRepository.findByUserId(userAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到店家資料")));
        Shop shop = optionalShop.get();
        GetShopInfoReponse reponse = GetShopInfoReponse.builder().shopName(shop.getShopName()).phone(shop.getPhone()).description(shop.getDescription()).address(shop.getAddress()).build();
        return reponse;
    }
    
    // 新增一組邀請碼
    @Transactional
    public String invite() {
        UserAccount userAccount = getCurrentUser();
        Shop shop = shopRepository.findByUserId(userAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到店家資料"));

        Optional<ShopInviteCode> activeInviteCode = shopInviteCodeRepository.findByShopIdAndStatus(
                shop.getId(),
                InviteCodeStatusEnum.ACTIVE.getCode()
        );

        LocalDateTime now = LocalDateTime.now();
        if (activeInviteCode.isPresent()) {

            ShopInviteCode inviteCode = activeInviteCode.get();

            if (inviteCode.getExpireTime().isAfter(now)) {
                return inviteCode.getCode();
            }

            // 已過期，更新狀態
            inviteCode.setStatus(InviteCodeStatusEnum.EXPIRED.getCode());
            inviteCode.setUpdateTime(now);
            shopInviteCodeRepository.save(inviteCode);
        }

        String uuid = generate.generateUuid();
        ShopInviteCode shopInviteCode = new ShopInviteCode();
        shopInviteCode.setCode(uuid);
        shopInviteCode.setShopId(shop.getId());
        shopInviteCode.setExpireTime(now.plusMinutes(5));
        shopInviteCode.setStatus(InviteCodeStatusEnum.ACTIVE.getCode());
        shopInviteCode.setCreateTime(now);
        shopInviteCodeRepository.save(shopInviteCode);
        return uuid;
    }
    
    @Transactional
    public void joinShop(JoinShopRequest req) {

        UserAccount userAccount = userAccountRepository
                .findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        // UX 提示：一個使用者只能加入一間店家
        if (manicuristRepository.existsByUserId(userAccount.getId())) {
            throw new RuntimeException("已經是美甲師");
        }

        ShopInviteCode invite = shopInviteCodeRepository.findByCode(req.getInviteCode())
                .orElseThrow(() -> new RuntimeException("邀請碼不存在"));

        if (!InviteCodeStatusEnum.ACTIVE.getCode().equals(invite.getStatus())) {
            throw new RuntimeException("邀請碼不可使用");
        }

        if (invite.getExpireTime() != null &&
            invite.getExpireTime().isBefore(LocalDateTime.now())) {

            invite.setStatus(InviteCodeStatusEnum.EXPIRED.getCode());
            invite.setUpdateTime(LocalDateTime.now());
            shopInviteCodeRepository.save(invite);

            throw new RuntimeException("邀請碼已過期");
        }

        int updated = shopInviteCodeRepository.useInviteCode(
                req.getInviteCode(),
                InviteCodeStatusEnum.ACTIVE.getCode(),
                InviteCodeStatusEnum.USED.getCode()
        );

        if (updated != 1) {
            throw new RuntimeException("邀請碼已被使用");
        }

        Manicurist manicurist = new Manicurist();
        manicurist.setCode(generate.generateUuid());
        manicurist.setShopId(invite.getShopId());
        manicurist.setUserId(userAccount.getId());
        manicurist.setCreateTime(LocalDateTime.now());
        manicurist.setStatus(ManicuristStatusEnum.ACTIVE.getCode());
        manicurist.setDisplayName(userAccount.getUserName());
        manicurist.setRole(RoleEnum.STAFF.getCode());

        try {
            manicuristRepository.save(manicurist);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("已經是美甲師");
        }
    }

}
