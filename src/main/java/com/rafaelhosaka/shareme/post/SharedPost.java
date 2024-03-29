package com.rafaelhosaka.shareme.post;

import com.rafaelhosaka.shareme.post.BasePost;
import com.rafaelhosaka.shareme.post.Post;
import com.rafaelhosaka.shareme.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "post")
public class SharedPost extends BasePost {

    @DBRef
    private Post sharedPost;
}
