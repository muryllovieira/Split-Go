package com.splitgo.api.settlement;

import java.math.BigDecimal;
import java.util.UUID;

public record Transfer(UUID fromMemberId, UUID toMemberId, BigDecimal amount) {
}