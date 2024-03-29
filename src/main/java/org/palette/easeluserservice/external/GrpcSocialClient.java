package org.palette.easeluserservice.external;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.palette.aop.InternalErrorLogging;
import org.palette.easeluserservice.persistence.User;
import org.palette.exception.BaseException;
import org.palette.exception.ExceptionType;
import org.palette.grpc.*;
import org.palette.passport.component.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class GrpcSocialClient {

    @GrpcClient("social-service")
    private GSocialServiceGrpc.GSocialServiceBlockingStub gSocialServiceBlockingStub;

    @InternalErrorLogging
    public GCreateUserResponse createSocialUser(final User user) {
        try {
            return gSocialServiceBlockingStub.createUser(
                    GCreateUserRequest.newBuilder()
                            .setId(user.getId())
                            .setUsername(user.getUsername())
                            .setImagePath(user.getProfile().staticContentPath().profileImagePath())
                            .setNickname(user.getProfile().nickname())
                            .setIsActive(user.getIsActivated())
                            .build());
        } catch (final StatusRuntimeException e) {
            throw new BaseException(ExceptionType.USER_500_000001);
        }
    }

    @InternalErrorLogging
    public GLoadUserFollowInformationResponse loadUserFollowShipCount(
            final UserInfo userInfo,
            final User user,
            final String integrityKey
    ) {
        if (user == null) {
            return retrieveFollowShipMe(userInfo, integrityKey);
        }
        return retrieveFollowShipOther(user, integrityKey);
    }

    @InternalErrorLogging
    private GLoadUserFollowInformationResponse retrieveFollowShipMe(
            final UserInfo userInfo,
            final String integrityKey
    ) {
        try {
            return gSocialServiceBlockingStub.loadUserFollowInformation(
                    GLoadUserFollowInformationRequest.newBuilder()
                            .setPassport(
                                    GPassport.newBuilder()
                                            .setId(userInfo.id())
                                            .setEmail(userInfo.email())
                                            .setUsername(userInfo.username())
                                            .setNickname(userInfo.nickname())
                                            .setRole(userInfo.role())
                                            .setAccessedAt(userInfo.isActivated().toString())
                                            .setCreatedAt(userInfo.createdAt())
                                            .setDeletedAt(userInfo.deletedAt())
                                            .setIntegrityKey(integrityKey)
                                            .build()
                            ).build()
            );
        } catch (final StatusRuntimeException e) {
            throw new BaseException(ExceptionType.USER_500_000001);
        }
    }

    @InternalErrorLogging
    private GLoadUserFollowInformationResponse retrieveFollowShipOther(
            final User user,
            final String integrityKey
    ) {
        try {
            return gSocialServiceBlockingStub.loadUserFollowInformation(
                    GLoadUserFollowInformationRequest.newBuilder()
                            .setPassport(
                                    GPassport.newBuilder()
                                            .setId(user.getId())
                                            .setEmail(user.getEmail())
                                            .setUsername(user.getUsername())
                                            .setNickname(user.getProfile().nickname())
                                            .setRole(user.getRole().toString())
                                            .setAccessedAt(user.getAccessedAt().toString())
                                            .setCreatedAt(user.getCreatedAt().toString())
                                            .setDeletedAt(user.getStringDeletedAt())
                                            .setIntegrityKey(integrityKey)
                                            .build()
                            ).build()
            );

        } catch (final StatusRuntimeException e) {
            throw new BaseException(ExceptionType.USER_500_000001);
        }
    }
}
