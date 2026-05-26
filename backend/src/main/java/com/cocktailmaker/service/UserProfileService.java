package com.cocktailmaker.service;

import com.cocktailmaker.dto.ApiResponse;
import com.cocktailmaker.dto.FlavorProfileDto;
import com.cocktailmaker.dto.UserActivityDto;
import com.cocktailmaker.dto.UserProfileDto;
import org.springframework.data.domain.Page;

public interface UserProfileService {

    ApiResponse<UserProfileDto> getUserProfile(Long userId);

    ApiResponse<Page<UserActivityDto>> getUserActivities(Long userId, int page, int size);

    ApiResponse<FlavorProfileDto> getFlavorProfile(Long userId);
}
