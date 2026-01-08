package chatterbox.services;

import chatterbox.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    @Override
    public void like(Long postId){
       postRepository.increaseLike(postId);
    }
    @Override
    public void dislike(Long postId){
        postRepository.decreaseLike(postId);
    }
    @Override
    public void sharePost(Long postId){
        postRepository.increaseShareCount(postId);
    }
}
