package com.nailsaas.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.entity.Address;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.AddressRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * 解析完整地址，取得對應的縣市與行政區資料。
     *
     * @param fullAddress 完整地址
     * @return 資料庫中的行政區資料
     */
    @Transactional(readOnly = true)
    public Address parseAddress(String fullAddress) {

        // 檢查地址是否為空
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_INVALID);
        }

        // 移除地址前後空白
        String address = fullAddress.trim();

        // 擷取縣市及行政區
        Pattern pattern = Pattern.compile("^(.+?[縣市])(.+?[區鄉鎮市])");
        Matcher matcher = pattern.matcher(address);

        // 地址格式不符合預期
        if (!matcher.find()) {
            throw new BusinessException(ErrorCodeEnum.ADDRESS_INVALID);
        }

        // 取得縣市名稱，例如：台北市
        String city = matcher.group(1);

        // 取得行政區名稱，例如：大安區
        String district = matcher.group(2);

        // 查詢資料庫中的縣市及行政區
        return addressRepository.findByCityAndDistrict(city, district)
                .orElseThrow(() ->
                        new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND)
                );
    }
}