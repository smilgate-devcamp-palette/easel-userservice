package org.palette.easeluserservice.e2e.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.palette.easeluserservice.dto.request.VerifyEmailDuplicationRequest;
import org.palette.easeluserservice.dto.response.VerifyEmailDuplicationResponse;
import org.palette.easeluserservice.e2e.AcceptanceTestBase;
import org.palette.easeluserservice.persistence.User;
import org.palette.easeluserservice.persistence.UserJpaRepository;
import org.palette.easeluserservice.persistence.embed.Password;
import org.palette.easeluserservice.persistence.embed.Pin;
import org.palette.easeluserservice.persistence.embed.Profile;
import org.palette.easeluserservice.persistence.embed.StaticContentPath;
import org.palette.easeluserservice.persistence.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VerifyEmailDuplication extends AcceptanceTestBase {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void insertTemporaryUser() throws NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        Class<User> userClass = User.class;
        Constructor<User> constructor = userClass.getDeclaredConstructor(
                Long.class,
                String.class,
                String.class,
                Password.class,
                Profile.class,
                Pin.class,
                Role.class,
                Boolean.class,
                Boolean.class,
                LocalDateTime.class,
                LocalDateTime.class,
                LocalDateTime.class,
                LocalDateTime.class
        );

        constructor.setAccessible(true);

        User user = constructor.newInstance(
                1L,
                "diger@gmail.com",
                "",
                new Password("", passwordEncoder),
                new Profile(
                        "digerDisplayName1",
                        "",
                        new StaticContentPath(
                                "",
                                "",
                                ""
                        )
                ),
                new Pin("", ""),
                Role.NORMAL,
                true,
                true,
                null,
                null,
                null,
                null
        );

        userJpaRepository.save(user);
    }

    @Test
    @DisplayName("이메일 중복")
    void executePassCase() throws Exception {
        VerifyEmailDuplicationRequest request = new VerifyEmailDuplicationRequest(
                "diger@gmail.com"
        );

        executePost(
                "/verify-email",
                request
        ).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("이메일 중복 아닐 시")
    void executePassCase2() throws Exception {
        VerifyEmailDuplicationRequest request = new VerifyEmailDuplicationRequest(
                "diger1@gmail.com"
        );

        VerifyEmailDuplicationResponse response = new VerifyEmailDuplicationResponse(
                false
        );

        executePost(
                "/verify-email",
                request
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
