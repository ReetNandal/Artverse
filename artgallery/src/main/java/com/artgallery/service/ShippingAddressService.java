package com.artgallery.service;

import com.artgallery.model.ShippingAddress;
import com.artgallery.repository.ShippingAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;

    public ShippingAddressService(ShippingAddressRepository shippingAddressRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
    }

    public ShippingAddress getByUserId(Long userId) {
        return shippingAddressRepository.findByUserId(userId);
    }

    public ShippingAddress save(ShippingAddress shippingAddress) {
        return shippingAddressRepository.save(shippingAddress);
    }
}