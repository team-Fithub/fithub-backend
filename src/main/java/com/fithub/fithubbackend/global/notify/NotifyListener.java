package com.fithub.fithubbackend.global.notify;

import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import com.fithub.fithubbackend.global.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotifyListener {

    private final NotifyService notifyService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) //커밋완료 후 작업
    @Transactional(propagation = Propagation.REQUIRES_NEW)// 새로운 트랜잭션으로 구성
    public void handleNotify(NotifyRequestDto dto) {
        notifyService.notifyByRequest(dto);
    }
}
