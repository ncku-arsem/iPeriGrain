package edu.ncku.store;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class NextPreviousListTest {

    @Test
    public void nextPreviousListTest(){
        NextPreviousList<Integer> list = new NextPreviousList<>(4);
        assertThat(list.isFull()).isFalse();
        assertThat(list.hasPrevious()).isFalse();
        assertThat(list.hasNext()).isFalse();

        list.add(1);
        assertThat(list.isFull()).isFalse();
        assertThat(list.hasNext()).isFalse();
        assertThat(list.hasPrevious()).isFalse();
        assertThat(list.previous()).isEmpty();
        assertThat(list.next()).isEmpty();

        list.add(2);
        assertThat(list.isFull()).isFalse();
        assertThat(list.hasNext()).isFalse();
        assertThat(list.hasPrevious()).isTrue();
        assertThat(list.next()).isEmpty();
        assertThat(list.next()).isEmpty();
        assertThat(list.previous()).isEqualTo(Optional.of(1));
        assertThat(list.hasPrevious()).isFalse();
        assertThat(list.previous()).isEmpty();
        assertThat(list.next()).isEqualTo(Optional.of(2));
        assertThat(list.hasNext()).isFalse();

        list.add(3);
        list.add(4);
        assertThat(list.isFull()).isTrue();
        assertThat(list.hasNext()).isFalse();
        assertThat(list.hasPrevious()).isTrue();
        assertThat(list.previous()).isEqualTo(Optional.of(3));
        assertThat(list.hasPrevious()).isTrue();
        assertThat(list.hasNext()).isTrue();
        assertThat(list.previous()).isEqualTo(Optional.of(2));
        assertThat(list.next()).isEqualTo(Optional.of(3));
        assertThat(list.next()).isEqualTo(Optional.of(4));
        assertThat(list.hasNext()).isFalse();
    }
}