package chatterbox.services;

import chatterbox.dtos.request.CreateMultiplePostsDto;
import chatterbox.dtos.request.CreatePostRequestDto;
import chatterbox.dtos.response.GetPostByIdDto;
import chatterbox.entities.Post;
import chatterbox.entities.Users;
import chatterbox.repository.PostRepository;
import chatterbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private final String CACHE_NAME = "POST";

    public void createPost(CreatePostRequestDto createPostRequestDto) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users foundUser = userRepository.findById(userDetail.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found who did these post"));
        Post post = new Post();
        post.setContent(createPostRequestDto.getContent());
        post.setUrls(createPostRequestDto.getUrls());
        post.setUser(foundUser);
        repository.save(post);
    }

    @Cacheable(cacheNames = {CACHE_NAME}, key = "#id")
    public GetPostByIdDto getPost(Long id) {
        Post post = repository.findById(id).orElse(new Post());
        return modelMapper.map(post, GetPostByIdDto.class);
    }

    @Cacheable(cacheNames = {CACHE_NAME}, key = "{#pageNumber,#pageSize}")
    public List<GetPostByIdDto> getAllThePostOfALLUser(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Post> postList = repository.findAll(pageable);
        List<GetPostByIdDto> responseDto = postList.stream().map(post -> this.modelMapper.map(post, GetPostByIdDto.class)).collect(Collectors.toList());

        return responseDto;
    }

    @Cacheable(cacheNames = {CACHE_NAME}, key = "{#pageNumber,#pageSize,#postId}")
    public List<GetPostByIdDto> getAllThePostParticularUser(int pageNumber, int pageSize, Long postId) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<GetPostByIdDto> lst = repository.findPostsByUserIdUsingPagination(postId, pageSize, pageNumber * pageSize).stream().map((post) -> this.modelMapper.map(post, GetPostByIdDto.class)).collect(Collectors.toList());
        System.out.println(lst);
        return lst;
    }

    @Transactional
    public String saveAllPost(List<CreateMultiplePostsDto> dto) {

        List<Post> pst = dto.stream().map(data -> {
            Users user = userRepository.findById(data.getUserId()).orElse(new Users());
            Post post = modelMapper.map(data, Post.class);
            post.setUser(user);
            return post;
        }).collect(Collectors.toList());
        repository.saveAll(pst);
        return "Successfull saved all post";
    }

    public void deletePost(Long postId) {
        repository.deleteById(postId);
    }

    public void deleteEveryPostOfParticularUser(Long userId) {
        repository.deleteEveryPostOfUserByUserId(userId);
    }
}
