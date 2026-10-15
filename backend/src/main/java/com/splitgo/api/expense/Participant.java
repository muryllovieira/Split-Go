package com.splitgo.api.expense;

import java.math.BigDecimal;
import java.util.UUID;

public record Participant(UUID memberId, BigDecimal value) {

}
