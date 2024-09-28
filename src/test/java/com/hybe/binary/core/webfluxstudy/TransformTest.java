package com.hybe.binary.core.webfluxstudy;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

public class TransformTest {
    /**
     * Flux map() 은 인풋, 아웃풋 데이터의 순서를 유지할까?
     * 예상값: map() 은 동기적으로 동작하기 때문에 순서가 유지되어야한다.
     */
    @Test
    void map_동기_테스트1() {
        // given
        List<Integer> sequentialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> expect = List.of(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);

        // when
        Flux<Integer> mapFlux = Flux.fromIterable(sequentialList)
                .map(integer -> integer * 2);

        // then
        StepVerifier.create(mapFlux)
                .expectNextSequence(expect)
                .verifyComplete();
    }

    /**
     * flux의 flatMap()은 인풋, 아웃푼 순서를 유지하지 않을까?
     * 예상값: flatMap()은 비동기적으로 동작하기 때문에 순서를 유지하지 않아야한다.
     */
    @Test
    void flatMap_비동기_테스트1() {
        // given
        List<Integer> sequentialList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // when
        // 딜레이를 주는 이유: 비동기적으로 동작하면, 각 지연때문에 순서가 잘 섞일수 있음.
        Flux<Integer> delayedFlux = Flux.fromIterable(sequentialList)
                .flatMap(integer -> Flux.just(integer)
                        .delayElements(Duration.ofMillis(100L * (5 - integer))));

        // then
        StepVerifier.create(delayedFlux.collectList())
                .expectNextMatches(list -> !list.equals(sequentialList))
                .verifyComplete();
    }
}
