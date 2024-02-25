package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatRoomRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;

import java.util.List;

public interface ChatRoomService {

    /* ChatRoom 조회 */
    public ChatRoomResponseDto findById(final Long id);

    /* ChatRoom 목록 조회 - 최신순 */
    public List<ChatRoomResponseDto> findAllDesc();

    /* ChatRoom 생성 */
    public Long save(final ChatRoomRequestDto requestDto);

    /* ChatRoom 삭제 */
    public void delete(final Long id);

}
