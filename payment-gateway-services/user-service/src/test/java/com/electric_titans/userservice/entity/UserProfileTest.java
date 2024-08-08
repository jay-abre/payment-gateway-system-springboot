package com.electric_titans.userservice.entity;

import com.electric_titans.userservice.enums.KycStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class UserProfileTest {

    @Mock
    private User user;

    @Test
    void testAllArgsConstructor() {
        UserProfile userProfile = new UserProfile(0L, user, "address line 1", "address line 2", "city",
                "state", "country", "postal code", KycStatusEnum.PENDING, "ID Picture", Instant.ofEpochMilli(1), Instant.ofEpochMilli(1));
        assertEquals(userProfile);
    }

    @Test
    void testNoArgsConstructor() {
        UserProfile userProfile = new UserProfile();
        Assertions.assertInstanceOf(UserProfile.class, userProfile);
    }

    @Test
    void testSetters() {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        userProfile.setUser(user);
        userProfile.setAddressLine1("address line 1");
        userProfile.setAddressLine2("address line 2");
        userProfile.setCity("city");
        userProfile.setState("state");
        userProfile.setCountry("country");
        userProfile.setPostalCode("postal code");
        userProfile.setKycStatus(KycStatusEnum.PENDING);
        userProfile.setIdPicture("ID Picture");
        userProfile.setCreatedAt(Instant.ofEpochMilli(1));
        userProfile.setUpdatedAt(Instant.ofEpochMilli(1));
        assertEquals(userProfile);
    }

    void assertEquals(UserProfile userProfile) {
        Assertions.assertEquals(0L, userProfile.getId());
        Assertions.assertEquals(user, userProfile.getUser());
        Assertions.assertEquals("address line 1", userProfile.getAddressLine1());
        Assertions.assertEquals("address line 2", userProfile.getAddressLine2());
        Assertions.assertEquals("city", userProfile.getCity());
        Assertions.assertEquals("state", userProfile.getState());
        Assertions.assertEquals("country", userProfile.getCountry());
        Assertions.assertEquals("postal code", userProfile.getPostalCode());
        Assertions.assertEquals(KycStatusEnum.PENDING, userProfile.getKycStatus());
        Assertions.assertEquals("ID Picture", userProfile.getIdPicture());
        Assertions.assertEquals(Instant.ofEpochMilli(1), userProfile.getCreatedAt());
        Assertions.assertEquals(Instant.ofEpochMilli(1), userProfile.getUpdatedAt());
    }
}
