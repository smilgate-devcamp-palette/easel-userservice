package org.palette.easeluserservice.dto.response;

import java.time.LocalDateTime;

public record RetrieveUserResponse(
        Long id,
        String backgroundImagePath,
        String profileImagePath,
        String nickname,
        String username,
        String introduce,
        String websitePath,
        LocalDateTime joinedAt,
        Long followingCount,
        Long followerCount
) {
}
