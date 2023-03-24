package com.example.instaclone.user.service;

import com.example.instaclone.jwt.JwtUtil;
import com.example.instaclone.post.dto.PostResponseDto;
import com.example.instaclone.post.entity.Post;
import com.example.instaclone.post.repository.PostRepository;
import com.example.instaclone.user.dto.*;
import com.example.instaclone.user.entity.User;
import com.example.instaclone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final PostRepository postRepository;


    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String email = signupRequestDto.getEmail();

        Optional<User> foundUsername = userRepository.findByUsername(username);

        if (foundUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }

        Optional<User> foundEmail = userRepository.findByEmail(email);
        if (foundEmail.isPresent()) {
            throw new IllegalArgumentException("이메일이 중복됩니다");
        }

        User user = new User(username, password, email);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(
                user.getUsername()));
    }

    // 유저이메일 중복 ck
    @Transactional(readOnly = true)
    public void checkemail(CheckEmailRequestDto checkEmailRequestDto) {
        String email = checkEmailRequestDto.getEmail();
        Optional<User> findemail = userRepository.findByEmail(email);
        if (findemail.isPresent()) {
            throw new IllegalArgumentException("이메일이 중복됩니다");
        }
    }

    //유저이름 중복 ck
    @Transactional(readOnly = true)
    public void checkusername(CheckUsernameRequestDto checkUsernameRequestDto) {
        String username = checkUsernameRequestDto.getUsername();
        Optional<User> findusername = userRepository.findByUsername(username);
        if (findusername.isPresent()) {
            throw new IllegalArgumentException("유저이름이 중복됩니다");
        }
    }

    // 마이페이지 조회 (토큰o)
    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPage(Long userId, User currentUser) {
       User user =  userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user);  // List<Post> findByUserOrderByCreatedAtDesc(User user);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : posts) {
            PostResponseDto postResponseDto = new PostResponseDto();
            postResponseDto.setId(post.getId());
            postResponseDto.setImage(post.getImage());
            postResponseDto.setLikeCnt(post.getLikes().size());
            postResponseDto.setCommentCnt(post.getComments().size());
            postResponseDtos.add(postResponseDto);
        }
        return new MyPageResponseDto(user.getId(), user.getUsername(), posts.getImage(), postResponseDtos.size(), postResponseDtos);
    }
}// public class PostResponseDto {
//
//    private Long id;
//    private String image;
//    private int likeCnt;
//    private int commentCnt;
//
//    // getter, setter 생략
//}


