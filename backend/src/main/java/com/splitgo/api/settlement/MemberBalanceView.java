package com.splitgo.api.settlement;

import java.math.BigDecimal;
import java.util.UUID;

public interface MemberBalanceView {

    UUID getMemberId();

    String getName();

    BigDecimal getPaid();

    BigDecimal getConsumed();

    BigDecimal getSent();

    BigDecimal getReceived();

    default BigDecimal getBalance() {
        return getPaid().subtract(getConsumed()).add(getSent()).subtract(getReceived());
    }
}