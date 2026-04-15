package com.jordy.studyoptimizer.github;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommitMatcherTest {

    @Test
    void detectsDayNumberFromKeywordOrHash() {
        assertThat(CommitMatcher.detectDayNumber("Reto 9 terminado")).hasValue(9);
        assertThat(CommitMatcher.detectDayNumber("Dia #12 - arrays")).hasValue(12);
        assertThat(CommitMatcher.detectDayNumber("#7 solucion")).hasValue(7);
    }

    @Test
    void ignoresBlankMessagesAndDayZero() {
        assertThat(CommitMatcher.detectDayNumber(null)).isEmpty();
        assertThat(CommitMatcher.detectDayNumber("")).isEmpty();
        assertThat(CommitMatcher.detectDayNumber("Reto 0 no existe en Study Optimizer")).isEmpty();
        assertThat(CommitMatcher.detectDayNumber("#0")).isEmpty();
    }
}
