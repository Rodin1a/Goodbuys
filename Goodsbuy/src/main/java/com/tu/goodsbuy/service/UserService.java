package com.tu.goodsbuy.service;


import com.tu.goodsbuy.global.exception.user.DuplicatedLoginIdException;
import com.tu.goodsbuy.global.exception.user.MakeMemberException;
import com.tu.goodsbuy.model.dto.MemberUser;
import com.tu.goodsbuy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwords =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();


    @Transactional
    public MemberUser doLogin(String userId, String userPwd) {
        MemberUser member = userRepository.getMemberUserById(userId).orElseThrow(DuplicatedLoginIdException::new);
        String stored = member.getUserPwd();
        if (userPwd == null || stored == null) throw new DuplicatedLoginIdException();
        if (stored.startsWith("$2")) {
            if (!passwords.matches(userPwd, stored)) throw new DuplicatedLoginIdException();
        } else {
            // Legacy accounts are upgraded after an exact, successful password check.
            if (!java.security.MessageDigest.isEqual(stored.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    userPwd.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                throw new DuplicatedLoginIdException();
            }
            if (userRepository.updatePassword(member.getUserNo(), stored, passwords.encode(userPwd)) != 1) {
                throw new DuplicatedLoginIdException();
            }
        }
        return new MemberUser(member.getUserNo(), member.getUserId(), null);
    }


    @Transactional(readOnly = true)
    public boolean isValidRegister(String userId, String nickname) {
        return !isUserIdExists(userId) && !isNicknameExists(nickname);
    }


    @Transactional
    public void makeMemberUser(String userId, String userPwd) {
        if (userRepository.makeMemberUser(userId, passwords.encode(userPwd)) == 0) {
            throw new MakeMemberException();
        }
    }

    @Transactional(readOnly = true)
    public boolean isUserIdExists(String id) {
        return userRepository.isUserIdExists(id) == 1;
    }

    @Transactional(readOnly = true)
    public boolean isNicknameExists(String nickname) {
        return userRepository.isNicknameExists(nickname) == 1;
    }


}

