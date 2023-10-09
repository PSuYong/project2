package boardexample.myboard.domain.member;

import boardexample.myboard.domain.BaseTimeEntity;
import boardexample.myboard.domain.commnet.Comment;
import boardexample.myboard.domain.post.Post;
import boardexample.myboard.global.cache.CacheLogin;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Embeddable
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity implements Serializable {


    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id; //primary Key



    @Column(nullable = false, length = 30, unique = true)
    private String username;//아이디

    private String password;//비밀번호


    @Column(nullable = false, length = 30)
    private String name;//이름(실명)


    @Column(nullable = false, length = 30)
    private String nickName;//별명


    @Column(nullable = false, length = 30)
    private Integer age;//나이



    @Enumerated(STRING)
    @Column(nullable = false, length = 30)
    private Role role;//권한 -> USER, ADMIN


    @Column(length = 1000)
    private String refreshToken;//RefreshToken



    //== 회원탈퇴 -> 작성한 게시물, 댓글 모두 삭제 ==//
    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();





    //== 연관관계 메서드 ==//
    public void addPost(Post post){
        //post의 writer 설정은 post에서 함
        postList.add(post);
    }

    public void addComment(Comment comment){
        //comment의 writer 설정은 comment에서 함
        commentList.add(comment);
    }







    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateAge(int age){
        this.age = age;
    }


    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken(){
        this.refreshToken = null;
    }


    /**
     * 패스워드 암호화
     */
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }


    /**
     * 패스워드 일치하는지 확인
     * @param passwordEncoder 패스워드 인코더
     * @param checkPassword 검사할 비밀번호
     * @return
     */
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        return passwordEncoder.matches(checkPassword, getPassword());
    }


    //== 권한 부여 ==//
    public void addUserAuthority() {
        this.role = Role.USER;
    }


}
