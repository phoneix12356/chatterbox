package chatterbox.services;

public interface LikeService {

     void like(Long postId);

     void dislike(Long postId);

     void sharePost(Long postId);
}
