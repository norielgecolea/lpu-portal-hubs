package org.nors.dev.codes.lpu.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ServerTimeResponseTest {

    @Test
    void nowUsesCampusTime() {
        ServerTimeResponse body = ServerTimeResponse.now();

        assertEquals("Asia/Manila", body.zoneId());
        assertEquals("+08:00", body.utcOffset());
        assertTrue(Duration.between(body.serverTime(), Instant.now()).abs().toMillis() < 1_000);
    }
}
