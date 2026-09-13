package com.tu.goodsbuy.service;


import com.tu.goodsbuy.global.exception.file.FileTransferException;
import com.tu.goodsbuy.global.exception.file.NotImageFileException;
import com.tu.goodsbuy.global.exception.profile.*;
import com.tu.goodsbuy.model.dto.MemberProfile;
import com.tu.goodsbuy.repository.ProfileRepository;
import com.tu.goodsbuy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;


    @Transactional
    public void makeMemberProfile(String userId, String nickname) {
        if (profileRepository.makeMemberProfile(userRepository.findUserNoById(userId), nickname) == 0) {
            throw new MakeMemberProfileException();
        }
    }


    @Transactional(readOnly = true)
    public MemberProfile getMemberProfileByUserNo(Long userNo) {
        return profileRepository.getMemberProfileByUserNo(userNo).orElseThrow(GetProfileException::new);
    }

    @Transactional
    public void updateNickname(Long userNo, String nickname) {
        if (profileRepository.updateNickname(userNo, nickname) == 0) {
            throw new NicknameDuplicateException();
        }
    }


    public String uploadSaveImageAndGetIdentifier(String imagePath, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new NotImageFileException();
        }
        try (var input = file.getInputStream()) {
            if (javax.imageio.ImageIO.read(input) == null) {
                throw new NotImageFileException();
            }
        } catch (IOException e) {
            throw new NotImageFileException();
        }
        String original = Objects.toString(file.getOriginalFilename(), "image")
                .replace('\\', '/');
        String name = original.substring(original.lastIndexOf('/') + 1)
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        if (!name.toLowerCase(java.util.Locale.ROOT).matches(".*\\.(png|jpe?g|gif|bmp)$")) {
            throw new NotImageFileException();
        }
        String identifier = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                + "/" + UUID.randomUUID() + "_" + name;
        Path target = Path.of(imagePath).toAbsolutePath().normalize().resolve(identifier);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new FileTransferException();
        }
        return identifier;
    }

    public void deleteImage(String imagePath, String imageUrl) {
        if (imageUrl == null) return;
        Path root = Path.of(imagePath).toAbsolutePath().normalize();
        Path target = root.resolve(imageUrl).normalize();
        if (!target.startsWith(root) || target.equals(root)) {
            throw new IllegalArgumentException("Image path is outside upload directory");
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Failed to delete image: {}", target, e);
        }
    }

    @Transactional
    public void setEmailVerificationStatus(Long userNo) {
        if (profileRepository.setEmailVerificationStatus(userNo) == 0) {
            throw new EmailStatusUpdateException();
        }
    }

    @Transactional
    public void setEmailProfileByUserNo(String email, Long userNo) {
        if (profileRepository.setEmailProfileByUserNo(email, userNo) == 0) {
            throw new EmailStatusUpdateException();
        }
    }

    @Transactional
    public void setLocationByUserNo(String location, Long userNo) {
        if (profileRepository.setLocationByUserNo(location, userNo) == 0) {
            throw new LocationUpdateException();
        }
    }

    @Transactional
    public void setIntroductionByUserNo(String introduction, Long userNo) {
        if (profileRepository.setIntroductionByUserNo(introduction, userNo) == 0) {
            throw new IntroductionUpdateException();
        }
    }

    @Transactional
    public void setImgUrlByUserNo(String imgURL, Long userNo) { // chat_room imageUrl도 업데이트


        if (profileRepository.setImgUrlByUserNo(imgURL, userNo) == 0) {
            throw new GetProfileException();
        }
    }


    @Transactional(readOnly = true)
    public String getNicknameByUserNo(Long userNo) {
        return profileRepository.getNicknameByUserNo(userNo);
    }
}

