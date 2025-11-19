package chatterbox.services;

import chatterbox.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final PostRepository postRepository;
    public void like(Long postId){
       postRepository.increaseLike(postId);
    }
    public void dislike(Long postId){
        postRepository.decreaseLike(postId);
    }
    public void sharePost(Long postId){
        postRepository.increaseShareCount(postId);
    }
}
